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


import QuotaUtil.QUOTA_TABLE_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static SpaceViolationPolicy.DISABLE;
import static SpaceViolationPolicy.NO_INSERTS;
import static SpaceViolationPolicy.NO_WRITES;
import static SpaceViolationPolicy.NO_WRITES_COMPACTIONS;


/**
 * Test class for {@link SpaceQuotaRefresherChore}.
 */
@Category(SmallTests.class)
public class TestSpaceQuotaViolationPolicyRefresherChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSpaceQuotaViolationPolicyRefresherChore.class);

    private RegionServerSpaceQuotaManager manager;

    private RegionServerServices rss;

    private SpaceQuotaRefresherChore chore;

    private Configuration conf;

    private Connection conn;

    @Test
    public void testPoliciesAreEnforced() throws IOException {
        // Create a number of policies that should be enforced (usage > limit)
        final Map<TableName, SpaceQuotaSnapshot> policiesToEnforce = new HashMap<>();
        policiesToEnforce.put(TableName.valueOf("table1"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(DISABLE), 1024L, 512L));
        policiesToEnforce.put(TableName.valueOf("table2"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_INSERTS), 2048L, 512L));
        policiesToEnforce.put(TableName.valueOf("table3"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_WRITES), 4096L, 512L));
        policiesToEnforce.put(TableName.valueOf("table4"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_WRITES_COMPACTIONS), 8192L, 512L));
        // No active enforcements
        Mockito.when(manager.copyQuotaSnapshots()).thenReturn(Collections.emptyMap());
        // Policies to enforce
        Mockito.when(chore.fetchSnapshotsFromQuotaTable()).thenReturn(policiesToEnforce);
        chore.chore();
        for (Map.Entry<TableName, SpaceQuotaSnapshot> entry : policiesToEnforce.entrySet()) {
            // Ensure we enforce the policy
            Mockito.verify(manager).enforceViolationPolicy(entry.getKey(), entry.getValue());
            // Don't disable any policies
            Mockito.verify(manager, Mockito.never()).disableViolationPolicyEnforcement(entry.getKey());
        }
    }

    @Test
    public void testOldPoliciesAreRemoved() throws IOException {
        final Map<TableName, SpaceQuotaSnapshot> previousPolicies = new HashMap<>();
        previousPolicies.put(TableName.valueOf("table3"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_WRITES), 4096L, 512L));
        previousPolicies.put(TableName.valueOf("table4"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_WRITES), 8192L, 512L));
        final Map<TableName, SpaceQuotaSnapshot> policiesToEnforce = new HashMap<>();
        policiesToEnforce.put(TableName.valueOf("table1"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(DISABLE), 1024L, 512L));
        policiesToEnforce.put(TableName.valueOf("table2"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_INSERTS), 2048L, 512L));
        policiesToEnforce.put(TableName.valueOf("table3"), new SpaceQuotaSnapshot(SpaceQuotaStatus.notInViolation(), 256L, 512L));
        policiesToEnforce.put(TableName.valueOf("table4"), new SpaceQuotaSnapshot(SpaceQuotaStatus.notInViolation(), 128L, 512L));
        // No active enforcements
        Mockito.when(manager.copyQuotaSnapshots()).thenReturn(previousPolicies);
        // Policies to enforce
        Mockito.when(chore.fetchSnapshotsFromQuotaTable()).thenReturn(policiesToEnforce);
        chore.chore();
        Mockito.verify(manager).enforceViolationPolicy(TableName.valueOf("table1"), policiesToEnforce.get(TableName.valueOf("table1")));
        Mockito.verify(manager).enforceViolationPolicy(TableName.valueOf("table2"), policiesToEnforce.get(TableName.valueOf("table2")));
        Mockito.verify(manager).disableViolationPolicyEnforcement(TableName.valueOf("table3"));
        Mockito.verify(manager).disableViolationPolicyEnforcement(TableName.valueOf("table4"));
    }

    @Test
    public void testNewPolicyOverridesOld() throws IOException {
        final Map<TableName, SpaceQuotaSnapshot> policiesToEnforce = new HashMap<>();
        policiesToEnforce.put(TableName.valueOf("table1"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(DISABLE), 1024L, 512L));
        policiesToEnforce.put(TableName.valueOf("table2"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_WRITES), 2048L, 512L));
        policiesToEnforce.put(TableName.valueOf("table3"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_INSERTS), 4096L, 512L));
        final Map<TableName, SpaceQuotaSnapshot> previousPolicies = new HashMap<>();
        previousPolicies.put(TableName.valueOf("table1"), new SpaceQuotaSnapshot(new SpaceQuotaStatus(NO_WRITES), 8192L, 512L));
        // No active enforcements
        Mockito.when(manager.getActivePoliciesAsMap()).thenReturn(previousPolicies);
        // Policies to enforce
        Mockito.when(chore.fetchSnapshotsFromQuotaTable()).thenReturn(policiesToEnforce);
        chore.chore();
        for (Map.Entry<TableName, SpaceQuotaSnapshot> entry : policiesToEnforce.entrySet()) {
            Mockito.verify(manager).enforceViolationPolicy(entry.getKey(), entry.getValue());
        }
        Mockito.verify(manager, Mockito.never()).disableViolationPolicyEnforcement(TableName.valueOf("table1"));
    }

    @Test
    public void testMissingAllColumns() throws IOException {
        Mockito.when(chore.fetchSnapshotsFromQuotaTable()).thenCallRealMethod();
        ResultScanner scanner = Mockito.mock(ResultScanner.class);
        Table quotaTable = Mockito.mock(Table.class);
        Mockito.when(conn.getTable(QUOTA_TABLE_NAME)).thenReturn(quotaTable);
        Mockito.when(quotaTable.getScanner(ArgumentMatchers.any(Scan.class))).thenReturn(scanner);
        List<Result> results = new ArrayList<>();
        results.add(Result.create(Collections.emptyList()));
        Mockito.when(scanner.iterator()).thenReturn(results.iterator());
        try {
            chore.fetchSnapshotsFromQuotaTable();
            Assert.fail("Expected an IOException, but did not receive one.");
        } catch (IOException e) {
            // Expected an error because we had no cells in the row.
            // This should only happen due to programmer error.
        }
    }

    @Test
    public void testMissingDesiredColumn() throws IOException {
        Mockito.when(chore.fetchSnapshotsFromQuotaTable()).thenCallRealMethod();
        ResultScanner scanner = Mockito.mock(ResultScanner.class);
        Table quotaTable = Mockito.mock(Table.class);
        Mockito.when(conn.getTable(QUOTA_TABLE_NAME)).thenReturn(quotaTable);
        Mockito.when(quotaTable.getScanner(ArgumentMatchers.any(Scan.class))).thenReturn(scanner);
        List<Result> results = new ArrayList<>();
        // Give a column that isn't the one we want
        Cell c = new org.apache.hadoop.hbase.KeyValue(Bytes.toBytes("t:inviolation"), Bytes.toBytes("q"), Bytes.toBytes("s"), new byte[0]);
        results.add(Result.create(Collections.singletonList(c)));
        Mockito.when(scanner.iterator()).thenReturn(results.iterator());
        try {
            chore.fetchSnapshotsFromQuotaTable();
            Assert.fail("Expected an IOException, but did not receive one.");
        } catch (IOException e) {
            // Expected an error because we were missing the column we expected in this row.
            // This should only happen due to programmer error.
        }
    }

    @Test
    public void testParsingError() throws IOException {
        Mockito.when(chore.fetchSnapshotsFromQuotaTable()).thenCallRealMethod();
        ResultScanner scanner = Mockito.mock(ResultScanner.class);
        Table quotaTable = Mockito.mock(Table.class);
        Mockito.when(conn.getTable(QUOTA_TABLE_NAME)).thenReturn(quotaTable);
        Mockito.when(quotaTable.getScanner(ArgumentMatchers.any(Scan.class))).thenReturn(scanner);
        List<Result> results = new ArrayList<>();
        Cell c = new org.apache.hadoop.hbase.KeyValue(Bytes.toBytes("t:inviolation"), Bytes.toBytes("u"), Bytes.toBytes("v"), new byte[0]);
        results.add(Result.create(Collections.singletonList(c)));
        Mockito.when(scanner.iterator()).thenReturn(results.iterator());
        try {
            chore.fetchSnapshotsFromQuotaTable();
            Assert.fail("Expected an IOException, but did not receive one.");
        } catch (IOException e) {
            // We provided a garbage serialized protobuf message (empty byte array), this should
            // in turn throw an IOException
        }
    }
}

