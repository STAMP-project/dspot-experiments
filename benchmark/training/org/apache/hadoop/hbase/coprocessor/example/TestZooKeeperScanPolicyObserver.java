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
package org.apache.hadoop.hbase.coprocessor.example;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.KeeperException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestZooKeeperScanPolicyObserver {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZooKeeperScanPolicyObserver.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName NAME = TableName.valueOf("TestCP");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUALIFIER = Bytes.toBytes("cq");

    private static Table TABLE;

    @Test
    public void test() throws IOException, InterruptedException, KeeperException {
        long now = System.currentTimeMillis();
        put(0, 100, (now - 10000));
        assertValueEquals(0, 100);
        setExpireBefore((now - 5000));
        Thread.sleep(5000);
        TestZooKeeperScanPolicyObserver.UTIL.getAdmin().flush(TestZooKeeperScanPolicyObserver.NAME);
        assertNotExists(0, 100);
        put(0, 50, (now - 1000));
        TestZooKeeperScanPolicyObserver.UTIL.getAdmin().flush(TestZooKeeperScanPolicyObserver.NAME);
        put(50, 100, (now - 100));
        TestZooKeeperScanPolicyObserver.UTIL.getAdmin().flush(TestZooKeeperScanPolicyObserver.NAME);
        assertValueEquals(0, 100);
        setExpireBefore((now - 500));
        Thread.sleep(5000);
        TestZooKeeperScanPolicyObserver.UTIL.getAdmin().majorCompact(TestZooKeeperScanPolicyObserver.NAME);
        TestZooKeeperScanPolicyObserver.UTIL.waitFor(30000, () -> (UTIL.getHBaseCluster().getRegions(TestZooKeeperScanPolicyObserver.NAME).iterator().next().getStore(TestZooKeeperScanPolicyObserver.FAMILY).getStorefilesCount()) == 1);
        assertNotExists(0, 50);
        assertValueEquals(50, 100);
    }
}

