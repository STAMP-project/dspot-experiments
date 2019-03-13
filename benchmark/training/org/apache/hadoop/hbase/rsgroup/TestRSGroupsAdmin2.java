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


import Option.LIVE_SERVERS;
import RSGroupAdminServer.KEEP_ONE_SERVER_IN_DEFAULT_ERROR_MESSAGE;
import RSGroupInfo.DEFAULT_GROUP;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.RegionInfo;
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
public class TestRSGroupsAdmin2 extends TestRSGroupsBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupsAdmin2.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestRSGroupsAdmin2.class);

    @Test
    public void testRegionMove() throws Exception {
        final RSGroupInfo newGroup = addGroup(getGroupName(name.getMethodName()), 1);
        final byte[] familyNameBytes = Bytes.toBytes("f");
        // All the regions created below will be assigned to the default group.
        TestRSGroupsBase.TEST_UTIL.createMultiRegionTable(tableName, familyNameBytes, 6);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                List<String> regions = getTableRegionMap().get(tableName);
                if (regions == null) {
                    return false;
                }
                return (getTableRegionMap().get(tableName).size()) >= 6;
            }
        });
        // get target region to move
        Map<ServerName, java.util.List<String>> assignMap = getTableServerRegionMap().get(tableName);
        String targetRegion = null;
        for (ServerName server : assignMap.keySet()) {
            targetRegion = ((assignMap.get(server).size()) > 0) ? assignMap.get(server).get(0) : null;
            if (targetRegion != null) {
                break;
            }
        }
        // get server which is not a member of new group
        ServerName tmpTargetServer = null;
        for (ServerName server : TestRSGroupsBase.admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet()) {
            if (!(newGroup.containsServer(server.getAddress()))) {
                tmpTargetServer = server;
                break;
            }
        }
        final ServerName targetServer = tmpTargetServer;
        // move target server to group
        TestRSGroupsBase.rsGroupAdmin.moveServers(Sets.newHashSet(targetServer.getAddress()), newGroup.getName());
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestRSGroupsBase.admin.getRegions(targetServer).size()) <= 0;
            }
        });
        // Lets move this region to the new group.
        TestRSGroupsBase.TEST_UTIL.getAdmin().move(Bytes.toBytes(RegionInfo.encodeRegionName(Bytes.toBytes(targetRegion))), Bytes.toBytes(targetServer.getServerName()));
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (((getTableRegionMap().get(tableName)) != null) && ((getTableRegionMap().get(tableName).size()) == 6)) && ((TestRSGroupsBase.admin.getClusterMetrics(EnumSet.of(Option.REGIONS_IN_TRANSITION)).getRegionStatesInTransition().size()) < 1);
            }
        });
        // verify that targetServer didn't open it
        for (RegionInfo region : TestRSGroupsBase.admin.getRegions(targetServer)) {
            if (targetRegion.equals(region.getRegionNameAsString())) {
                Assert.fail("Target server opened region");
            }
        }
    }

    @Test
    public void testRegionServerMove() throws IOException, InterruptedException {
        int initNumGroups = TestRSGroupsBase.rsGroupAdmin.listRSGroups().size();
        RSGroupInfo appInfo = addGroup(getGroupName(name.getMethodName()), 1);
        RSGroupInfo adminInfo = addGroup(getGroupName(name.getMethodName()), 1);
        RSGroupInfo dInfo = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP);
        Assert.assertEquals((initNumGroups + 2), TestRSGroupsBase.rsGroupAdmin.listRSGroups().size());
        Assert.assertEquals(1, adminInfo.getServers().size());
        Assert.assertEquals(1, appInfo.getServers().size());
        Assert.assertEquals(((getNumServers()) - 2), dInfo.getServers().size());
        TestRSGroupsBase.rsGroupAdmin.moveServers(appInfo.getServers(), DEFAULT_GROUP);
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(appInfo.getName());
        TestRSGroupsBase.rsGroupAdmin.moveServers(adminInfo.getServers(), DEFAULT_GROUP);
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(adminInfo.getName());
        Assert.assertEquals(TestRSGroupsBase.rsGroupAdmin.listRSGroups().size(), initNumGroups);
    }

    @Test
    public void testMoveServers() throws Exception {
        // create groups and assign servers
        addGroup("bar", 3);
        TestRSGroupsBase.rsGroupAdmin.addRSGroup("foo");
        RSGroupInfo barGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("bar");
        RSGroupInfo fooGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("foo");
        Assert.assertEquals(3, barGroup.getServers().size());
        Assert.assertEquals(0, fooGroup.getServers().size());
        // test fail bogus server move
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServers(Sets.newHashSet(Address.fromString("foo:9999")), "foo");
            Assert.fail("Bogus servers shouldn't have been successfully moved.");
        } catch (IOException ex) {
            String exp = "Source RSGroup for server foo:9999 does not exist.";
            String msg = ("Expected '" + exp) + "' in exception message: ";
            Assert.assertTrue(((msg + " ") + (ex.getMessage())), ex.getMessage().contains(exp));
        }
        // test success case
        TestRSGroupsAdmin2.LOG.info((("moving servers " + (barGroup.getServers())) + " to group foo"));
        TestRSGroupsBase.rsGroupAdmin.moveServers(barGroup.getServers(), fooGroup.getName());
        barGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("bar");
        fooGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("foo");
        Assert.assertEquals(0, barGroup.getServers().size());
        Assert.assertEquals(3, fooGroup.getServers().size());
        TestRSGroupsAdmin2.LOG.info((("moving servers " + (fooGroup.getServers())) + " to group default"));
        TestRSGroupsBase.rsGroupAdmin.moveServers(fooGroup.getServers(), DEFAULT_GROUP);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getNumServers()) == (TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(RSGroupInfo.DEFAULT_GROUP).getServers().size());
            }
        });
        fooGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("foo");
        Assert.assertEquals(0, fooGroup.getServers().size());
        // test group removal
        TestRSGroupsAdmin2.LOG.info(("Remove group " + (barGroup.getName())));
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(barGroup.getName());
        Assert.assertEquals(null, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(barGroup.getName()));
        TestRSGroupsAdmin2.LOG.info(("Remove group " + (fooGroup.getName())));
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(fooGroup.getName());
        Assert.assertEquals(null, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(fooGroup.getName()));
    }

    @Test
    public void testRemoveServers() throws Exception {
        TestRSGroupsAdmin2.LOG.info("testRemoveServers");
        final RSGroupInfo newGroup = addGroup(getGroupName(name.getMethodName()), 3);
        Iterator<Address> iterator = newGroup.getServers().iterator();
        ServerName targetServer = getServerName(iterator.next());
        // remove online servers
        try {
            TestRSGroupsBase.rsGroupAdmin.removeServers(Sets.newHashSet(targetServer.getAddress()));
            Assert.fail("Online servers shouldn't have been successfully removed.");
        } catch (IOException ex) {
            String exp = ("Server " + (targetServer.getAddress())) + " is an online server, not allowed to remove.";
            String msg = ("Expected '" + exp) + "' in exception message: ";
            Assert.assertTrue(((msg + " ") + (ex.getMessage())), ex.getMessage().contains(exp));
        }
        Assert.assertTrue(newGroup.getServers().contains(targetServer.getAddress()));
        // remove dead servers
        TestRSGroupsBase.NUM_DEAD_SERVERS = TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().size();
        try {
            // stopping may cause an exception
            // due to the connection loss
            TestRSGroupsAdmin2.LOG.info(("stopping server " + (targetServer.getServerName())));
            TestRSGroupsBase.admin.stopRegionServer(targetServer.getAddress().toString());
            (TestRSGroupsBase.NUM_DEAD_SERVERS)++;
        } catch (Exception e) {
        }
        // wait for stopped regionserver to dead server list
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (!(TestRSGroupsBase.master.getServerManager().areDeadServersInProgress())) && ((TestRSGroupsBase.cluster.getClusterMetrics().getDeadServerNames().size()) == (TestRSGroupsBase.NUM_DEAD_SERVERS));
            }
        });
        try {
            TestRSGroupsBase.rsGroupAdmin.removeServers(Sets.newHashSet(targetServer.getAddress()));
            Assert.fail("Dead servers shouldn't have been successfully removed.");
        } catch (IOException ex) {
            String exp = (("Server " + (targetServer.getAddress())) + " is on the dead servers list,") + " Maybe it will come back again, not allowed to remove.";
            String msg = ("Expected '" + exp) + "' in exception message: ";
            Assert.assertTrue(((msg + " ") + (ex.getMessage())), ex.getMessage().contains(exp));
        }
        Assert.assertTrue(newGroup.getServers().contains(targetServer.getAddress()));
        // remove decommissioned servers
        java.util.List<ServerName> serversToDecommission = new ArrayList<>();
        targetServer = getServerName(iterator.next());
        Assert.assertTrue(TestRSGroupsBase.master.getServerManager().getOnlineServers().containsKey(targetServer));
        serversToDecommission.add(targetServer);
        TestRSGroupsBase.admin.decommissionRegionServers(serversToDecommission, true);
        Assert.assertEquals(1, TestRSGroupsBase.admin.listDecommissionedRegionServers().size());
        Assert.assertTrue(newGroup.getServers().contains(targetServer.getAddress()));
        TestRSGroupsBase.rsGroupAdmin.removeServers(Sets.newHashSet(targetServer.getAddress()));
        Set<Address> newGroupServers = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getServers();
        Assert.assertFalse(newGroupServers.contains(targetServer.getAddress()));
        Assert.assertEquals(2, newGroupServers.size());
        Assert.assertTrue(TestRSGroupsAdmin2.observer.preRemoveServersCalled);
        Assert.assertTrue(TestRSGroupsAdmin2.observer.postRemoveServersCalled);
    }

    @Test
    public void testMoveServersAndTables() throws Exception {
        TestRSGroupsAdmin2.LOG.info("testMoveServersAndTables");
        final RSGroupInfo newGroup = addGroup(getGroupName(name.getMethodName()), 1);
        // create table
        final byte[] familyNameBytes = Bytes.toBytes("f");
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
        // get server which is not a member of new group
        ServerName targetServer = null;
        for (ServerName server : TestRSGroupsBase.admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet()) {
            if ((!(newGroup.containsServer(server.getAddress()))) && (!(TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("master").containsServer(server.getAddress())))) {
                targetServer = server;
                break;
            }
        }
        TestRSGroupsAdmin2.LOG.debug(("Print group info : " + (TestRSGroupsBase.rsGroupAdmin.listRSGroups())));
        int oldDefaultGroupServerSize = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getServers().size();
        int oldDefaultGroupTableSize = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getTables().size();
        // test fail bogus server move
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServersAndTables(Sets.newHashSet(Address.fromString("foo:9999")), Sets.newHashSet(tableName), newGroup.getName());
            Assert.fail("Bogus servers shouldn't have been successfully moved.");
        } catch (IOException ex) {
            String exp = "Source RSGroup for server foo:9999 does not exist.";
            String msg = ("Expected '" + exp) + "' in exception message: ";
            Assert.assertTrue(((msg + " ") + (ex.getMessage())), ex.getMessage().contains(exp));
        }
        // test fail server move
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServersAndTables(Sets.newHashSet(targetServer.getAddress()), Sets.newHashSet(tableName), DEFAULT_GROUP);
            Assert.fail("servers shouldn't have been successfully moved.");
        } catch (IOException ex) {
            String exp = ((("Target RSGroup " + (DEFAULT_GROUP)) + " is same as source ") + (DEFAULT_GROUP)) + " RSGroup.";
            String msg = ("Expected '" + exp) + "' in exception message: ";
            Assert.assertTrue(((msg + " ") + (ex.getMessage())), ex.getMessage().contains(exp));
        }
        // verify default group info
        Assert.assertEquals(oldDefaultGroupServerSize, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getServers().size());
        Assert.assertEquals(oldDefaultGroupTableSize, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getTables().size());
        // verify new group info
        Assert.assertEquals(1, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getServers().size());
        Assert.assertEquals(0, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getTables().size());
        // get all region to move targetServer
        java.util.List<String> regionList = getTableRegionMap().get(tableName);
        for (String region : regionList) {
            // Lets move this region to the targetServer
            TestRSGroupsBase.TEST_UTIL.getAdmin().move(Bytes.toBytes(RegionInfo.encodeRegionName(Bytes.toBytes(region))), Bytes.toBytes(targetServer.getServerName()));
        }
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return ((((getTableRegionMap().get(tableName)) != null) && ((getTableRegionMap().get(tableName).size()) == 5)) && ((getTableServerRegionMap().get(tableName).size()) == 1)) && ((TestRSGroupsBase.admin.getClusterMetrics(EnumSet.of(Option.REGIONS_IN_TRANSITION)).getRegionStatesInTransition().size()) < 1);
            }
        });
        // verify that all region move to targetServer
        Assert.assertEquals(5, getTableServerRegionMap().get(tableName).get(targetServer).size());
        // move targetServer and table to newGroup
        TestRSGroupsAdmin2.LOG.info("moving server and table to newGroup");
        TestRSGroupsBase.rsGroupAdmin.moveServersAndTables(Sets.newHashSet(targetServer.getAddress()), Sets.newHashSet(tableName), newGroup.getName());
        // verify group change
        Assert.assertEquals(newGroup.getName(), TestRSGroupsBase.rsGroupAdmin.getRSGroupInfoOfTable(tableName).getName());
        // verify servers' not exist in old group
        Set<Address> defaultServers = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getServers();
        Assert.assertFalse(defaultServers.contains(targetServer.getAddress()));
        // verify servers' exist in new group
        Set<Address> newGroupServers = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getServers();
        Assert.assertTrue(newGroupServers.contains(targetServer.getAddress()));
        // verify tables' not exist in old group
        Set<TableName> defaultTables = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getTables();
        Assert.assertFalse(defaultTables.contains(tableName));
        // verify tables' exist in new group
        Set<TableName> newGroupTables = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(newGroup.getName()).getTables();
        Assert.assertTrue(newGroupTables.contains(tableName));
        // verify that all region still assgin on targetServer
        Assert.assertEquals(5, getTableServerRegionMap().get(tableName).get(targetServer).size());
        Assert.assertTrue(TestRSGroupsAdmin2.observer.preMoveServersAndTables);
        Assert.assertTrue(TestRSGroupsAdmin2.observer.postMoveServersAndTables);
    }

    @Test
    public void testMoveServersFromDefaultGroup() throws Exception {
        // create groups and assign servers
        TestRSGroupsBase.rsGroupAdmin.addRSGroup("foo");
        RSGroupInfo fooGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("foo");
        Assert.assertEquals(0, fooGroup.getServers().size());
        RSGroupInfo defaultGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP);
        // test remove all servers from default
        try {
            TestRSGroupsBase.rsGroupAdmin.moveServers(defaultGroup.getServers(), fooGroup.getName());
            Assert.fail(KEEP_ONE_SERVER_IN_DEFAULT_ERROR_MESSAGE);
        } catch (ConstraintException ex) {
            Assert.assertTrue(ex.getMessage().contains(KEEP_ONE_SERVER_IN_DEFAULT_ERROR_MESSAGE));
        }
        // test success case, remove one server from default ,keep at least one server
        if ((defaultGroup.getServers().size()) > 1) {
            Address serverInDefaultGroup = defaultGroup.getServers().iterator().next();
            TestRSGroupsAdmin2.LOG.info(((("moving server " + serverInDefaultGroup) + " from group default to group ") + (fooGroup.getName())));
            TestRSGroupsBase.rsGroupAdmin.moveServers(Sets.newHashSet(serverInDefaultGroup), fooGroup.getName());
        }
        fooGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("foo");
        TestRSGroupsAdmin2.LOG.info((("moving servers " + (fooGroup.getServers())) + " to group default"));
        TestRSGroupsBase.rsGroupAdmin.moveServers(fooGroup.getServers(), DEFAULT_GROUP);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (getNumServers()) == (TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(RSGroupInfo.DEFAULT_GROUP).getServers().size());
            }
        });
        fooGroup = TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo("foo");
        Assert.assertEquals(0, fooGroup.getServers().size());
        // test group removal
        TestRSGroupsAdmin2.LOG.info(("Remove group " + (fooGroup.getName())));
        TestRSGroupsBase.rsGroupAdmin.removeRSGroup(fooGroup.getName());
        Assert.assertEquals(null, TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(fooGroup.getName()));
    }
}

