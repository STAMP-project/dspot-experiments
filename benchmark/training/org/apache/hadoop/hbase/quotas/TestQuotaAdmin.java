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


import JVMClusterUtil.RegionServerThread;
import QuotaScope.CLUSTER;
import QuotaScope.MACHINE;
import QuotaTableUtil.QUOTA_TABLE_NAME;
import ThrottleType.READ_NUMBER;
import ThrottleType.REQUEST_CAPACITY_UNIT;
import ThrottleType.REQUEST_NUMBER;
import ThrottleType.REQUEST_SIZE;
import ThrottleType.WRITE_NUMBER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static QuotaTableUtil.QUOTA_REGION_SERVER_ROW_KEY;
import static QuotaType.THROTTLE;
import static SpaceViolationPolicy.NO_WRITES;
import static SpaceViolationPolicy.NO_WRITES_COMPACTIONS;
import static ThrottleType.READ_NUMBER;
import static ThrottleType.REQUEST_NUMBER;


/**
 * minicluster tests that validate that quota  entries are properly set in the quota table
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestQuotaAdmin {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaAdmin.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestQuotaAdmin.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testThrottleType() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        String userName = User.getCurrent().getShortName();
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, READ_NUMBER, 6, TimeUnit.MINUTES));
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, WRITE_NUMBER, 12, TimeUnit.MINUTES));
        admin.setQuota(QuotaSettingsFactory.bypassGlobals(userName, true));
        try (QuotaRetriever scanner = QuotaRetriever.open(TestQuotaAdmin.TEST_UTIL.getConfiguration())) {
            int countThrottle = 0;
            int countGlobalBypass = 0;
            for (QuotaSettings settings : scanner) {
                switch (settings.getQuotaType()) {
                    case THROTTLE :
                        ThrottleSettings throttle = ((ThrottleSettings) (settings));
                        if ((throttle.getSoftLimit()) == 6) {
                            Assert.assertEquals(READ_NUMBER, throttle.getThrottleType());
                        } else
                            if ((throttle.getSoftLimit()) == 12) {
                                Assert.assertEquals(WRITE_NUMBER, throttle.getThrottleType());
                            } else {
                                Assert.fail("should not come here, because don't set quota with this limit");
                            }

                        Assert.assertEquals(userName, throttle.getUserName());
                        Assert.assertEquals(null, throttle.getTableName());
                        Assert.assertEquals(null, throttle.getNamespace());
                        Assert.assertEquals(TimeUnit.MINUTES, throttle.getTimeUnit());
                        countThrottle++;
                        break;
                    case GLOBAL_BYPASS :
                        countGlobalBypass++;
                        break;
                    default :
                        Assert.fail(("unexpected settings type: " + (settings.getQuotaType())));
                }
            }
            Assert.assertEquals(2, countThrottle);
            Assert.assertEquals(1, countGlobalBypass);
        }
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        assertNumResults(1, null);
        admin.setQuota(QuotaSettingsFactory.bypassGlobals(userName, false));
        assertNumResults(0, null);
    }

    @Test
    public void testSimpleScan() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        String userName = User.getCurrent().getShortName();
        admin.setQuota(QuotaSettingsFactory.throttleUser(userName, REQUEST_NUMBER, 6, TimeUnit.MINUTES));
        admin.setQuota(QuotaSettingsFactory.bypassGlobals(userName, true));
        try (QuotaRetriever scanner = QuotaRetriever.open(TestQuotaAdmin.TEST_UTIL.getConfiguration())) {
            int countThrottle = 0;
            int countGlobalBypass = 0;
            for (QuotaSettings settings : scanner) {
                TestQuotaAdmin.LOG.debug(Objects.toString(settings));
                switch (settings.getQuotaType()) {
                    case THROTTLE :
                        ThrottleSettings throttle = ((ThrottleSettings) (settings));
                        Assert.assertEquals(userName, throttle.getUserName());
                        Assert.assertEquals(null, throttle.getTableName());
                        Assert.assertEquals(null, throttle.getNamespace());
                        Assert.assertEquals(null, throttle.getRegionServer());
                        Assert.assertEquals(6, throttle.getSoftLimit());
                        Assert.assertEquals(TimeUnit.MINUTES, throttle.getTimeUnit());
                        countThrottle++;
                        break;
                    case GLOBAL_BYPASS :
                        countGlobalBypass++;
                        break;
                    default :
                        Assert.fail(("unexpected settings type: " + (settings.getQuotaType())));
                }
            }
            Assert.assertEquals(1, countThrottle);
            Assert.assertEquals(1, countGlobalBypass);
        }
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(userName));
        assertNumResults(1, null);
        admin.setQuota(QuotaSettingsFactory.bypassGlobals(userName, false));
        assertNumResults(0, null);
    }

    @Test
    public void testMultiQuotaThrottling() throws Exception {
        byte[] FAMILY = Bytes.toBytes("testFamily");
        byte[] ROW = Bytes.toBytes("testRow");
        byte[] QUALIFIER = Bytes.toBytes("testQualifier");
        byte[] VALUE = Bytes.toBytes("testValue");
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        TableName tableName = TableName.valueOf("testMultiQuotaThrottling");
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(FAMILY)).build();
        admin.createTable(desc);
        // Set up the quota.
        admin.setQuota(QuotaSettingsFactory.throttleTable(tableName, WRITE_NUMBER, 6, TimeUnit.SECONDS));
        Thread.sleep(1000);
        TestQuotaAdmin.TEST_UTIL.getRSForFirstRegionInTable(tableName).getRegionServerRpcQuotaManager().getQuotaCache().triggerCacheRefresh();
        Thread.sleep(1000);
        Table t = TestQuotaAdmin.TEST_UTIL.getConnection().getTable(tableName);
        try {
            int size = 5;
            List actions = new ArrayList();
            Object[] results = new Object[size];
            for (int i = 0; i < size; i++) {
                Put put1 = new Put(ROW);
                put1.addColumn(FAMILY, QUALIFIER, VALUE);
                actions.add(put1);
            }
            t.batch(actions, results);
            t.batch(actions, results);
        } catch (IOException e) {
            Assert.fail(("Not supposed to get ThrottlingExcepiton " + e));
        } finally {
            t.close();
        }
    }

    @Test
    public void testQuotaRetrieverFilter() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        TableName[] tables = new TableName[]{ TableName.valueOf("T0"), TableName.valueOf("T01"), TableName.valueOf("NS0:T2") };
        String[] namespaces = new String[]{ "NS0", "NS01", "NS2" };
        String[] users = new String[]{ "User0", "User01", "User2" };
        for (String user : users) {
            admin.setQuota(QuotaSettingsFactory.throttleUser(user, REQUEST_NUMBER, 1, TimeUnit.MINUTES));
            for (TableName table : tables) {
                admin.setQuota(QuotaSettingsFactory.throttleUser(user, table, REQUEST_NUMBER, 2, TimeUnit.MINUTES));
            }
            for (String ns : namespaces) {
                admin.setQuota(QuotaSettingsFactory.throttleUser(user, ns, REQUEST_NUMBER, 3, TimeUnit.MINUTES));
            }
        }
        assertNumResults(21, null);
        for (TableName table : tables) {
            admin.setQuota(QuotaSettingsFactory.throttleTable(table, REQUEST_NUMBER, 4, TimeUnit.MINUTES));
        }
        assertNumResults(24, null);
        for (String ns : namespaces) {
            admin.setQuota(QuotaSettingsFactory.throttleNamespace(ns, REQUEST_NUMBER, 5, TimeUnit.MINUTES));
        }
        assertNumResults(27, null);
        assertNumResults(7, new QuotaFilter().setUserFilter("User0"));
        assertNumResults(0, new QuotaFilter().setUserFilter("User"));
        assertNumResults(21, new QuotaFilter().setUserFilter("User.*"));
        assertNumResults(3, new QuotaFilter().setUserFilter("User.*").setTableFilter("T0"));
        assertNumResults(3, new QuotaFilter().setUserFilter("User.*").setTableFilter("NS.*"));
        assertNumResults(0, new QuotaFilter().setUserFilter("User.*").setTableFilter("T"));
        assertNumResults(6, new QuotaFilter().setUserFilter("User.*").setTableFilter("T.*"));
        assertNumResults(3, new QuotaFilter().setUserFilter("User.*").setNamespaceFilter("NS0"));
        assertNumResults(0, new QuotaFilter().setUserFilter("User.*").setNamespaceFilter("NS"));
        assertNumResults(9, new QuotaFilter().setUserFilter("User.*").setNamespaceFilter("NS.*"));
        assertNumResults(6, new QuotaFilter().setUserFilter("User.*").setTableFilter("T0").setNamespaceFilter("NS0"));
        assertNumResults(1, new QuotaFilter().setTableFilter("T0"));
        assertNumResults(0, new QuotaFilter().setTableFilter("T"));
        assertNumResults(2, new QuotaFilter().setTableFilter("T.*"));
        assertNumResults(3, new QuotaFilter().setTableFilter(".*T.*"));
        assertNumResults(1, new QuotaFilter().setNamespaceFilter("NS0"));
        assertNumResults(0, new QuotaFilter().setNamespaceFilter("NS"));
        assertNumResults(3, new QuotaFilter().setNamespaceFilter("NS.*"));
        for (String user : users) {
            admin.setQuota(QuotaSettingsFactory.unthrottleUser(user));
            for (TableName table : tables) {
                admin.setQuota(QuotaSettingsFactory.unthrottleUser(user, table));
            }
            for (String ns : namespaces) {
                admin.setQuota(QuotaSettingsFactory.unthrottleUser(user, ns));
            }
        }
        assertNumResults(6, null);
        for (TableName table : tables) {
            admin.setQuota(QuotaSettingsFactory.unthrottleTable(table));
        }
        assertNumResults(3, null);
        for (String ns : namespaces) {
            admin.setQuota(QuotaSettingsFactory.unthrottleNamespace(ns));
        }
        assertNumResults(0, null);
    }

    @Test
    public void testSetGetRemoveSpaceQuota() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        final TableName tn = TableName.valueOf("sq_table1");
        final long sizeLimit = (((1024L * 1024L) * 1024L) * 1024L) * 5L;// 5TB

        final SpaceViolationPolicy violationPolicy = NO_WRITES;
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, sizeLimit, violationPolicy);
        admin.setQuota(settings);
        // Verify the Quotas in the table
        try (Table quotaTable = TestQuotaAdmin.TEST_UTIL.getConnection().getTable(QUOTA_TABLE_NAME)) {
            ResultScanner scanner = quotaTable.getScanner(new Scan());
            try {
                Result r = Iterables.getOnlyElement(scanner);
                CellScanner cells = r.cellScanner();
                Assert.assertTrue("Expected to find a cell", cells.advance());
                assertSpaceQuota(sizeLimit, violationPolicy, cells.current());
            } finally {
                scanner.close();
            }
        }
        // Verify we can retrieve it via the QuotaRetriever API
        QuotaRetriever scanner = QuotaRetriever.open(admin.getConfiguration());
        try {
            assertSpaceQuota(sizeLimit, violationPolicy, Iterables.getOnlyElement(scanner));
        } finally {
            scanner.close();
        }
        // Now, remove the quota
        QuotaSettings removeQuota = QuotaSettingsFactory.removeTableSpaceLimit(tn);
        admin.setQuota(removeQuota);
        // Verify that the record doesn't exist in the table
        try (Table quotaTable = TestQuotaAdmin.TEST_UTIL.getConnection().getTable(QUOTA_TABLE_NAME)) {
            ResultScanner rs = quotaTable.getScanner(new Scan());
            try {
                Assert.assertNull("Did not expect to find a quota entry", rs.next());
            } finally {
                rs.close();
            }
        }
        // Verify that we can also not fetch it via the API
        scanner = QuotaRetriever.open(admin.getConfiguration());
        try {
            Assert.assertNull("Did not expect to find a quota entry", scanner.next());
        } finally {
            scanner.close();
        }
    }

    @Test
    public void testSetModifyRemoveSpaceQuota() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        final TableName tn = TableName.valueOf("sq_table2");
        final long originalSizeLimit = (((1024L * 1024L) * 1024L) * 1024L) * 5L;// 5TB

        final SpaceViolationPolicy violationPolicy = NO_WRITES;
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, originalSizeLimit, violationPolicy);
        admin.setQuota(settings);
        // Verify the Quotas in the table
        try (Table quotaTable = TestQuotaAdmin.TEST_UTIL.getConnection().getTable(QUOTA_TABLE_NAME)) {
            ResultScanner scanner = quotaTable.getScanner(new Scan());
            try {
                Result r = Iterables.getOnlyElement(scanner);
                CellScanner cells = r.cellScanner();
                Assert.assertTrue("Expected to find a cell", cells.advance());
                assertSpaceQuota(originalSizeLimit, violationPolicy, cells.current());
            } finally {
                scanner.close();
            }
        }
        // Verify we can retrieve it via the QuotaRetriever API
        QuotaRetriever quotaScanner = QuotaRetriever.open(admin.getConfiguration());
        try {
            assertSpaceQuota(originalSizeLimit, violationPolicy, Iterables.getOnlyElement(quotaScanner));
        } finally {
            quotaScanner.close();
        }
        // Setting a new size and policy should be reflected
        final long newSizeLimit = ((1024L * 1024L) * 1024L) * 1024L;// 1TB

        final SpaceViolationPolicy newViolationPolicy = NO_WRITES_COMPACTIONS;
        QuotaSettings newSettings = QuotaSettingsFactory.limitTableSpace(tn, newSizeLimit, newViolationPolicy);
        admin.setQuota(newSettings);
        // Verify the new Quotas in the table
        try (Table quotaTable = TestQuotaAdmin.TEST_UTIL.getConnection().getTable(QUOTA_TABLE_NAME)) {
            ResultScanner scanner = quotaTable.getScanner(new Scan());
            try {
                Result r = Iterables.getOnlyElement(scanner);
                CellScanner cells = r.cellScanner();
                Assert.assertTrue("Expected to find a cell", cells.advance());
                assertSpaceQuota(newSizeLimit, newViolationPolicy, cells.current());
            } finally {
                scanner.close();
            }
        }
        // Verify we can retrieve the new quota via the QuotaRetriever API
        quotaScanner = QuotaRetriever.open(admin.getConfiguration());
        try {
            assertSpaceQuota(newSizeLimit, newViolationPolicy, Iterables.getOnlyElement(quotaScanner));
        } finally {
            quotaScanner.close();
        }
        // Now, remove the quota
        QuotaSettings removeQuota = QuotaSettingsFactory.removeTableSpaceLimit(tn);
        admin.setQuota(removeQuota);
        // Verify that the record doesn't exist in the table
        try (Table quotaTable = TestQuotaAdmin.TEST_UTIL.getConnection().getTable(QUOTA_TABLE_NAME)) {
            ResultScanner scanner = quotaTable.getScanner(new Scan());
            try {
                Assert.assertNull("Did not expect to find a quota entry", scanner.next());
            } finally {
                scanner.close();
            }
        }
        // Verify that we can also not fetch it via the API
        quotaScanner = QuotaRetriever.open(admin.getConfiguration());
        try {
            Assert.assertNull("Did not expect to find a quota entry", quotaScanner.next());
        } finally {
            quotaScanner.close();
        }
    }

    @Test
    public void testSetGetRemoveRPCQuota() throws Exception {
        testSetGetRemoveRPCQuota(REQUEST_SIZE);
        testSetGetRemoveRPCQuota(REQUEST_CAPACITY_UNIT);
    }

    @Test
    public void testSetModifyRemoveRPCQuota() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        final TableName tn = TableName.valueOf("sq_table1");
        QuotaSettings settings = QuotaSettingsFactory.throttleTable(tn, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        // Verify the Quota in the table
        verifyRecordPresentInQuotaTable(REQUEST_SIZE, 2L, TimeUnit.HOURS);
        // Verify we can retrieve it via the QuotaRetriever API
        verifyFetchableViaAPI(admin, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        // Setting a limit and time unit should be reflected
        QuotaSettings newSettings = QuotaSettingsFactory.throttleTable(tn, REQUEST_SIZE, 3L, TimeUnit.DAYS);
        admin.setQuota(newSettings);
        // Verify the new Quota in the table
        verifyRecordPresentInQuotaTable(REQUEST_SIZE, 3L, TimeUnit.DAYS);
        // Verify we can retrieve the new quota via the QuotaRetriever API
        verifyFetchableViaAPI(admin, REQUEST_SIZE, 3L, TimeUnit.DAYS);
        // Now, remove the quota
        QuotaSettings removeQuota = QuotaSettingsFactory.unthrottleTable(tn);
        admin.setQuota(removeQuota);
        // Verify that the record doesn't exist in the table
        verifyRecordNotPresentInQuotaTable();
        // Verify that we can also not fetch it via the API
        verifyNotFetchableViaAPI(admin);
    }

    @Test
    public void testSetAndRemoveRegionServerQuota() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        String regionServer = QUOTA_REGION_SERVER_ROW_KEY;
        QuotaFilter rsFilter = new QuotaFilter().setRegionServerFilter(regionServer);
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(regionServer, REQUEST_NUMBER, 10, TimeUnit.MINUTES));
        assertNumResults(1, rsFilter);
        // Verify the Quota in the table
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES);
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(regionServer, REQUEST_NUMBER, 20, TimeUnit.MINUTES));
        assertNumResults(1, rsFilter);
        // Verify the Quota in the table
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 20, TimeUnit.MINUTES);
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(regionServer, READ_NUMBER, 30, TimeUnit.SECONDS));
        int count = 0;
        QuotaRetriever scanner = QuotaRetriever.open(TestQuotaAdmin.TEST_UTIL.getConfiguration(), rsFilter);
        try {
            for (QuotaSettings settings : scanner) {
                Assert.assertTrue(((settings.getQuotaType()) == (THROTTLE)));
                ThrottleSettings throttleSettings = ((ThrottleSettings) (settings));
                Assert.assertEquals(regionServer, throttleSettings.getRegionServer());
                count++;
                if ((throttleSettings.getThrottleType()) == (REQUEST_NUMBER)) {
                    Assert.assertEquals(20, throttleSettings.getSoftLimit());
                    Assert.assertEquals(TimeUnit.MINUTES, throttleSettings.getTimeUnit());
                } else
                    if ((throttleSettings.getThrottleType()) == (READ_NUMBER)) {
                        Assert.assertEquals(30, throttleSettings.getSoftLimit());
                        Assert.assertEquals(TimeUnit.SECONDS, throttleSettings.getTimeUnit());
                    }

            }
        } finally {
            scanner.close();
        }
        Assert.assertEquals(2, count);
        admin.setQuota(QuotaSettingsFactory.unthrottleRegionServer(regionServer));
        assertNumResults(0, new QuotaFilter().setRegionServerFilter(regionServer));
    }

    @Test
    public void testRpcThrottleWhenStartup() throws IOException, InterruptedException {
        TestQuotaAdmin.TEST_UTIL.getAdmin().switchRpcThrottle(false);
        Assert.assertFalse(TestQuotaAdmin.TEST_UTIL.getAdmin().isRpcThrottleEnabled());
        TestQuotaAdmin.TEST_UTIL.killMiniHBaseCluster();
        TestQuotaAdmin.TEST_UTIL.startMiniHBaseCluster();
        Assert.assertFalse(TestQuotaAdmin.TEST_UTIL.getAdmin().isRpcThrottleEnabled());
        for (JVMClusterUtil.RegionServerThread rs : TestQuotaAdmin.TEST_UTIL.getHBaseCluster().getRegionServerThreads()) {
            RegionServerRpcQuotaManager quotaManager = rs.getRegionServer().getRegionServerRpcQuotaManager();
            Assert.assertFalse(quotaManager.isRpcThrottleEnabled());
        }
        // enable rpc throttle
        TestQuotaAdmin.TEST_UTIL.getAdmin().switchRpcThrottle(true);
        Assert.assertTrue(TestQuotaAdmin.TEST_UTIL.getAdmin().isRpcThrottleEnabled());
    }

    @Test
    public void testSwitchRpcThrottle() throws IOException {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        testSwitchRpcThrottle(admin, true, true);
        testSwitchRpcThrottle(admin, true, false);
        testSwitchRpcThrottle(admin, false, false);
        testSwitchRpcThrottle(admin, false, true);
    }

    @Test
    public void testSwitchExceedThrottleQuota() throws IOException {
        String regionServer = QUOTA_REGION_SERVER_ROW_KEY;
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        try {
            admin.exceedThrottleQuotaSwitch(true);
            Assert.fail(("should not come here, because can't enable exceed throttle quota " + "if there is no region server quota"));
        } catch (IOException e) {
            TestQuotaAdmin.LOG.warn("Expected exception", e);
        }
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(regionServer, WRITE_NUMBER, 100, TimeUnit.SECONDS));
        try {
            admin.exceedThrottleQuotaSwitch(true);
            Assert.fail(("should not come here, because can't enable exceed throttle quota " + "if there is no read region server quota"));
        } catch (IOException e) {
            TestQuotaAdmin.LOG.warn("Expected exception", e);
        }
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(regionServer, READ_NUMBER, 20, TimeUnit.MINUTES));
        try {
            admin.exceedThrottleQuotaSwitch(true);
            Assert.fail(("should not come here, because can't enable exceed throttle quota " + "because not all region server quota are in seconds time unit"));
        } catch (IOException e) {
            TestQuotaAdmin.LOG.warn("Expected exception", e);
        }
        admin.setQuota(QuotaSettingsFactory.throttleRegionServer(regionServer, READ_NUMBER, 20, TimeUnit.SECONDS));
        Assert.assertFalse(admin.exceedThrottleQuotaSwitch(true));
        Assert.assertTrue(admin.exceedThrottleQuotaSwitch(true));
        Assert.assertTrue(admin.exceedThrottleQuotaSwitch(false));
        Assert.assertFalse(admin.exceedThrottleQuotaSwitch(false));
        admin.setQuota(QuotaSettingsFactory.unthrottleRegionServer(regionServer));
    }

    @Test
    public void testQuotaScope() throws Exception {
        Admin admin = TestQuotaAdmin.TEST_UTIL.getAdmin();
        String user = "user1";
        String namespace = "testQuotaScope_ns";
        TableName tableName = TableName.valueOf("testQuotaScope");
        QuotaFilter filter = new QuotaFilter();
        // set CLUSTER quota scope for namespace
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(namespace, REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        assertNumResults(1, filter);
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER);
        admin.setQuota(QuotaSettingsFactory.throttleNamespace(namespace, REQUEST_NUMBER, 10, TimeUnit.MINUTES, MACHINE));
        assertNumResults(1, filter);
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES, MACHINE);
        admin.setQuota(QuotaSettingsFactory.unthrottleNamespace(namespace));
        assertNumResults(0, filter);
        // set CLUSTER quota scope for table
        admin.setQuota(QuotaSettingsFactory.throttleTable(tableName, REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER);
        admin.setQuota(QuotaSettingsFactory.unthrottleTable(tableName));
        // set CLUSTER quota scope for user
        admin.setQuota(QuotaSettingsFactory.throttleUser(user, REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER);
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(user));
        // set CLUSTER quota scope for user and table
        admin.setQuota(QuotaSettingsFactory.throttleUser(user, tableName, REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER);
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(user));
        // set CLUSTER quota scope for user and namespace
        admin.setQuota(QuotaSettingsFactory.throttleUser(user, namespace, REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER));
        verifyRecordPresentInQuotaTable(REQUEST_NUMBER, 10, TimeUnit.MINUTES, CLUSTER);
        admin.setQuota(QuotaSettingsFactory.unthrottleUser(user));
    }
}

