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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestQuotaState {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQuotaState.class);

    private static final TableName UNKNOWN_TABLE_NAME = TableName.valueOf("unknownTable");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testQuotaStateBypass() {
        QuotaState quotaInfo = new QuotaState();
        Assert.assertTrue(quotaInfo.isBypass());
        assertNoopLimiter(quotaInfo.getGlobalLimiter());
        UserQuotaState userQuotaState = new UserQuotaState();
        Assert.assertTrue(userQuotaState.isBypass());
        assertNoopLimiter(userQuotaState.getTableLimiter(TestQuotaState.UNKNOWN_TABLE_NAME));
    }

    @Test
    public void testSimpleQuotaStateOperation() {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final int NUM_GLOBAL_THROTTLE = 3;
        final int NUM_TABLE_THROTTLE = 2;
        UserQuotaState quotaInfo = new UserQuotaState();
        Assert.assertTrue(quotaInfo.isBypass());
        // Set global quota
        quotaInfo.setQuotas(buildReqNumThrottle(NUM_GLOBAL_THROTTLE));
        Assert.assertFalse(quotaInfo.isBypass());
        // Set table quota
        quotaInfo.setQuotas(tableName, buildReqNumThrottle(NUM_TABLE_THROTTLE));
        Assert.assertFalse(quotaInfo.isBypass());
        Assert.assertTrue(((quotaInfo.getGlobalLimiter()) == (quotaInfo.getTableLimiter(TestQuotaState.UNKNOWN_TABLE_NAME))));
        assertThrottleException(quotaInfo.getTableLimiter(TestQuotaState.UNKNOWN_TABLE_NAME), NUM_GLOBAL_THROTTLE);
        assertThrottleException(quotaInfo.getTableLimiter(tableName), NUM_TABLE_THROTTLE);
    }

    @Test
    public void testQuotaStateUpdateBypassThrottle() {
        final long LAST_UPDATE = 10;
        UserQuotaState quotaInfo = new UserQuotaState();
        Assert.assertEquals(0, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        UserQuotaState otherQuotaState = new UserQuotaState(LAST_UPDATE);
        Assert.assertEquals(LAST_UPDATE, otherQuotaState.getLastUpdate());
        Assert.assertTrue(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        Assert.assertTrue(((quotaInfo.getGlobalLimiter()) == (quotaInfo.getTableLimiter(TestQuotaState.UNKNOWN_TABLE_NAME))));
        assertNoopLimiter(quotaInfo.getTableLimiter(TestQuotaState.UNKNOWN_TABLE_NAME));
    }

    @Test
    public void testQuotaStateUpdateGlobalThrottle() {
        final int NUM_GLOBAL_THROTTLE_1 = 3;
        final int NUM_GLOBAL_THROTTLE_2 = 11;
        final long LAST_UPDATE_1 = 10;
        final long LAST_UPDATE_2 = 20;
        final long LAST_UPDATE_3 = 30;
        QuotaState quotaInfo = new QuotaState();
        Assert.assertEquals(0, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        // Add global throttle
        QuotaState otherQuotaState = new QuotaState(LAST_UPDATE_1);
        otherQuotaState.setQuotas(buildReqNumThrottle(NUM_GLOBAL_THROTTLE_1));
        Assert.assertEquals(LAST_UPDATE_1, otherQuotaState.getLastUpdate());
        Assert.assertFalse(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_1, quotaInfo.getLastUpdate());
        Assert.assertFalse(quotaInfo.isBypass());
        assertThrottleException(quotaInfo.getGlobalLimiter(), NUM_GLOBAL_THROTTLE_1);
        // Update global Throttle
        otherQuotaState = new QuotaState(LAST_UPDATE_2);
        otherQuotaState.setQuotas(buildReqNumThrottle(NUM_GLOBAL_THROTTLE_2));
        Assert.assertEquals(LAST_UPDATE_2, otherQuotaState.getLastUpdate());
        Assert.assertFalse(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_2, quotaInfo.getLastUpdate());
        Assert.assertFalse(quotaInfo.isBypass());
        assertThrottleException(quotaInfo.getGlobalLimiter(), (NUM_GLOBAL_THROTTLE_2 - NUM_GLOBAL_THROTTLE_1));
        // Remove global throttle
        otherQuotaState = new QuotaState(LAST_UPDATE_3);
        Assert.assertEquals(LAST_UPDATE_3, otherQuotaState.getLastUpdate());
        Assert.assertTrue(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_3, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        assertNoopLimiter(quotaInfo.getGlobalLimiter());
    }

    @Test
    public void testQuotaStateUpdateTableThrottle() {
        final TableName tableNameA = TableName.valueOf(((name.getMethodName()) + "A"));
        final TableName tableNameB = TableName.valueOf(((name.getMethodName()) + "B"));
        final TableName tableNameC = TableName.valueOf(((name.getMethodName()) + "C"));
        final int TABLE_A_THROTTLE_1 = 3;
        final int TABLE_A_THROTTLE_2 = 11;
        final int TABLE_B_THROTTLE = 4;
        final int TABLE_C_THROTTLE = 5;
        final long LAST_UPDATE_1 = 10;
        final long LAST_UPDATE_2 = 20;
        final long LAST_UPDATE_3 = 30;
        UserQuotaState quotaInfo = new UserQuotaState();
        Assert.assertEquals(0, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        // Add A B table limiters
        UserQuotaState otherQuotaState = new UserQuotaState(LAST_UPDATE_1);
        otherQuotaState.setQuotas(tableNameA, buildReqNumThrottle(TABLE_A_THROTTLE_1));
        otherQuotaState.setQuotas(tableNameB, buildReqNumThrottle(TABLE_B_THROTTLE));
        Assert.assertEquals(LAST_UPDATE_1, otherQuotaState.getLastUpdate());
        Assert.assertFalse(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_1, quotaInfo.getLastUpdate());
        Assert.assertFalse(quotaInfo.isBypass());
        assertThrottleException(quotaInfo.getTableLimiter(tableNameA), TABLE_A_THROTTLE_1);
        assertThrottleException(quotaInfo.getTableLimiter(tableNameB), TABLE_B_THROTTLE);
        assertNoopLimiter(quotaInfo.getTableLimiter(tableNameC));
        // Add C, Remove B, Update A table limiters
        otherQuotaState = new UserQuotaState(LAST_UPDATE_2);
        otherQuotaState.setQuotas(tableNameA, buildReqNumThrottle(TABLE_A_THROTTLE_2));
        otherQuotaState.setQuotas(tableNameC, buildReqNumThrottle(TABLE_C_THROTTLE));
        Assert.assertEquals(LAST_UPDATE_2, otherQuotaState.getLastUpdate());
        Assert.assertFalse(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_2, quotaInfo.getLastUpdate());
        Assert.assertFalse(quotaInfo.isBypass());
        assertThrottleException(quotaInfo.getTableLimiter(tableNameA), (TABLE_A_THROTTLE_2 - TABLE_A_THROTTLE_1));
        assertThrottleException(quotaInfo.getTableLimiter(tableNameC), TABLE_C_THROTTLE);
        assertNoopLimiter(quotaInfo.getTableLimiter(tableNameB));
        // Remove table limiters
        otherQuotaState = new UserQuotaState(LAST_UPDATE_3);
        Assert.assertEquals(LAST_UPDATE_3, otherQuotaState.getLastUpdate());
        Assert.assertTrue(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_3, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        assertNoopLimiter(quotaInfo.getTableLimiter(TestQuotaState.UNKNOWN_TABLE_NAME));
    }

    @Test(timeout = 60000)
    public void testTableThrottleWithBatch() {
        final TableName TABLE_A = TableName.valueOf("TableA");
        final int TABLE_A_THROTTLE_1 = 3;
        final long LAST_UPDATE_1 = 10;
        UserQuotaState quotaInfo = new UserQuotaState();
        Assert.assertEquals(0, quotaInfo.getLastUpdate());
        Assert.assertTrue(quotaInfo.isBypass());
        // Add A table limiters
        UserQuotaState otherQuotaState = new UserQuotaState(LAST_UPDATE_1);
        otherQuotaState.setQuotas(TABLE_A, buildReqNumThrottle(TABLE_A_THROTTLE_1));
        Assert.assertEquals(LAST_UPDATE_1, otherQuotaState.getLastUpdate());
        Assert.assertFalse(otherQuotaState.isBypass());
        quotaInfo.update(otherQuotaState);
        Assert.assertEquals(LAST_UPDATE_1, quotaInfo.getLastUpdate());
        Assert.assertFalse(quotaInfo.isBypass());
        QuotaLimiter limiter = quotaInfo.getTableLimiter(TABLE_A);
        try {
            limiter.checkQuota((TABLE_A_THROTTLE_1 + 1), (TABLE_A_THROTTLE_1 + 1), 0, 0, 1, 0);
            Assert.fail("Should have thrown RpcThrottlingException");
        } catch (RpcThrottlingException e) {
            // expected
        }
    }
}

