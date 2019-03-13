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


import HConstants.TABLE_FAMILY;
import TableName.META_TABLE_NAME;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.StartMiniClusterOption;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMasterRepairMode {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterRepairMode.class);

    @Rule
    public TestName name = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterRepairMode.class);

    private static final byte[] FAMILYNAME = Bytes.toBytes("fam");

    private static HBaseTestingUtility TEST_UTIL;

    @Test
    public void testNewCluster() throws Exception {
        enableMaintenanceMode();
        TestMasterRepairMode.TEST_UTIL.startMiniCluster(StartMiniClusterOption.builder().numRegionServers(0).numDataNodes(3).build());
        Connection conn = TestMasterRepairMode.TEST_UTIL.getConnection();
        Assert.assertTrue(conn.getAdmin().isMasterInMaintenanceMode());
        try (Table table = conn.getTable(META_TABLE_NAME);ResultScanner scanner = table.getScanner(new Scan())) {
            Assert.assertNotNull("Could not read meta.", scanner.next());
        }
    }

    @Test
    public void testExistingCluster() throws Exception {
        TableName testRepairMode = TableName.valueOf(name.getMethodName());
        TestMasterRepairMode.TEST_UTIL.startMiniCluster();
        Table t = TestMasterRepairMode.TEST_UTIL.createTable(testRepairMode, TestMasterRepairMode.FAMILYNAME);
        Put p = new Put(Bytes.toBytes("r"));
        p.addColumn(TestMasterRepairMode.FAMILYNAME, Bytes.toBytes("c"), new byte[0]);
        t.put(p);
        TestMasterRepairMode.TEST_UTIL.shutdownMiniHBaseCluster();
        TestMasterRepairMode.LOG.info("Starting master-only");
        enableMaintenanceMode();
        TestMasterRepairMode.TEST_UTIL.startMiniHBaseCluster(StartMiniClusterOption.builder().numRegionServers(0).createRootDir(false).build());
        Connection conn = TestMasterRepairMode.TEST_UTIL.getConnection();
        Assert.assertTrue(conn.getAdmin().isMasterInMaintenanceMode());
        try (Table table = conn.getTable(META_TABLE_NAME);ResultScanner scanner = table.getScanner(TABLE_FAMILY);Stream<Result> results = StreamSupport.stream(scanner.spliterator(), false)) {
            Assert.assertTrue("Did not find user table records while reading hbase:meta", results.anyMatch(( r) -> Arrays.equals(r.getRow(), testRepairMode.getName())));
        }
        try (Table table = conn.getTable(testRepairMode);ResultScanner scanner = table.getScanner(new Scan())) {
            scanner.next();
            Assert.fail("Should not be able to access user-space tables in repair mode.");
        } catch (Exception e) {
            // Expected
        }
    }
}

