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


import QuotaScope.MACHINE;
import QuotaUtil.QUOTA_TABLE_NAME;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.Quotas;
import org.apache.hadoop.hbase.shaded.protobuf.generated.QuotaProtos.Throttle;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import static SpaceViolationPolicy.DISABLE;
import static SpaceViolationPolicy.NO_INSERTS;
import static SpaceViolationPolicy.NO_WRITES;


/**
 * Test the quota table helpers (e.g. CRUD operations)
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestQuotaTableUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaTableUtil.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Connection connection;

    private int tableNameCounter;

    @Rule
    public TestName testName = new TestName();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testTableQuotaUtil() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Quotas quota = Quotas.newBuilder().setThrottle(Throttle.newBuilder().setReqNum(ProtobufUtil.toTimedQuota(1000, TimeUnit.SECONDS, MACHINE)).setWriteNum(ProtobufUtil.toTimedQuota(600, TimeUnit.SECONDS, MACHINE)).setReadSize(ProtobufUtil.toTimedQuota(8192, TimeUnit.SECONDS, MACHINE)).build()).build();
        // Add user quota and verify it
        QuotaUtil.addTableQuota(this.connection, tableName, quota);
        Quotas resQuota = QuotaUtil.getTableQuota(this.connection, tableName);
        Assert.assertEquals(quota, resQuota);
        // Remove user quota and verify it
        QuotaUtil.deleteTableQuota(this.connection, tableName);
        resQuota = QuotaUtil.getTableQuota(this.connection, tableName);
        Assert.assertEquals(null, resQuota);
    }

    @Test
    public void testNamespaceQuotaUtil() throws Exception {
        final String namespace = "testNamespaceQuotaUtilNS";
        Quotas quota = Quotas.newBuilder().setThrottle(Throttle.newBuilder().setReqNum(ProtobufUtil.toTimedQuota(1000, TimeUnit.SECONDS, MACHINE)).setWriteNum(ProtobufUtil.toTimedQuota(600, TimeUnit.SECONDS, MACHINE)).setReadSize(ProtobufUtil.toTimedQuota(8192, TimeUnit.SECONDS, MACHINE)).build()).build();
        // Add user quota and verify it
        QuotaUtil.addNamespaceQuota(this.connection, namespace, quota);
        Quotas resQuota = QuotaUtil.getNamespaceQuota(this.connection, namespace);
        Assert.assertEquals(quota, resQuota);
        // Remove user quota and verify it
        QuotaUtil.deleteNamespaceQuota(this.connection, namespace);
        resQuota = QuotaUtil.getNamespaceQuota(this.connection, namespace);
        Assert.assertEquals(null, resQuota);
    }

    @Test
    public void testUserQuotaUtil() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final String namespace = "testNS";
        final String user = "testUser";
        Quotas quotaNamespace = Quotas.newBuilder().setThrottle(Throttle.newBuilder().setReqNum(ProtobufUtil.toTimedQuota(50000, TimeUnit.SECONDS, MACHINE)).build()).build();
        Quotas quotaTable = Quotas.newBuilder().setThrottle(Throttle.newBuilder().setReqNum(ProtobufUtil.toTimedQuota(1000, TimeUnit.SECONDS, MACHINE)).setWriteNum(ProtobufUtil.toTimedQuota(600, TimeUnit.SECONDS, MACHINE)).setReadSize(ProtobufUtil.toTimedQuota(10000, TimeUnit.SECONDS, MACHINE)).build()).build();
        Quotas quota = Quotas.newBuilder().setThrottle(Throttle.newBuilder().setReqSize(ProtobufUtil.toTimedQuota(8192, TimeUnit.SECONDS, MACHINE)).setWriteSize(ProtobufUtil.toTimedQuota(4096, TimeUnit.SECONDS, MACHINE)).setReadNum(ProtobufUtil.toTimedQuota(1000, TimeUnit.SECONDS, MACHINE)).build()).build();
        // Add user global quota
        QuotaUtil.addUserQuota(this.connection, user, quota);
        Quotas resQuota = QuotaUtil.getUserQuota(this.connection, user);
        Assert.assertEquals(quota, resQuota);
        // Add user quota for table
        QuotaUtil.addUserQuota(this.connection, user, tableName, quotaTable);
        Quotas resQuotaTable = QuotaUtil.getUserQuota(this.connection, user, tableName);
        Assert.assertEquals(quotaTable, resQuotaTable);
        // Add user quota for namespace
        QuotaUtil.addUserQuota(this.connection, user, namespace, quotaNamespace);
        Quotas resQuotaNS = QuotaUtil.getUserQuota(this.connection, user, namespace);
        Assert.assertEquals(quotaNamespace, resQuotaNS);
        // Delete user global quota
        QuotaUtil.deleteUserQuota(this.connection, user);
        resQuota = QuotaUtil.getUserQuota(this.connection, user);
        Assert.assertEquals(null, resQuota);
        // Delete user quota for table
        QuotaUtil.deleteUserQuota(this.connection, user, tableName);
        resQuotaTable = QuotaUtil.getUserQuota(this.connection, user, tableName);
        Assert.assertEquals(null, resQuotaTable);
        // Delete user quota for namespace
        QuotaUtil.deleteUserQuota(this.connection, user, namespace);
        resQuotaNS = QuotaUtil.getUserQuota(this.connection, user, namespace);
        Assert.assertEquals(null, resQuotaNS);
    }

    @Test
    public void testSerDeViolationPolicies() throws Exception {
        final TableName tn1 = getUniqueTableName();
        final SpaceQuotaSnapshot snapshot1 = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(DISABLE), 512L, 1024L);
        final TableName tn2 = getUniqueTableName();
        final SpaceQuotaSnapshot snapshot2 = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(NO_INSERTS), 512L, 1024L);
        final TableName tn3 = getUniqueTableName();
        final SpaceQuotaSnapshot snapshot3 = new SpaceQuotaSnapshot(new org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus(NO_WRITES), 512L, 1024L);
        List<Put> puts = new ArrayList<>();
        puts.add(QuotaTableUtil.createPutForSpaceSnapshot(tn1, snapshot1));
        puts.add(QuotaTableUtil.createPutForSpaceSnapshot(tn2, snapshot2));
        puts.add(QuotaTableUtil.createPutForSpaceSnapshot(tn3, snapshot3));
        final Map<TableName, SpaceQuotaSnapshot> expectedPolicies = new HashMap<>();
        expectedPolicies.put(tn1, snapshot1);
        expectedPolicies.put(tn2, snapshot2);
        expectedPolicies.put(tn3, snapshot3);
        final Map<TableName, SpaceQuotaSnapshot> actualPolicies = new HashMap<>();
        try (Table quotaTable = connection.getTable(QUOTA_TABLE_NAME)) {
            quotaTable.put(puts);
            ResultScanner scanner = quotaTable.getScanner(QuotaTableUtil.makeQuotaSnapshotScan());
            for (Result r : scanner) {
                QuotaTableUtil.extractQuotaSnapshot(r, actualPolicies);
            }
            scanner.close();
        }
        Assert.assertEquals(expectedPolicies, actualPolicies);
    }

    @Test
    public void testSerdeTableSnapshotSizes() throws Exception {
        TableName tn1 = TableName.valueOf("tn1");
        TableName tn2 = TableName.valueOf("tn2");
        try (Table quotaTable = connection.getTable(QuotaTableUtil.QUOTA_TABLE_NAME)) {
            for (int i = 0; i < 3; i++) {
                Put p = QuotaTableUtil.createPutForSnapshotSize(tn1, ("tn1snap" + i), (1024L * (1 + i)));
                quotaTable.put(p);
            }
            for (int i = 0; i < 3; i++) {
                Put p = QuotaTableUtil.createPutForSnapshotSize(tn2, ("tn2snap" + i), (2048L * (1 + i)));
                quotaTable.put(p);
            }
            verifyTableSnapshotSize(quotaTable, tn1, "tn1snap0", 1024L);
            verifyTableSnapshotSize(quotaTable, tn1, "tn1snap1", 2048L);
            verifyTableSnapshotSize(quotaTable, tn1, "tn1snap2", 3072L);
            verifyTableSnapshotSize(quotaTable, tn2, "tn2snap0", 2048L);
            verifyTableSnapshotSize(quotaTable, tn2, "tn2snap1", 4096L);
            verifyTableSnapshotSize(quotaTable, tn2, "tn2snap2", 6144L);
        }
    }

    @Test
    public void testReadNamespaceSnapshotSizes() throws Exception {
        String ns1 = "ns1";
        String ns2 = "ns2";
        String defaultNs = NamespaceDescriptor.DEFAULT_NAMESPACE_NAME_STR;
        try (Table quotaTable = connection.getTable(QuotaTableUtil.QUOTA_TABLE_NAME)) {
            quotaTable.put(QuotaTableUtil.createPutForNamespaceSnapshotSize(ns1, 1024L));
            quotaTable.put(QuotaTableUtil.createPutForNamespaceSnapshotSize(ns2, 2048L));
            quotaTable.put(QuotaTableUtil.createPutForNamespaceSnapshotSize(defaultNs, 8192L));
            Assert.assertEquals(1024L, QuotaTableUtil.getNamespaceSnapshotSize(connection, ns1));
            Assert.assertEquals(2048L, QuotaTableUtil.getNamespaceSnapshotSize(connection, ns2));
            Assert.assertEquals(8192L, QuotaTableUtil.getNamespaceSnapshotSize(connection, defaultNs));
        }
    }
}

