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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.quotas.policies.DefaultViolationPolicyEnforcement;
import org.apache.hadoop.hbase.quotas.policies.DisableTableViolationPolicyEnforcement;
import org.apache.hadoop.hbase.quotas.policies.NoInsertsViolationPolicyEnforcement;
import org.apache.hadoop.hbase.quotas.policies.NoWritesCompactionsViolationPolicyEnforcement;
import org.apache.hadoop.hbase.quotas.policies.NoWritesViolationPolicyEnforcement;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import static SpaceViolationPolicy.DISABLE;
import static SpaceViolationPolicy.NO_INSERTS;
import static SpaceViolationPolicy.NO_WRITES;
import static SpaceViolationPolicy.NO_WRITES_COMPACTIONS;


/**
 * Test class for {@link RegionServerSpaceQuotaManager}.
 */
@Category(SmallTests.class)
public class TestRegionServerSpaceQuotaManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerSpaceQuotaManager.class);

    private RegionServerSpaceQuotaManager quotaManager;

    private RegionServerServices rss;

    @Test
    public void testSpacePoliciesFromEnforcements() {
        final Map<TableName, SpaceViolationPolicyEnforcement> enforcements = new HashMap<>();
        final Map<TableName, SpaceQuotaSnapshot> expectedPolicies = new HashMap<>();
        Mockito.when(quotaManager.copyActiveEnforcements()).thenReturn(enforcements);
        Mockito.when(quotaManager.getActivePoliciesAsMap()).thenCallRealMethod();
        NoInsertsViolationPolicyEnforcement noInsertsPolicy = new NoInsertsViolationPolicyEnforcement();
        SpaceQuotaSnapshot noInsertsSnapshot = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(NO_INSERTS), 256L, 1024L);
        noInsertsPolicy.initialize(rss, TableName.valueOf("no_inserts"), noInsertsSnapshot);
        enforcements.put(noInsertsPolicy.getTableName(), noInsertsPolicy);
        expectedPolicies.put(noInsertsPolicy.getTableName(), noInsertsSnapshot);
        NoWritesViolationPolicyEnforcement noWritesPolicy = new NoWritesViolationPolicyEnforcement();
        SpaceQuotaSnapshot noWritesSnapshot = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(NO_WRITES), 512L, 2048L);
        noWritesPolicy.initialize(rss, TableName.valueOf("no_writes"), noWritesSnapshot);
        enforcements.put(noWritesPolicy.getTableName(), noWritesPolicy);
        expectedPolicies.put(noWritesPolicy.getTableName(), noWritesSnapshot);
        NoWritesCompactionsViolationPolicyEnforcement noWritesCompactionsPolicy = new NoWritesCompactionsViolationPolicyEnforcement();
        SpaceQuotaSnapshot noWritesCompactionsSnapshot = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(NO_WRITES_COMPACTIONS), 1024L, 4096L);
        noWritesCompactionsPolicy.initialize(rss, TableName.valueOf("no_writes_compactions"), noWritesCompactionsSnapshot);
        enforcements.put(noWritesCompactionsPolicy.getTableName(), noWritesCompactionsPolicy);
        expectedPolicies.put(noWritesCompactionsPolicy.getTableName(), noWritesCompactionsSnapshot);
        DisableTableViolationPolicyEnforcement disablePolicy = new DisableTableViolationPolicyEnforcement();
        SpaceQuotaSnapshot disableSnapshot = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(DISABLE), 2048L, 8192L);
        disablePolicy.initialize(rss, TableName.valueOf("disable"), disableSnapshot);
        enforcements.put(disablePolicy.getTableName(), disablePolicy);
        expectedPolicies.put(disablePolicy.getTableName(), disableSnapshot);
        enforcements.put(TableName.valueOf("no_policy"), new DefaultViolationPolicyEnforcement());
        Map<TableName, SpaceQuotaSnapshot> actualPolicies = quotaManager.getActivePoliciesAsMap();
        Assert.assertEquals(expectedPolicies, actualPolicies);
    }

    @Test
    public void testExceptionOnPolicyEnforcementEnable() throws Exception {
        final TableName tableName = TableName.valueOf("foo");
        final SpaceQuotaSnapshot snapshot = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(DISABLE), 1024L, 2048L);
        RegionServerServices rss = Mockito.mock(RegionServerServices.class);
        SpaceViolationPolicyEnforcementFactory factory = Mockito.mock(SpaceViolationPolicyEnforcementFactory.class);
        SpaceViolationPolicyEnforcement enforcement = Mockito.mock(SpaceViolationPolicyEnforcement.class);
        RegionServerSpaceQuotaManager realManager = new RegionServerSpaceQuotaManager(rss, factory);
        Mockito.when(factory.create(rss, tableName, snapshot)).thenReturn(enforcement);
        Mockito.doThrow(new IOException("Failed for test!")).when(enforcement).enable();
        realManager.enforceViolationPolicy(tableName, snapshot);
        Map<TableName, SpaceViolationPolicyEnforcement> enforcements = realManager.copyActiveEnforcements();
        Assert.assertTrue(("Expected active enforcements to be empty, but were " + enforcements), enforcements.isEmpty());
    }

    @Test
    public void testExceptionOnPolicyEnforcementDisable() throws Exception {
        final TableName tableName = TableName.valueOf("foo");
        final SpaceQuotaSnapshot snapshot = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(DISABLE), 1024L, 2048L);
        RegionServerServices rss = Mockito.mock(RegionServerServices.class);
        SpaceViolationPolicyEnforcementFactory factory = Mockito.mock(SpaceViolationPolicyEnforcementFactory.class);
        SpaceViolationPolicyEnforcement enforcement = Mockito.mock(SpaceViolationPolicyEnforcement.class);
        RegionServerSpaceQuotaManager realManager = new RegionServerSpaceQuotaManager(rss, factory);
        Mockito.when(factory.create(rss, tableName, snapshot)).thenReturn(enforcement);
        Mockito.doNothing().when(enforcement).enable();
        Mockito.doThrow(new IOException("Failed for test!")).when(enforcement).disable();
        // Enabling should work
        realManager.enforceViolationPolicy(tableName, snapshot);
        Map<TableName, SpaceViolationPolicyEnforcement> enforcements = realManager.copyActiveEnforcements();
        Assert.assertEquals(1, enforcements.size());
        // If the disable fails, we should still treat it as "active"
        realManager.disableViolationPolicyEnforcement(tableName);
        enforcements = realManager.copyActiveEnforcements();
        Assert.assertEquals(1, enforcements.size());
    }
}

