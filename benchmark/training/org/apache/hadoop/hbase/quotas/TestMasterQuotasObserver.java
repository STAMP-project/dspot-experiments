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


import SpaceViolationPolicy.NO_INSERTS;
import ThrottleType.REQUEST_SIZE;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.MasterCoprocessorHost;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Test class for {@link MasterQuotasObserver}.
 */
@Category(MediumTests.class)
public class TestMasterQuotasObserver {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterQuotasObserver.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static SpaceQuotaHelperForTests helper;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testTableSpaceQuotaRemoved() throws Exception {
        final Connection conn = TestMasterQuotasObserver.TEST_UTIL.getConnection();
        final Admin admin = conn.getAdmin();
        final TableName tn = TableName.valueOf(testName.getMethodName());
        // Drop the table if it somehow exists
        if (admin.tableExists(tn)) {
            dropTable(admin, tn);
        }
        createTable(admin, tn);
        Assert.assertEquals(0, getNumSpaceQuotas());
        // Set space quota
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, 1024L, NO_INSERTS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        // Drop the table and observe the Space quota being automatically deleted as well
        dropTable(admin, tn);
        Assert.assertEquals(0, getNumSpaceQuotas());
    }

    @Test
    public void testTableRPCQuotaRemoved() throws Exception {
        final Connection conn = TestMasterQuotasObserver.TEST_UTIL.getConnection();
        final Admin admin = conn.getAdmin();
        final TableName tn = TableName.valueOf(testName.getMethodName());
        // Drop the table if it somehow exists
        if (admin.tableExists(tn)) {
            dropTable(admin, tn);
        }
        createTable(admin, tn);
        Assert.assertEquals(0, getThrottleQuotas());
        // Set RPC quota
        QuotaSettings settings = QuotaSettingsFactory.throttleTable(tn, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getThrottleQuotas());
        // Delete the table and observe the RPC quota being automatically deleted as well
        dropTable(admin, tn);
        Assert.assertEquals(0, getThrottleQuotas());
    }

