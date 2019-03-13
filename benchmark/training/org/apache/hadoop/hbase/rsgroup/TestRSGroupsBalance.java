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
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class })
public class TestRSGroupsBalance extends TestRSGroupsBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupsBalance.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestRSGroupsBalance.class);

    @Test
    public void testGroupBalance() throws Exception {
        TestRSGroupsBalance.LOG.info(name.getMethodName());
        String newGroupName = getGroupName(name.getMethodName());
        addGroup(newGroupName, 3);
        final TableName tableName = TableName.valueOf(((TestRSGroupsBase.tablePrefix) + "_ns"), name.getMethodName());
        TestRSGroupsBase.admin.createNamespace(NamespaceDescriptor.create(tableName.getNamespaceAsString()).addConfiguration(NAMESPACE_DESC_PROP_GROUP, newGroupName).build());
        final TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of("f")).build();
        byte[] startKey = Bytes.toBytes("aaaaa");
        byte[] endKey = Bytes.toBytes("zzzzz");
        TestRSGroupsBase.admin.createTable(desc, startKey, endKey, 6);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                List<String> regions = getTableRegionMap().get(tableName);
                if (regions == null) {
                    return false;
                }
                return (regions.size()) >= 6;
            }
        });
        // make assignment uneven, move all regions to one server
        Map<ServerName, java.util.List<String>> assignMap = getTableServerRegionMap().get(tableName);
        final ServerName first = assignMap.entrySet().iterator().next().getKey();
        for (RegionInfo region : TestRSGroupsBase.admin.getRegions(tableName)) {
            if (!(assignMap.get(first).contains(region.getRegionNameAsString()))) {
                TestRSGroupsBase.admin.move(region.getEncodedNameAsBytes(), Bytes.toBytes(first.getServerName()));
            }
        }
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                Map<ServerName, List<String>> map = getTableServerRegionMap().get(tableName);
                if (map == null) {
                    return true;
                }
                List<String> regions = map.get(first);
                if (regions == null) {
                    return true;
                }
                return (regions.size()) >= 6;
            }
        });
        // balance the other group and make sure it doesn't affect the new group
        TestRSGroupsBase.admin.balancerSwitch(true, true);
        TestRSGroupsBase.rsGroupAdmin.balanceRSGroup(DEFAULT_GROUP);
        Assert.assertEquals(6, getTableServerRegionMap().get(tableName).get(first).size());
        // disable balance, balancer will not be run and return false
        TestRSGroupsBase.admin.balancerSwitch(false, true);
        Assert.assertFalse(TestRSGroupsBase.rsGroupAdmin.balanceRSGroup(newGroupName));
        Assert.assertEquals(6, getTableServerRegionMap().get(tableName).get(first).size());
        // enable balance
        TestRSGroupsBase.admin.balancerSwitch(true, true);
        TestRSGroupsBase.rsGroupAdmin.balanceRSGroup(newGroupName);
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                for (List<String> regions : getTableServerRegionMap().get(tableName).values()) {
                    if (2 != (regions.size())) {
                        return false;
                    }
                }
                return true;
            }
        });
        TestRSGroupsBase.admin.balancerSwitch(false, true);
    }

    @Test
    public void testMisplacedRegions() throws Exception {
        final TableName tableName = TableName.valueOf(((TestRSGroupsBase.tablePrefix) + "_testMisplacedRegions"));
        TestRSGroupsBalance.LOG.info("testMisplacedRegions");
        final RSGroupInfo RSGroupInfo = addGroup("testMisplacedRegions", 1);
        TestRSGroupsBase.TEST_UTIL.createMultiRegionTable(tableName, new byte[]{ 'f' }, 15);
        TestRSGroupsBase.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        TestRSGroupsBase.rsGroupAdminEndpoint.getGroupInfoManager().moveTables(Sets.newHashSet(tableName), RSGroupInfo.getName());
        TestRSGroupsBase.admin.balancerSwitch(true, true);
        Assert.assertTrue(TestRSGroupsBase.rsGroupAdmin.balanceRSGroup(RSGroupInfo.getName()));
        TestRSGroupsBase.admin.balancerSwitch(false, true);
        Assert.assertTrue(TestRSGroupsBalance.observer.preBalanceRSGroupCalled);
        Assert.assertTrue(TestRSGroupsBalance.observer.postBalanceRSGroupCalled);
        TestRSGroupsBase.TEST_UTIL.waitFor(60000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                ServerName serverName = ServerName.valueOf(RSGroupInfo.getServers().iterator().next().toString(), 1);
                return (TestRSGroupsBase.admin.getConnection().getAdmin().getRegions(serverName).size()) == 15;
            }
        });
    }
}

