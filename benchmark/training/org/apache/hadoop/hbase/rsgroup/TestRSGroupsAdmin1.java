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


import RSGroupInfo.DEFAULT_GROUP;
import RSGroupInfo.NAMESPACE_DESC_PROP_GROUP;
import TableName.META_TABLE_NAME;
import TableNamespaceManager.KEY_MAX_REGIONS;
import TableNamespaceManager.KEY_MAX_TABLES;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.constraint.ConstraintException;
import org.apache.hadoop.hbase.net.Address;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static RSGroupInfo.DEFAULT_GROUP;


@Category({ MediumTests.class })
public class TestRSGroupsAdmin1 extends TestRSGroupsBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupsAdmin1.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestRSGroupsAdmin1.class);

    @Test
    public void testValidGroupNames() throws IOException {
        String[] badNames = new String[]{ "foo*", "foo@", "-" };
        String[] goodNames = new String[]{ "foo_123" };
        for (String entry : badNames) {
            try {
                TestRSGroupsBase.rsGroupAdmin.addRSGroup(entry);
                Assert.fail(("Expected a constraint exception for: " + entry));
            } catch (ConstraintException ex) {
                // expected
            }
        }
        for (String entry : goodNames) {
            TestRSGroupsBase.rsGroupAdmin.addRSGroup(entry);
        }
    }

    @Test
    public void testBogusArgs() throws Exception {
        Assert.assertNull(TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(TableName.valueOf("nonexistent")));
        Assert.assertNull(TestRSGroupsBase.rsGroupAdmin.getRSGroupOfServer(Address.fromParts("bogus", 123)));
        Assert.assertNull(TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("bogus"));
        try {
            TestRSGroupsBase.rsGroupAdmin.removeRSGroup("bogus");
            Assert.fail("Expected removing bogus group to fail");
        } catch (ConstraintException ex) {
            // expected
        }
        try {
            TestRSGroupsBase.rsGroupAdmin.moveTables(Sets.newHashSet(TableName.valueOf("bogustable")), "bogus");
            Assert.fail("Expected move with bogus group to fail");
        } catch (ConstraintException | TableNotFoundException ex) {
            // expected
        }
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServers(Sets.newHashSet(Address.fromParts("bogus", 123)), "bogus");
            Assert.fail("Expected move with bogus group to fail");
        } catch (ConstraintException ex) {
            // expected
        }
        try {
            TestRSGroupsBase.admin.balancerSwitch(true, true);
            TestRSGroupsBase.rsGroupAdmin.balanceRSGroup("bogus");
            TestRSGroupsBase.admin.balancerSwitch(false, true);
            Assert.fail("Expected move with bogus group to fail");
        } catch (ConstraintException ex) {
            // expected
        }
    }

    @Test
    public void testNamespaceConstraint() throws Exception {
        String nsName = (TestRSGroupsBase.tablePrefix) + "_foo";
        String groupName = (TestRSGroupsBase.tablePrefix) + "_foo";
        TestRSGroupsAdmin1.LOG.info("testNamespaceConstraint");
        TestRSGroupsBase.rsGroupAdmin.addRSGroup(groupName);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preAddRSGroupCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postAddRSGroupCalled);
        TestRSGroupsBase.admin.createNamespace(NamespaceDescriptor.create(nsName).addConfiguration(NAMESPACE_DESC_PROP_GROUP, groupName).build());
        // test removing a referenced group
        try {
            TestRSGroupsBase.rsGroupAdmin.removeRSGroup(groupName);
            Assert.fail("Expected a constraint exception");
        } catch (IOException ex) {
        }
        // test modify group
        // changing with the same name is fine
        TestRSGroupsBase.admin.modifyNamespace(NamespaceDescriptor.create(nsName).addConfiguration(NAMESPACE_DESC_PROP_GROUP, groupName).build());
        String anotherGroup = (TestRSGroupsBase.tablePrefix) + "_anotherGroup";
        TestRSGroupsBase.rsGroupAdmin.addRSGroup(anotherGroup);
        // test add non-existent group
        TestRSGroupsBase.admin.deleteNamespace(nsName);
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(groupName);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preRemoveRSGroupCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postRemoveRSGroupCalled);
        try {
            TestRSGroupsBase.admin.createNamespace(NamespaceDescriptor.create(nsName).addConfiguration(NAMESPACE_DESC_PROP_GROUP, "foo").build());
            Assert.fail("Expected a constraint exception");
        } catch (IOException ex) {
        }
    }

    @Test
    public void testGroupInfoMultiAccessing() throws Exception {
        RSGroupInfoManager manager = TestRSGroupsBase.rsGroupAdminEndpoint.getGroupInfoManager();
        RSGroupInfo defaultGroup = manager.getRSGroup("default");
        // getRSGroup updates default group's server list
        // this process must not affect other threads iterating the list
        Iterator<Address> it = defaultGroup.getServers().iterator();
        manager.getRSGroup("default");
        it.next();
    }

    @Test
    public void testGetRSGroupInfoCPHookCalled() throws Exception {
        TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preGetRSGroupInfoCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postGetRSGroupInfoCalled);
    }

    @Test
    public void testGetRSGroupInfoOfTableCPHookCalled() throws Exception {
        TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(META_TABLE_NAME);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preGetRSGroupInfoOfTableCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postGetRSGroupInfoOfTableCalled);
    }

    @Test
    public void testListRSGroupsCPHookCalled() throws Exception {
        TestRSGroupsBase.rsGroupAdmin.listRSGroups();
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preListRSGroupsCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postListRSGroupsCalled);
    }

    @Test
    public void testGetRSGroupInfoOfServerCPHookCalled() throws Exception {
        ServerName masterServerName = getMaster().getServerName();
        TestRSGroupsBase.rsGroupAdmin.getRSGroupOfServer(masterServerName.getAddress());
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preGetRSGroupInfoOfServerCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postGetRSGroupInfoOfServerCalled);
    }

    @Test
    public void testFailRemoveGroup() throws IOException, InterruptedException {
        int initNumGroups = TestRSGroupsBase.rsGroupAdmin.listRSGroups().size();
        addGroup("bar", 3);
        TestRSGroupsBase.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"));
        TestRSGroupsBase.rsGroupAdmin.moveTables(Sets.newHashSet(tableName), "bar");
        RSGroupInfo barGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("bar");
        // group is not empty therefore it should fail
        try {
            TestRSGroupsBase.rsGroupAdmin.removeRSGroup(barGroup.getName());
            Assert.fail("Expected remove group to fail");
        } catch (IOException e) {
        }
        // group cannot lose all it's servers therefore it should fail
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServers(barGroup.getServers(), DEFAULT_GROUP);
            Assert.fail("Expected move servers to fail");
        } catch (IOException e) {
        }
        TestRSGroupsBase.rsGroupAdmin.moveTables(barGroup.getTables(), DEFAULT_GROUP);
        try {
            TestRSGroupsBase.rsGroupAdmin.removeRSGroup(barGroup.getName());
            Assert.fail("Expected move servers to fail");
        } catch (IOException e) {
        }
        TestRSGroupsBase.rsGroupAdmin.moveServers(barGroup.getServers(), DEFAULT_GROUP);
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(barGroup.getName());
        Assert.assertEquals(initNumGroups, TestRSGroupsBase.rsGroupAdmin.listRSGroups().size());
    }

    @Test
    public void testMultiTableMove() throws Exception {
        final TableName tableNameA = TableName.valueOf((((TestRSGroupsBase.tablePrefix) + (name.getMethodName())) + "A"));
        final TableName tableNameB = TableName.valueOf((((TestRSGroupsBase.tablePrefix) + (name.getMethodName())) + "B"));
        final byte[] familyNameBytes = Bytes.toBytes("f");
        String newGroupName = getGroupName(name.getMethodName());
        final RSGroupInfo newGroup = addGroup(newGroupName, 1);
        TestRSGroupsBase.TEST_UTIL.createTable(tableNameA, familyNameBytes);
        TestRSGroupsBase.TEST_UTIL.createTable(tableNameB, familyNameBytes);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                List<String> regionsA = getTableRegionMap().get(tableNameA);
                if (regionsA == null) {
                    return false;
                }
                List<String> regionsB = getTableRegionMap().get(tableNameB);
                if (regionsB == null) {
                    return false;
                }
                return ((getTableRegionMap().get(tableNameA).size()) >= 1) && ((getTableRegionMap().get(tableNameB).size()) >= 1);
            }
        });
        RSGroupInfo tableGrpA = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableNameA);
        Assert.assertTrue(tableGrpA.getName().equals(DEFAULT_GROUP));
        RSGroupInfo tableGrpB = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableNameB);
        Assert.assertTrue(tableGrpB.getName().equals(DEFAULT_GROUP));
        // change table's group
        TestRSGroupsAdmin1.LOG.info(((((("Moving table [" + tableNameA) + ",") + tableNameB) + "] to ") + (newGroup.getName())));
        TestRSGroupsBase.rsGroupAdmin.moveTables(Sets.newHashSet(tableNameA, tableNameB), newGroup.getName());
        // verify group change
        Assert.assertEquals(newGroup.getName(), TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableNameA).getName());
        Assert.assertEquals(newGroup.getName(), TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableNameB).getName());
        // verify tables' not exist in old group
        Set<TableName> DefaultTables = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getTables();
        Assert.assertFalse(DefaultTables.contains(tableNameA));
        Assert.assertFalse(DefaultTables.contains(tableNameB));
        // verify tables' exist in new group
        Set<TableName> newGroupTables = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroupName).getTables();
        Assert.assertTrue(newGroupTables.contains(tableNameA));
        Assert.assertTrue(newGroupTables.contains(tableNameB));
    }

    @Test
    public void testTableMoveTruncateAndDrop() throws Exception {
        final byte[] familyNameBytes = Bytes.toBytes("f");
        String newGroupName = getGroupName(name.getMethodName());
        final RSGroupInfo newGroup = addGroup(newGroupName, 2);
        TestRSGroupsBase.TEST_UTIL.createMultiRegionTable(tableName, familyNameBytes, 5);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                List<String> regions = getTableRegionMap().get(tableName);
                if (regions == null) {
                    return false;
                }
                return (getTableRegionMap().get(tableName).size()) >= 5;
            }
        });
        RSGroupInfo tableGrp = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName);
        Assert.assertTrue(tableGrp.getName().equals(DEFAULT_GROUP));
        // change table's group
        TestRSGroupsAdmin1.LOG.info(((("Moving table " + (tableName)) + " to ") + (newGroup.getName())));
        TestRSGroupsBase.rsGroupAdmin.moveTables(Sets.newHashSet(tableName), newGroup.getName());
        // verify group change
        Assert.assertEquals(newGroup.getName(), TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName).getName());
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                Map<ServerName, List<String>> serverMap = getTableServerRegionMap().get(tableName);
                int count = 0;
                if (serverMap != null) {
                    for (ServerName rs : serverMap.keySet()) {
                        if (newGroup.containsServer(rs.getAddress())) {
                            count += serverMap.get(rs).size();
                        }
                    }
                }
                return count == 5;
            }
        });
        // test truncate
        TestRSGroupsBase.admin.disableTable(tableName);
        TestRSGroupsBase.admin.truncateTable(tableName, true);
        Assert.assertEquals(1, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getTables().size());
        Assert.assertEquals(tableName, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getTables().first());
        // verify removed table is removed from group
        TestRSGroupsBase.TEST_UTIL.deleteTable(tableName);
        Assert.assertEquals(0, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getTables().size());
        Assert.assertTrue(TestRSGroupsAdmin1.observer.preMoveTablesCalled);
        Assert.assertTrue(TestRSGroupsAdmin1.observer.postMoveTablesCalled);
    }

    @Test
    public void testDisabledTableMove() throws Exception {
        final byte[] familyNameBytes = Bytes.toBytes("f");
        String newGroupName = getGroupName(name.getMethodName());
        final RSGroupInfo newGroup = addGroup(newGroupName, 2);
        TestRSGroupsBase.TEST_UTIL.createMultiRegionTable(tableName, familyNameBytes, 5);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                List<String> regions = getTableRegionMap().get(tableName);
                if (regions == null) {
                    return false;
                }
                return (getTableRegionMap().get(tableName).size()) >= 5;
            }
        });
        RSGroupInfo tableGrp = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName);
        Assert.assertTrue(tableGrp.getName().equals(DEFAULT_GROUP));
        // test disable table
        TestRSGroupsBase.admin.disableTable(tableName);
        // change table's group
        TestRSGroupsAdmin1.LOG.info(((("Moving table " + (tableName)) + " to ") + (newGroup.getName())));
        TestRSGroupsBase.rsGroupAdmin.moveTables(Sets.newHashSet(tableName), newGroup.getName());
        // verify group change
        Assert.assertEquals(newGroup.getName(), TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName).getName());
    }

    @Test
    public void testNonExistentTableMove() throws Exception {
        TableName tableName = TableName.valueOf(((TestRSGroupsBase.tablePrefix) + (name.getMethodName())));
        RSGroupInfo tableGrp = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName);
        Assert.assertNull(tableGrp);
        // test if table exists already.
        boolean exist = TestRSGroupsBase.admin.tableExists(tableName);
        Assert.assertFalse(exist);
        TestRSGroupsAdmin1.LOG.info(((("Moving table " + tableName) + " to ") + (DEFAULT_GROUP)));
        try {
            TestRSGroupsBase.rsGroupAdmin.moveTables(Sets.newHashSet(tableName), DEFAULT_GROUP);
            Assert.fail((("Table " + tableName) + " shouldn't have been successfully moved."));
        } catch (IOException ex) {
            Assert.assertTrue((ex instanceof TableNotFoundException));
        }
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServersAndTables(Sets.newHashSet(Address.fromParts("bogus", 123)), Sets.newHashSet(tableName), DEFAULT_GROUP);
            Assert.fail((("Table " + tableName) + " shouldn't have been successfully moved."));
        } catch (IOException ex) {
            Assert.assertTrue((ex instanceof TableNotFoundException));
        }
        // verify group change
        Assert.assertNull(TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName));
    }

    @Test
    public void testRSGroupListDoesNotContainFailedTableCreation() throws Exception {
        toggleQuotaCheckAndRestartMiniCluster(true);
        String nsp = "np1";
        NamespaceDescriptor nspDesc = NamespaceDescriptor.create(nsp).addConfiguration(KEY_MAX_REGIONS, "5").addConfiguration(KEY_MAX_TABLES, "2").build();
        TestRSGroupsBase.admin.createNamespace(nspDesc);
        Assert.assertEquals(3, TestRSGroupsBase.admin.listNamespaceDescriptors().length);
        ColumnFamilyDescriptor fam1 = ColumnFamilyDescriptorBuilder.of("fam1");
        TableDescriptor tableDescOne = TableDescriptorBuilder.newBuilder(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table1"))).setColumnFamily(fam1).build();
        TestRSGroupsBase.admin.createTable(tableDescOne);
        TableDescriptor tableDescTwo = TableDescriptorBuilder.newBuilder(TableName.valueOf(((nsp + (TableName.NAMESPACE_DELIM)) + "table2"))).setColumnFamily(fam1).build();
        boolean constraintViolated = false;
        try {
            TestRSGroupsBase.admin.createTable(tableDescTwo, Bytes.toBytes("AAA"), Bytes.toBytes("ZZZ"), 6);
            Assert.fail("Creation table should fail because of quota violation.");
        } catch (Exception exp) {
            Assert.assertTrue((exp instanceof IOException));
            constraintViolated = true;
        } finally {
            Assert.assertTrue(("Constraint not violated for table " + (tableDescTwo.getTableName())), constraintViolated);
        }
        java.util.List<RSGroupInfo> rsGroupInfoList = TestRSGroupsBase.rsGroupAdmin.listRSGroups();
        boolean foundTable2 = false;
        boolean foundTable1 = false;
        for (int i = 0; i < (rsGroupInfoList.size()); i++) {
            if (rsGroupInfoList.get(i).getTables().contains(tableDescTwo.getTableName())) {
                foundTable2 = true;
            }
            if (rsGroupInfoList.get(i).getTables().contains(tableDescOne.getTableName())) {
                foundTable1 = true;
            }
        }
        Assert.assertFalse("Found table2 in rsgroup list.", foundTable2);
        Assert.assertTrue("Did not find table1 in rsgroup list", foundTable1);
        TestRSGroupsBase.TEST_UTIL.deleteTable(tableDescOne.getTableName());
        TestRSGroupsBase.admin.deleteNamespace(nspDesc.getName());
        toggleQuotaCheckAndRestartMiniCluster(false);
    }

    @Test
    public void testNotMoveTableToNullRSGroupWhenCreatingExistingTable() throws Exception {
        // Trigger
        TableName tn1 = TableName.valueOf("t1");
        TestRSGroupsBase.TEST_UTIL.createTable(tn1, "cf1");
        try {
            // Create an existing table to trigger HBASE-21866
            TestRSGroupsBase.TEST_UTIL.createTable(tn1, "cf1");
        } catch (TableExistsException teex) {
            // Ignore
        }
        // Wait then verify
        // Could not verify until the rollback of CreateTableProcedure is done
        // (that is, the coprocessor finishes its work),
        // or the table is still in the "default" rsgroup even though HBASE-21866
        // is not fixed.
        TestRSGroupsBase.TEST_UTIL.waitFor(5000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestRSGroupsBase.master.getMasterProcedureExecutor().getActiveExecutorCount()) == 0;
            }
        });
        SortedSet<TableName> tables = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getTables();
        Assert.assertTrue("Table 't1' must be in 'default' rsgroup", tables.contains(tn1));
        // Cleanup
        TestRSGroupsBase.TEST_UTIL.deleteTable(tn1);
    }
}