    @Test
    public void testTableSpaceAndRPCQuotaRemoved() throws Exception {
        final Connection conn = TestMasterQuotasObserver.TEST_UTIL.getConnection();
        final Admin admin = conn.getAdmin();
        final TableName tn = TableName.valueOf(testName.getMethodName());
        // Drop the table if it somehow exists
        if (admin.tableExists(tn)) {
            dropTable(admin, tn);
        }
        createTable(admin, tn);
        Assert.assertEquals(0, getNumSpaceQuotas());
        Assert.assertEquals(0, getThrottleQuotas());
        // Set Both quotas
        QuotaSettings settings = QuotaSettingsFactory.limitTableSpace(tn, 1024L, NO_INSERTS);
        admin.setQuota(settings);
        settings = QuotaSettingsFactory.throttleTable(tn, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Remove Space quota
        settings = QuotaSettingsFactory.removeTableSpaceLimit(tn);
        admin.setQuota(settings);
        Assert.assertEquals(0, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Set back the space quota
        settings = QuotaSettingsFactory.limitTableSpace(tn, 1024L, NO_INSERTS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Remove the throttle quota
        settings = QuotaSettingsFactory.unthrottleTable(tn);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(0, getThrottleQuotas());
        // Set back the throttle quota
        settings = QuotaSettingsFactory.throttleTable(tn, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Drop the table and check that both the quotas have been dropped as well
        dropTable(admin, tn);
        Assert.assertEquals(0, getNumSpaceQuotas());
        Assert.assertEquals(0, getThrottleQuotas());
    }

    @Test
    public void testNamespaceSpaceQuotaRemoved() throws Exception {
        final Connection conn = TestMasterQuotasObserver.TEST_UTIL.getConnection();
        final Admin admin = conn.getAdmin();
        final String ns = testName.getMethodName();
        // Drop the ns if it somehow exists
        if (namespaceExists(ns)) {
            admin.deleteNamespace(ns);
        }
        // Create the ns
        NamespaceDescriptor desc = NamespaceDescriptor.create(ns).build();
        admin.createNamespace(desc);
        Assert.assertEquals(0, getNumSpaceQuotas());
        // Set a quota
        QuotaSettings settings = QuotaSettingsFactory.limitNamespaceSpace(ns, 1024L, NO_INSERTS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        // Delete the namespace and observe the quota being automatically deleted as well
        admin.deleteNamespace(ns);
        Assert.assertEquals(0, getNumSpaceQuotas());
    }

    @Test
    public void testNamespaceRPCQuotaRemoved() throws Exception {
        final Connection conn = TestMasterQuotasObserver.TEST_UTIL.getConnection();
        final Admin admin = conn.getAdmin();
        final String ns = testName.getMethodName();
        // Drop the ns if it somehow exists
        if (namespaceExists(ns)) {
            admin.deleteNamespace(ns);
        }
        // Create the ns
        NamespaceDescriptor desc = NamespaceDescriptor.create(ns).build();
        admin.createNamespace(desc);
        Assert.assertEquals(0, getThrottleQuotas());
        // Set a quota
        QuotaSettings settings = QuotaSettingsFactory.throttleNamespace(ns, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getThrottleQuotas());
        // Delete the namespace and observe the quota being automatically deleted as well
        admin.deleteNamespace(ns);
        Assert.assertEquals(0, getThrottleQuotas());
    }

    @Test
    public void testNamespaceSpaceAndRPCQuotaRemoved() throws Exception {
        final Connection conn = TestMasterQuotasObserver.TEST_UTIL.getConnection();
        final Admin admin = conn.getAdmin();
        final String ns = testName.getMethodName();
        // Drop the ns if it somehow exists
        if (namespaceExists(ns)) {
            admin.deleteNamespace(ns);
        }
        // Create the ns
        NamespaceDescriptor desc = NamespaceDescriptor.create(ns).build();
        admin.createNamespace(desc);
        Assert.assertEquals(0, getNumSpaceQuotas());
        Assert.assertEquals(0, getThrottleQuotas());
        // Set Both quotas
        QuotaSettings settings = QuotaSettingsFactory.limitNamespaceSpace(ns, 1024L, NO_INSERTS);
        admin.setQuota(settings);
        settings = QuotaSettingsFactory.throttleNamespace(ns, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Remove Space quota
        settings = QuotaSettingsFactory.removeNamespaceSpaceLimit(ns);
        admin.setQuota(settings);
        Assert.assertEquals(0, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Set back the space quota
        settings = QuotaSettingsFactory.limitNamespaceSpace(ns, 1024L, NO_INSERTS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Remove the throttle quota
        settings = QuotaSettingsFactory.unthrottleNamespace(ns);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(0, getThrottleQuotas());
        // Set back the throttle quota
        settings = QuotaSettingsFactory.throttleNamespace(ns, REQUEST_SIZE, 2L, TimeUnit.HOURS);
        admin.setQuota(settings);
        Assert.assertEquals(1, getNumSpaceQuotas());
        Assert.assertEquals(1, getThrottleQuotas());
        // Delete the namespace and check that both the quotas have been dropped as well
        admin.deleteNamespace(ns);
        Assert.assertEquals(0, getNumSpaceQuotas());
        Assert.assertEquals(0, getThrottleQuotas());
    }

    @Test
    public void testObserverAddedByDefault() throws Exception {
        final HMaster master = TestMasterQuotasObserver.TEST_UTIL.getHBaseCluster().getMaster();
        final MasterCoprocessorHost cpHost = master.getMasterCoprocessorHost();
        Set<String> coprocessorNames = cpHost.getCoprocessors();
        Assert.assertTrue(("Did not find MasterQuotasObserver in list of CPs: " + coprocessorNames), coprocessorNames.contains(MasterQuotasObserver.class.getSimpleName()));
    }
}

