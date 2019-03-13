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
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class })
public class TestRSGroupsKillRS extends TestRSGroupsBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupsKillRS.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestRSGroupsKillRS.class);

    @Test
    public void testKillRS() throws Exception {
        RSGroupInfo appInfo = addGroup("appInfo", 1);
        final TableName tableName = TableName.valueOf(((TestRSGroupsBase.tablePrefix) + "_ns"), name.getMethodName());
        TestRSGroupsBase.admin.createNamespace(NamespaceDescriptor.create(tableName.getNamespaceAsString()).addConfiguration(NAMESPACE_DESC_PROP_GROUP, appInfo.getName()).build());
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
        Assert.assertEquals(1, TestRSGroupsBase.admin.getRegions(targetServer).size());
        try {
            // stopping may cause an exception
            // due to the connection loss
            TestRSGroupsBase.admin.stopRegionServer(targetServer.getAddress().toString());
        } catch (Exception e) {
        }
        // wait until the server is actually down
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return !(TestRSGroupsBase.cluster.getClusterMetrics().getLiveServerMetrics().containsKey(targetServer));
            }
        });
        // there is only one rs in the group and we killed it, so the region can not be online, until
        // later we add new servers to it.
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return !(TestRSGroupsBase.cluster.getClusterMetrics().getRegionStatesInTransition().isEmpty());
            }
        });
        Set<Address> newServers = Sets.newHashSet();
        newServers.add(TestRSGroupsBase.rsGroupAdmin.getRSGroupInfo(DEFAULT_GROUP).getServers().iterator().next());
        TestRSGroupsBase.rsGroupAdmin.moveServers(newServers, appInfo.getName());
        // Make sure all the table's regions get reassigned
        // disabling the table guarantees no conflicting assign/unassign (ie SSH) happens
        TestRSGroupsBase.admin.disableTable(tableName);
        TestRSGroupsBase.admin.enableTable(tableName);
        // wait for region to be assigned
        TestRSGroupsBase.TEST_UTIL.waitFor(TestRSGroupsBase.WAIT_TIMEOUT, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return TestRSGroupsBase.cluster.getClusterMetrics().getRegionStatesInTransition().isEmpty();
            }
        });
        ServerName targetServer1 = getServerName(newServers.iterator().next());
        Assert.assertEquals(1, TestRSGroupsBase.admin.getRegions(targetServer1).size());
        Assert.assertEquals(tableName, TestRSGroupsBase.admin.getRegions(targetServer1).get(0).getTable());
    }
}

