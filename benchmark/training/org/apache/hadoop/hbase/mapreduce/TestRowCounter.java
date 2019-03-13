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
package org.apache.hadoop.hbase.mapreduce;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LauncherSecurityManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the rowcounter map reduce job.
 */
@Category({ MapReduceTests.class, LargeTests.class })
public class TestRowCounter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRowCounter.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRowCounter.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final String TABLE_NAME = "testRowCounter";

    private static final String TABLE_NAME_TS_RANGE = "testRowCounter_ts_range";

    private static final String COL_FAM = "col_fam";

    private static final String COL1 = "c1";

    private static final String COL2 = "c2";

    private static final String COMPOSITE_COLUMN = "C:A:A";

    private static final int TOTAL_ROWS = 10;

    private static final int ROWS_WITH_ONE_COL = 2;

    /**
     * Test a case when no column was specified in command line arguments.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterNoColumn() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME };
        runRowCount(args, 10);
    }

    /**
     * Test a case when the column specified in command line arguments is
     * exclusive for few rows.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterExclusiveColumn() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL1) };
        runRowCount(args, 8);
    }

    /**
     * Test a case when the column specified in command line arguments is
     * one for which the qualifier contains colons.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterColumnWithColonInQualifier() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COMPOSITE_COLUMN) };
        runRowCount(args, 8);
    }

    /**
     * Test a case when the column specified in command line arguments is not part
     * of first KV for a row.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterHiddenColumn() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL2) };
        runRowCount(args, 10);
    }

    /**
     * Test a case when the column specified in command line arguments is
     * exclusive for few rows and also a row range filter is specified
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterColumnAndRowRange() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, "--range=\\x00rov,\\x00rox", ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL1) };
        runRowCount(args, 8);
    }

    /**
     * Test a case when a range is specified with single range of start-end keys
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterRowSingleRange() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, "--range=\\x00row1,\\x00row3" };
        runRowCount(args, 2);
    }

    /**
     * Test a case when a range is specified with single range with end key only
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterRowSingleRangeUpperBound() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, "--range=,\\x00row3" };
        runRowCount(args, 3);
    }

    /**
     * Test a case when a range is specified with two ranges where one range is with end key only
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterRowMultiRangeUpperBound() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, "--range=,\\x00row3;\\x00row5,\\x00row7" };
        runRowCount(args, 5);
    }

    /**
     * Test a case when a range is specified with multiple ranges of start-end keys
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterRowMultiRange() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, "--range=\\x00row1,\\x00row3;\\x00row5,\\x00row8" };
        runRowCount(args, 5);
    }

    /**
     * Test a case when a range is specified with multiple ranges of start-end keys;
     * one range is filled, another two are not
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterRowMultiEmptyRange() throws Exception {
        String[] args = new String[]{ TestRowCounter.TABLE_NAME, "--range=\\x00row1,\\x00row3;;" };
        runRowCount(args, 2);
    }

    @Test
    public void testRowCounter10kRowRange() throws Exception {
        String tableName = (TestRowCounter.TABLE_NAME) + "10k";
        try (Table table = TestRowCounter.TEST_UTIL.createTable(TableName.valueOf(tableName), Bytes.toBytes(TestRowCounter.COL_FAM))) {
            TestRowCounter.writeRows(table, 10000, 0);
        }
        String[] args = new String[]{ tableName, "--range=\\x00row9872,\\x00row9875" };
        runRowCount(args, 3);
    }

    /**
     * Test a case when the timerange is specified with --starttime and --endtime options
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRowCounterTimeRange() throws Exception {
        final byte[] family = Bytes.toBytes(TestRowCounter.COL_FAM);
        final byte[] col1 = Bytes.toBytes(TestRowCounter.COL1);
        Put put1 = new Put(Bytes.toBytes(("row_timerange_" + 1)));
        Put put2 = new Put(Bytes.toBytes(("row_timerange_" + 2)));
        Put put3 = new Put(Bytes.toBytes(("row_timerange_" + 3)));
        long ts;
        // clean up content of TABLE_NAME
        Table table = TestRowCounter.TEST_UTIL.createTable(TableName.valueOf(TestRowCounter.TABLE_NAME_TS_RANGE), Bytes.toBytes(TestRowCounter.COL_FAM));
        ts = System.currentTimeMillis();
        put1.addColumn(family, col1, ts, Bytes.toBytes("val1"));
        table.put(put1);
        Thread.sleep(100);
        ts = System.currentTimeMillis();
        put2.addColumn(family, col1, ts, Bytes.toBytes("val2"));
        put3.addColumn(family, col1, ts, Bytes.toBytes("val3"));
        table.put(put2);
        table.put(put3);
        table.close();
        String[] args = new String[]{ TestRowCounter.TABLE_NAME_TS_RANGE, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL1), "--starttime=" + 0, "--endtime=" + ts };
        runRowCount(args, 1);
        args = new String[]{ TestRowCounter.TABLE_NAME_TS_RANGE, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL1), "--starttime=" + 0, "--endtime=" + (ts - 10) };
        runRowCount(args, 1);
        args = new String[]{ TestRowCounter.TABLE_NAME_TS_RANGE, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL1), "--starttime=" + ts, "--endtime=" + (ts + 1000) };
        runRowCount(args, 2);
        args = new String[]{ TestRowCounter.TABLE_NAME_TS_RANGE, ((TestRowCounter.COL_FAM) + ":") + (TestRowCounter.COL1), "--starttime=" + (ts - (30 * 1000)), "--endtime=" + (ts + (30 * 1000)) };
        runRowCount(args, 3);
    }

    /**
     * test main method. Import should print help and call System.exit
     */
    @Test
    public void testImportMain() throws Exception {
        PrintStream oldPrintStream = System.err;
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        String[] args = new String[]{  };
        System.setErr(new PrintStream(data));
        try {
            System.setErr(new PrintStream(data));
            try {
                RowCounter.main(args);
                Assert.fail("should be SecurityException");
            } catch (SecurityException e) {
                Assert.assertEquals((-1), newSecurityManager.getExitCode());
                Assert.assertTrue(data.toString().contains("Wrong number of parameters:"));
                assertUsageContent(data.toString());
            }
            data.reset();
            try {
                args = new String[2];
                args[0] = "table";
                args[1] = "--range=1";
                RowCounter.main(args);
                Assert.fail("should be SecurityException");
            } catch (SecurityException e) {
                Assert.assertEquals((-1), newSecurityManager.getExitCode());
                Assert.assertTrue(data.toString().contains(("Please specify range in such format as \"--range=a,b\" or, with only one boundary," + " \"--range=,b\" or \"--range=a,\"")));
                assertUsageContent(data.toString());
            }
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }
}

