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
package org.apache.hadoop.hbase.quotas;


import QuotaScope.CLUSTER;
import QuotaScope.MACHINE;
import ThrottleType.READ_NUMBER;
import ThrottleType.WRITE_NUMBER;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ RegionServerTests.class, LargeTests.class })
public class TestClusterScopeQuotaThrottle {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClusterScopeQuotaThrottle.class);

    private static final int REFRESH_TIME = 30 * 60000;

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName[] TABLE_NAMES = new TableName[]{ TableName.valueOf("TestQuotaAdmin0"), TableName.valueOf("TestQuotaAdmin1"), TableName.valueOf("TestQuotaAdmin2") };

    private static final byte[] FAMILY = Bytes.toBytes("cf");

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static final byte[][] SPLITS = new byte[][]{ Bytes.toBytes("1") };

    private static Table[] tables;

    private static final String NAMESPACE = "TestNs";

    private static final TableName TABLE_NAME = TableName.valueOf(TestClusterScopeQuotaThrottle.NAMESPACE, "TestTable");

    private static Table table;

    @Test
    public void testNamespaceClusterScopeQuota() throws Exception {
        final Admin admin = TestClusterScopeQuotaThrottle.TEST_UTIL.getAdmin();
        final String NAMESPACE = "default";
        // Add 10req/min limit for write request in cluster scope
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(NAMESPACE, WRITE_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        // Add 6req/min limit for read request in machine scope
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(NAMESPACE, READ_NUMBER, 6, TimeUnit.MINUTES, MACHINE));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, false, TestClusterScopeQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 5 write requests and at max 3 read requests
        Assert.assertEquals(5, ThrottleQuotaTestUtil.doPuts(10, TestClusterScopeQuotaThrottle.FAMILY, TestClusterScopeQuotaThrottle.QUALIFIER, TestClusterScopeQuotaThrottle.tables[0]));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doGets(10, TestClusterScopeQuotaThrottle.tables[0]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleNamespace(NAMESPACE));
        ThrottleQuotaTestUtil.triggerNamespaceCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, true, TestClusterScopeQuotaThrottle.TABLE_NAMES[0]);
    }

    @Test
    public void testTableClusterScopeQuota() throws Exception {
        final Admin admin = TestClusterScopeQuotaThrottle.TEST_UTIL.getAdmin();
        admin.setQuota(QuotaSettingsFactory.throttleTable(TestClusterScopeQuotaThrottle.TABLE_NAME, READ_NUMBER, 20, TimeUnit.HOURS, CLUSTER));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, false, TestClusterScopeQuotaThrottle.TABLE_NAME);
        for (RegionServerThread rst : TestClusterScopeQuotaThrottle.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads()) {
            for (TableName tableName : rst.getRegionServer().getOnlineTables()) {
                if (tableName.getNameAsString().equals(TestClusterScopeQuotaThrottle.TABLE_NAME.getNameAsString())) {
                    int rsRegionNum = rst.getRegionServer().getRegions(tableName).size();
                    if (rsRegionNum == 0) {
                        // If rs has 0 region, the machine limiter is 0 (20 * 0 / 2)
                        break;
                    } else
                        if (rsRegionNum == 1) {
                            // If rs has 1 region, the machine limiter is 10 (20 * 1 / 2)
                            // Read rows from 1 region, so can read 10 first time and 0 second time
                            long count = ThrottleQuotaTestUtil.doGets(20, TestClusterScopeQuotaThrottle.table);
                            Assert.assertTrue(((count == 0) || (count == 10)));
                        } else
                            if (rsRegionNum == 2) {
                                // If rs has 2 regions, the machine limiter is 20 (20 * 2 / 2)
                                Assert.assertEquals(20, ThrottleQuotaTestUtil.doGets(20, TestClusterScopeQuotaThrottle.table));
                            }


                    break;
                }
            }
        }
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(TestClusterScopeQuotaThrottle.TABLE_NAME));
        ThrottleQuotaTestUtil.triggerTableCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, true, TestClusterScopeQuotaThrottle.TABLE_NAME);
    }

    @Test
    public void testUserClusterScopeQuota() throws Exception {
        final Admin admin = TestClusterScopeQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        // Add 6req/min limit for read request in cluster scope
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, READ_NUMBER, 6, TimeUnit.MINUTES, CLUSTER));
        // Add 6req/min limit for write request in machine scope
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, false, TestClusterScopeQuotaThrottle.TABLE_NAMES);
        // should execute at max 6 read requests and at max 3 write write requests
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(10, TestClusterScopeQuotaThrottle.FAMILY, TestClusterScopeQuotaThrottle.QUALIFIER, TestClusterScopeQuotaThrottle.tables[0]));
        Assert.assertEquals(3, ThrottleQuotaTestUtil.doGets(10, TestClusterScopeQuotaThrottle.tables[0]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, true, TestClusterScopeQuotaThrottle.TABLE_NAMES);
    }

    @Test
    public void testUserNamespaceClusterScopeQuota() throws Exception {
        final Admin admin = TestClusterScopeQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        final String namespace = TestClusterScopeQuotaThrottle.TABLE_NAMES[0].getNamespaceAsString();
        // Add 10req/min limit for read request in cluster scope
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, namespace, READ_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        // Add 6req/min limit for write request in machine scope
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, namespace, WRITE_NUMBER, 6, TimeUnit.MINUTES));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, false, TestClusterScopeQuotaThrottle.TABLE_NAMES[0]);
        // should execute at max 5 read requests and at max 6 write requests
        Assert.assertEquals(5, ThrottleQuotaTestUtil.doGets(10, TestClusterScopeQuotaThrottle.tables[0]));
        Assert.assertEquals(6, ThrottleQuotaTestUtil.doPuts(10, TestClusterScopeQuotaThrottle.FAMILY, TestClusterScopeQuotaThrottle.QUALIFIER, TestClusterScopeQuotaThrottle.tables[0]));
        // Remove all the limits
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName, namespace));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, true, TestClusterScopeQuotaThrottle.TABLE_NAMES[0]);
    }

    @Test
    public void testUserTableClusterScopeQuota() throws Exception {
        final Admin admin = TestClusterScopeQuotaThrottle.TEST_UTIL.getAdmin();
        final String userName = User.getCurrent().getShortName();
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, TestClusterScopeQuotaThrottle.TABLE_NAME, READ_NUMBER, 20, TimeUnit.HOURS, CLUSTER));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, false, TestClusterScopeQuotaThrottle.TABLE_NAME);
        for (RegionServerThread rst : TestClusterScopeQuotaThrottle.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads()) {
            for (TableName tableName : rst.getRegionServer().getOnlineTables()) {
                if (tableName.getNameAsString().equals(TestClusterScopeQuotaThrottle.TABLE_NAME.getNameAsString())) {
                    int rsRegionNum = rst.getRegionServer().getRegions(tableName).size();
                    if (rsRegionNum == 0) {
                        // If rs has 0 region, the machine limiter is 0 (20 * 0 / 2)
                        break;
                    } else
                        if (rsRegionNum == 1) {
                            // If rs has 1 region, the machine limiter is 10 (20 * 1 / 2)
                            // Read rows from 1 region, so can read 10 first time and 0 second time
                            long count = ThrottleQuotaTestUtil.doGets(20, TestClusterScopeQuotaThrottle.table);
                            Assert.assertTrue(((count == 0) || (count == 10)));
                        } else
                            if (rsRegionNum == 2) {
                                // If rs has 2 regions, the machine limiter is 20 (20 * 2 / 2)
                                Assert.assertEquals(20, ThrottleQuotaTestUtil.doGets(20, TestClusterScopeQuotaThrottle.table));
                            }


                    break;
                }
            }
        }
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        ThrottleQuotaTestUtil.triggerUserCacheRefresh(TestClusterScopeQuotaThrottle.TEST_UTIL, true, TestClusterScopeQuotaThrottle.TABLE_NAME);
    }
}

