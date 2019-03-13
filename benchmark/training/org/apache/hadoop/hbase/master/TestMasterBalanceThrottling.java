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
package org.apache.hadoop.hbase.master;


import HConstants.HBASE_BALANCER_MAX_BALANCING;
import HConstants.HBASE_MASTER_BALANCER_MAX_RIT_PERCENT;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// SimpleLoadBalancer seems borked whether AMv2 or not. Disabling till gets attention.
@Ignore
@Category({ MasterTests.class, MediumTests.class })
public class TestMasterBalanceThrottling {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterBalanceThrottling.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] FAMILYNAME = Bytes.toBytes("fam");

    @Test
    public void testThrottlingByBalanceInterval() throws Exception {
        // Use default config and start a cluster of two regionservers.
        TestMasterBalanceThrottling.TEST_UTIL.startMiniCluster(2);
        TableName tableName = createTable("testNoThrottling");
        final HMaster master = TestMasterBalanceThrottling.TEST_UTIL.getHBaseCluster().getMaster();
        // Default max balancing time is 300000 ms and there are 50 regions to balance
        // The balance interval is 6000 ms, much longger than the normal region in transition duration
        // So the master can balance the region one by one
        unbalance(master, tableName);
        AtomicInteger maxCount = new AtomicInteger(0);
        AtomicBoolean stop = new AtomicBoolean(false);
        Thread checker = startBalancerChecker(master, maxCount, stop);
        master.balance();
        stop.set(true);
        checker.interrupt();
        checker.join();
        Assert.assertTrue(("max regions in transition: " + (maxCount.get())), ((maxCount.get()) == 1));
        TestMasterBalanceThrottling.TEST_UTIL.deleteTable(tableName);
    }

    @Test
    public void testThrottlingByMaxRitPercent() throws Exception {
        // Set max balancing time to 500 ms and max percent of regions in transition to 0.05
        TestMasterBalanceThrottling.TEST_UTIL.getConfiguration().setInt(HBASE_BALANCER_MAX_BALANCING, 500);
        TestMasterBalanceThrottling.TEST_UTIL.getConfiguration().setDouble(HBASE_MASTER_BALANCER_MAX_RIT_PERCENT, 0.05);
        TestMasterBalanceThrottling.TEST_UTIL.startMiniCluster(2);
        TableName tableName = createTable("testThrottlingByMaxRitPercent");
        final HMaster master = TestMasterBalanceThrottling.TEST_UTIL.getHBaseCluster().getMaster();
        unbalance(master, tableName);
        AtomicInteger maxCount = new AtomicInteger(0);
        AtomicBoolean stop = new AtomicBoolean(false);
        Thread checker = startBalancerChecker(master, maxCount, stop);
        master.balance();
        stop.set(true);
        checker.interrupt();
        checker.join();
        // The max number of regions in transition is 100 * 0.05 = 5
        Assert.assertTrue(("max regions in transition: " + (maxCount.get())), ((maxCount.get()) == 5));
        TestMasterBalanceThrottling.TEST_UTIL.deleteTable(tableName);
    }
}

