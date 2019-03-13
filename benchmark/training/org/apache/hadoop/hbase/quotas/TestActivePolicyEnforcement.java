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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus;
import org.apache.hadoop.hbase.quotas.policies.DefaultViolationPolicyEnforcement;
import org.apache.hadoop.hbase.quotas.policies.MissingSnapshotViolationPolicyEnforcement;
import org.apache.hadoop.hbase.quotas.policies.NoWritesViolationPolicyEnforcement;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Test class for {@link ActivePolicyEnforcement}.
 */
@Category(SmallTests.class)
public class TestActivePolicyEnforcement {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestActivePolicyEnforcement.class);

    private RegionServerServices rss;

    @Test
    public void testGetter() {
        final TableName tableName = TableName.valueOf("table");
        Map<TableName, SpaceViolationPolicyEnforcement> map = new HashMap<>();
        map.put(tableName, new NoWritesViolationPolicyEnforcement());
        ActivePolicyEnforcement ape = new ActivePolicyEnforcement(map, Collections.emptyMap(), null);
        Assert.assertEquals(map.get(tableName), ape.getPolicyEnforcement(tableName));
    }

    @Test
    public void testNoPolicyReturnsNoopEnforcement() {
        ActivePolicyEnforcement ape = new ActivePolicyEnforcement(new HashMap(), Collections.emptyMap(), Mockito.mock(RegionServerServices.class));
        SpaceViolationPolicyEnforcement enforcement = ape.getPolicyEnforcement(TableName.valueOf("nonexistent"));
        Assert.assertNotNull(enforcement);
        Assert.assertTrue(("Expected an instance of MissingSnapshotViolationPolicyEnforcement, but got " + (enforcement.getClass())), (enforcement instanceof MissingSnapshotViolationPolicyEnforcement));
    }

    @Test
    public void testNoBulkLoadChecksOnNoSnapshot() {
        ActivePolicyEnforcement ape = new ActivePolicyEnforcement(new HashMap<TableName, SpaceViolationPolicyEnforcement>(), Collections.<TableName, SpaceQuotaSnapshot>emptyMap(), Mockito.mock(RegionServerServices.class));
        SpaceViolationPolicyEnforcement enforcement = ape.getPolicyEnforcement(TableName.valueOf("nonexistent"));
        Assert.assertFalse("Should not check bulkloads", enforcement.shouldCheckBulkLoads());
    }

    @Test
    public void testNoQuotaReturnsSingletonPolicyEnforcement() {
        final ActivePolicyEnforcement ape = new ActivePolicyEnforcement(Collections.emptyMap(), Collections.emptyMap(), rss);
        final TableName tableName = TableName.valueOf("my_table");
        SpaceViolationPolicyEnforcement policyEnforcement = ape.getPolicyEnforcement(tableName);
        // This should be the same exact instance, the singleton
        Assert.assertTrue((policyEnforcement == (MissingSnapshotViolationPolicyEnforcement.getInstance())));
        Assert.assertEquals(1, ape.getLocallyCachedPolicies().size());
        Map.Entry<TableName, SpaceViolationPolicyEnforcement> entry = ape.getLocallyCachedPolicies().entrySet().iterator().next();
        Assert.assertTrue((policyEnforcement == (entry.getValue())));
    }

    @Test
    public void testNonViolatingQuotaCachesPolicyEnforcment() {
        final Map<TableName, SpaceQuotaSnapshot> snapshots = new HashMap<>();
        final TableName tableName = TableName.valueOf("my_table");
        snapshots.put(tableName, new SpaceQuotaSnapshot(SpaceQuotaStatus.notInViolation(), 0, 1024));
        final ActivePolicyEnforcement ape = new ActivePolicyEnforcement(Collections.emptyMap(), snapshots, rss);
        SpaceViolationPolicyEnforcement policyEnforcement = ape.getPolicyEnforcement(tableName);
        Assert.assertTrue(("Found the wrong class: " + (policyEnforcement.getClass())), (policyEnforcement instanceof DefaultViolationPolicyEnforcement));
        SpaceViolationPolicyEnforcement copy = ape.getPolicyEnforcement(tableName);
        Assert.assertTrue("Expected the instance to be cached", (policyEnforcement == copy));
        Map.Entry<TableName, SpaceViolationPolicyEnforcement> entry = ape.getLocallyCachedPolicies().entrySet().iterator().next();
        Assert.assertTrue((policyEnforcement == (entry.getValue())));
    }

    @Test
    public void testViolatingQuotaCachesNothing() {
        final TableName tableName = TableName.valueOf("my_table");
        SpaceViolationPolicyEnforcement policyEnforcement = Mockito.mock(SpaceViolationPolicyEnforcement.class);
        final Map<TableName, SpaceViolationPolicyEnforcement> activePolicies = new HashMap<>();
        activePolicies.put(tableName, policyEnforcement);
        final ActivePolicyEnforcement ape = new ActivePolicyEnforcement(activePolicies, Collections.emptyMap(), rss);
        Assert.assertTrue(((ape.getPolicyEnforcement(tableName)) == policyEnforcement));
        Assert.assertEquals(0, ape.getLocallyCachedPolicies().size());
    }
}

