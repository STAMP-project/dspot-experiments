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
package org.apache.hadoop.hbase.client;


import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestSizeFailures {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSizeFailures.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSizeFailures.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    protected static int SLAVES = 1;

    private static TableName TABLENAME;

    private static final int NUM_ROWS = 1000 * 1000;

    private static final int NUM_COLS = 9;

    /**
     * Basic client side validation of HBASE-13262
     */
    @Test
    public void testScannerSeesAllRecords() throws Exception {
        Connection conn = TestSizeFailures.TEST_UTIL.getConnection();
        try (Table table = conn.getTable(TestSizeFailures.TABLENAME)) {
            Scan s = new Scan();
            s.addFamily(TestSizeFailures.FAMILY);
            s.setMaxResultSize((-1));
            s.setBatch((-1));
            s.setCaching(500);
            Map.Entry<Long, Long> entry = sumTable(table.getScanner(s));
            long rowsObserved = entry.getKey();
            long entriesObserved = entry.getValue();
            // Verify that we see 1M rows and 9M cells
            Assert.assertEquals(TestSizeFailures.NUM_ROWS, rowsObserved);
            Assert.assertEquals(((TestSizeFailures.NUM_ROWS) * (TestSizeFailures.NUM_COLS)), entriesObserved);
        }
    }

    /**
     * Basic client side validation of HBASE-13262
     */
    @Test
    public void testSmallScannerSeesAllRecords() throws Exception {
        Connection conn = TestSizeFailures.TEST_UTIL.getConnection();
        try (Table table = conn.getTable(TestSizeFailures.TABLENAME)) {
            Scan s = new Scan();
            s.setSmall(true);
            s.addFamily(TestSizeFailures.FAMILY);
            s.setMaxResultSize((-1));
            s.setBatch((-1));
            s.setCaching(500);
            Map.Entry<Long, Long> entry = sumTable(table.getScanner(s));
            long rowsObserved = entry.getKey();
            long entriesObserved = entry.getValue();
            // Verify that we see 1M rows and 9M cells
            Assert.assertEquals(TestSizeFailures.NUM_ROWS, rowsObserved);
            Assert.assertEquals(((TestSizeFailures.NUM_ROWS) * (TestSizeFailures.NUM_COLS)), entriesObserved);
        }
    }
}

