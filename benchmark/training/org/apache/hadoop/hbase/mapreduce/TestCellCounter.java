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
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LauncherSecurityManager;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import static TableInputFormat.SCAN_COLUMN_FAMILY;


@Category({ MapReduceTests.class, LargeTests.class })
public class TestCellCounter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCellCounter.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final byte[] ROW1 = Bytes.toBytesBinary("\\x01row1");

    private static final byte[] ROW2 = Bytes.toBytesBinary("\\x01row2");

    private static final String FAMILY_A_STRING = "a";

    private static final String FAMILY_B_STRING = "b";

    private static final byte[] FAMILY_A = Bytes.toBytes(TestCellCounter.FAMILY_A_STRING);

    private static final byte[] FAMILY_B = Bytes.toBytes(TestCellCounter.FAMILY_B_STRING);

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static Path FQ_OUTPUT_DIR;

    private static final String OUTPUT_DIR = ((("target" + (File.separator)) + "test-data") + (File.separator)) + "output";

    private static long now = System.currentTimeMillis();

    @Rule
    public TestName name = new TestName();

    /**
     * Test CellCounter all data should print to output
     */
    @Test
    public void testCellCounter() throws Exception {
        final TableName sourceTable = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ TestCellCounter.FAMILY_A, TestCellCounter.FAMILY_B };
        Table t = TestCellCounter.UTIL.createTable(sourceTable, families);
        try {
            Put p = new Put(TestCellCounter.ROW1);
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Data11"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data12"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data13"));
            t.put(p);
            p = new Put(TestCellCounter.ROW2);
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Dat21"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data22"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data23"));
            t.put(p);
            String[] args = new String[]{ sourceTable.getNameAsString(), TestCellCounter.FQ_OUTPUT_DIR.toString(), ";", "^row1" };
            runCount(args);
            FileInputStream inputStream = new FileInputStream((((TestCellCounter.OUTPUT_DIR) + (File.separator)) + "part-r-00000"));
            String data = IOUtils.toString(inputStream);
            inputStream.close();
            Assert.assertTrue(data.contains(("Total Families Across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total Qualifiers across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total ROWS" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("b;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("a;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;a;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;b;q_Versions" + ("\t" + "1"))));
        } finally {
            t.close();
            FileUtil.fullyDelete(new File(TestCellCounter.OUTPUT_DIR));
        }
    }

    /**
     * Test CellCounter all data should print to output
     */
    @Test
    public void testCellCounterPrefix() throws Exception {
        final TableName sourceTable = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ TestCellCounter.FAMILY_A, TestCellCounter.FAMILY_B };
        Table t = TestCellCounter.UTIL.createTable(sourceTable, families);
        try {
            Put p = new Put(TestCellCounter.ROW1);
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Data11"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data12"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data13"));
            t.put(p);
            p = new Put(TestCellCounter.ROW2);
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Dat21"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data22"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data23"));
            t.put(p);
            String[] args = new String[]{ sourceTable.getNameAsString(), TestCellCounter.FQ_OUTPUT_DIR.toString(), ";", "\\x01row1" };
            runCount(args);
            FileInputStream inputStream = new FileInputStream((((TestCellCounter.OUTPUT_DIR) + (File.separator)) + "part-r-00000"));
            String data = IOUtils.toString(inputStream);
            inputStream.close();
            Assert.assertTrue(data.contains(("Total Families Across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total Qualifiers across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total ROWS" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("b;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("a;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;a;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;b;q_Versions" + ("\t" + "1"))));
        } finally {
            t.close();
            FileUtil.fullyDelete(new File(TestCellCounter.OUTPUT_DIR));
        }
    }

    /**
     * Test CellCounter with time range all data should print to output
     */
    @Test
    public void testCellCounterStartTimeRange() throws Exception {
        final TableName sourceTable = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ TestCellCounter.FAMILY_A, TestCellCounter.FAMILY_B };
        Table t = TestCellCounter.UTIL.createTable(sourceTable, families);
        try {
            Put p = new Put(TestCellCounter.ROW1);
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Data11"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data12"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data13"));
            t.put(p);
            p = new Put(TestCellCounter.ROW2);
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Dat21"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data22"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data23"));
            t.put(p);
            String[] args = new String[]{ sourceTable.getNameAsString(), TestCellCounter.FQ_OUTPUT_DIR.toString(), ";", "^row1", "--starttime=" + (TestCellCounter.now), ("--endtime=" + (TestCellCounter.now)) + 2 };
            runCount(args);
            FileInputStream inputStream = new FileInputStream((((TestCellCounter.OUTPUT_DIR) + (File.separator)) + "part-r-00000"));
            String data = IOUtils.toString(inputStream);
            inputStream.close();
            Assert.assertTrue(data.contains(("Total Families Across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total Qualifiers across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total ROWS" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("b;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("a;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;a;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;b;q_Versions" + ("\t" + "1"))));
        } finally {
            t.close();
            FileUtil.fullyDelete(new File(TestCellCounter.OUTPUT_DIR));
        }
    }

    /**
     * Test CellCounter with time range all data should print to output
     */
    @Test
    public void testCellCounteEndTimeRange() throws Exception {
        final TableName sourceTable = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ TestCellCounter.FAMILY_A, TestCellCounter.FAMILY_B };
        Table t = TestCellCounter.UTIL.createTable(sourceTable, families);
        try {
            Put p = new Put(TestCellCounter.ROW1);
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Data11"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data12"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data13"));
            t.put(p);
            p = new Put(TestCellCounter.ROW2);
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Dat21"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data22"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data23"));
            t.put(p);
            String[] args = new String[]{ sourceTable.getNameAsString(), TestCellCounter.FQ_OUTPUT_DIR.toString(), ";", "^row1", ("--endtime=" + (TestCellCounter.now)) + 1 };
            runCount(args);
            FileInputStream inputStream = new FileInputStream((((TestCellCounter.OUTPUT_DIR) + (File.separator)) + "part-r-00000"));
            String data = IOUtils.toString(inputStream);
            inputStream.close();
            Assert.assertTrue(data.contains(("Total Families Across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total Qualifiers across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total ROWS" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("b;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("a;q" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;a;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;b;q_Versions" + ("\t" + "1"))));
        } finally {
            t.close();
            FileUtil.fullyDelete(new File(TestCellCounter.OUTPUT_DIR));
        }
    }

    /**
     * Test CellCounter with time range all data should print to output
     */
    @Test
    public void testCellCounteOutOfTimeRange() throws Exception {
        final TableName sourceTable = TableName.valueOf(name.getMethodName());
        byte[][] families = new byte[][]{ TestCellCounter.FAMILY_A, TestCellCounter.FAMILY_B };
        Table t = TestCellCounter.UTIL.createTable(sourceTable, families);
        try {
            Put p = new Put(TestCellCounter.ROW1);
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Data11"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data12"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data13"));
            t.put(p);
            p = new Put(TestCellCounter.ROW2);
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Dat21"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data22"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data23"));
            t.put(p);
            String[] args = new String[]{ sourceTable.getNameAsString(), TestCellCounter.FQ_OUTPUT_DIR.toString(), ";", ("--starttime=" + (TestCellCounter.now)) + 1, ("--endtime=" + (TestCellCounter.now)) + 2 };
            runCount(args);
            FileInputStream inputStream = new FileInputStream((((TestCellCounter.OUTPUT_DIR) + (File.separator)) + "part-r-00000"));
            String data = IOUtils.toString(inputStream);
            inputStream.close();
            // nothing should hace been emitted to the reducer
            Assert.assertTrue(data.isEmpty());
        } finally {
            t.close();
            FileUtil.fullyDelete(new File(TestCellCounter.OUTPUT_DIR));
        }
    }

    /**
     * Test main method of CellCounter
     */
    @Test
    public void testCellCounterMain() throws Exception {
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
                CellCounter.main(args);
                Assert.fail("should be SecurityException");
            } catch (SecurityException e) {
                Assert.assertEquals((-1), newSecurityManager.getExitCode());
                Assert.assertTrue(data.toString().contains("ERROR: Wrong number of parameters:"));
                // should be information about usage
                Assert.assertTrue(data.toString().contains("Usage:"));
            }
        } finally {
            System.setErr(oldPrintStream);
            System.setSecurityManager(SECURITY_MANAGER);
        }
    }

    /**
     * Test CellCounter for complete table all data should print to output
     */
    @Test
    public void testCellCounterForCompleteTable() throws Exception {
        final TableName sourceTable = TableName.valueOf(name.getMethodName());
        String outputPath = (TestCellCounter.OUTPUT_DIR) + sourceTable;
        LocalFileSystem localFileSystem = new LocalFileSystem();
        Path outputDir = new Path(outputPath).makeQualified(localFileSystem.getUri(), localFileSystem.getWorkingDirectory());
        byte[][] families = new byte[][]{ TestCellCounter.FAMILY_A, TestCellCounter.FAMILY_B };
        Table t = TestCellCounter.UTIL.createTable(sourceTable, families);
        try {
            Put p = new Put(TestCellCounter.ROW1);
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Data11"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data12"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data13"));
            t.put(p);
            p = new Put(TestCellCounter.ROW2);
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, TestCellCounter.now, Bytes.toBytes("Dat21"));
            p.addColumn(TestCellCounter.FAMILY_A, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 1), Bytes.toBytes("Data22"));
            p.addColumn(TestCellCounter.FAMILY_B, TestCellCounter.QUALIFIER, ((TestCellCounter.now) + 2), Bytes.toBytes("Data23"));
            t.put(p);
            String[] args = new String[]{ sourceTable.getNameAsString(), outputDir.toString(), ";" };
            runCount(args);
            FileInputStream inputStream = new FileInputStream(((outputPath + (File.separator)) + "part-r-00000"));
            String data = IOUtils.toString(inputStream);
            inputStream.close();
            Assert.assertTrue(data.contains(("Total Families Across all Rows" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("Total Qualifiers across all Rows" + ("\t" + "4"))));
            Assert.assertTrue(data.contains(("Total ROWS" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("b;q" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("a;q" + ("\t" + "2"))));
            Assert.assertTrue(data.contains(("row1;a;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row1;b;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row2;a;q_Versions" + ("\t" + "1"))));
            Assert.assertTrue(data.contains(("row2;b;q_Versions" + ("\t" + "1"))));
            FileUtil.fullyDelete(new File(outputPath));
            args = new String[]{ ("-D " + (SCAN_COLUMN_FAMILY)) + "=a, b", sourceTable.getNameAsString(), outputDir.toString(), ";" };
            runCount(args);
            inputStream = new FileInputStream(((outputPath + (File.separator)) + "part-r-00000"));
            String data2 = IOUtils.toString(inputStream);
            inputStream.close();
            Assert.assertEquals(data, data2);
        } finally {
            t.close();
            localFileSystem.close();
            FileUtil.fullyDelete(new File(outputPath));
        }
    }

    @Test
    public void TestCellCounterWithoutOutputDir() throws Exception {
        String[] args = new String[]{ "tableName" };
        Assert.assertEquals("CellCounter should exit with -1 as output directory is not specified.", (-1), ToolRunner.run(HBaseConfiguration.create(), new CellCounter(), args));
    }
}

