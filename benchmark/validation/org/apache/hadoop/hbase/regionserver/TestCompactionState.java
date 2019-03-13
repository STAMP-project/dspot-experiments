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
package org.apache.hadoop.hbase.regionserver;


import CompactionState.MAJOR;
import CompactionState.MINOR;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowRegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests to test retrieving table/region compaction state
 */
@Category({ VerySlowRegionServerTests.class, LargeTests.class })
public class TestCompactionState {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactionState.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactionState.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Random random = new Random();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMajorCompaction() throws IOException, InterruptedException {
        compaction(name.getMethodName(), 8, MAJOR, false);
    }

    @Test
    public void testMinorCompaction() throws IOException, InterruptedException {
        compaction(name.getMethodName(), 15, MINOR, false);
    }

    @Test
    public void testMajorCompactionOnFamily() throws IOException, InterruptedException {
        compaction(name.getMethodName(), 8, MAJOR, true);
    }

    @Test
    public void testMinorCompactionOnFamily() throws IOException, InterruptedException {
        compaction(name.getMethodName(), 15, MINOR, true);
    }

    @Test
    public void testInvalidColumnFamily() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] family = Bytes.toBytes("family");
        byte[] fakecf = Bytes.toBytes("fakecf");
        boolean caughtMinorCompact = false;
        boolean caughtMajorCompact = false;
        Table ht = null;
        try {
            ht = TestCompactionState.TEST_UTIL.createTable(tableName, family);
            Admin admin = TestCompactionState.TEST_UTIL.getAdmin();
            try {
                admin.compact(tableName, fakecf);
            } catch (IOException ioe) {
                caughtMinorCompact = true;
            }
            try {
                admin.majorCompact(tableName, fakecf);
            } catch (IOException ioe) {
                caughtMajorCompact = true;
            }
        } finally {
            if (ht != null) {
                TestCompactionState.TEST_UTIL.deleteTable(tableName);
            }
            Assert.assertTrue(caughtMinorCompact);
            Assert.assertTrue(caughtMajorCompact);
        }
    }
}

