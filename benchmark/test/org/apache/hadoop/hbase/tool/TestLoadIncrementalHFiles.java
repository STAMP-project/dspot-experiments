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
package org.apache.hadoop.hbase.tool;


import BloomType.NONE;
import BloomType.ROW;
import BloomType.ROWCOL;
import DataBlockEncoding.DIFF;
import LoadIncrementalHFiles.CREATE_TABLE_CONF_KEY;
import java.io.IOException;
import java.util.Locale;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.HFileTestUtil;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Test cases for the "load" half of the HFileOutputFormat bulk load functionality. These tests run
 * faster than the full MR cluster tests in TestHFileOutputFormat
 */
@Category({ MiscTests.class, LargeTests.class })
public class TestLoadIncrementalHFiles {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLoadIncrementalHFiles.class);

    @Rule
    public TestName tn = new TestName();

    private static final byte[] QUALIFIER = Bytes.toBytes("myqual");

    private static final byte[] FAMILY = Bytes.toBytes("myfam");

    private static final String NAMESPACE = "bulkNS";

    static final String EXPECTED_MSG_FOR_NON_EXISTING_FAMILY = "Unmatched family names found";

    static final int MAX_FILES_PER_REGION_PER_FAMILY = 4;

    private static final byte[][] SPLIT_KEYS = new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("ppp") };

    static HBaseTestingUtility util = new HBaseTestingUtility();

    @Test
    public void testSimpleLoadWithMap() throws Exception {
        runTest("testSimpleLoadWithMap", NONE, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("cccc") }, new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("ooo") } }, true);
    }

    /**
     * Test case that creates some regions and loads HFiles that fit snugly inside those regions
     */
    @Test
    public void testSimpleLoad() throws Exception {
        runTest("testSimpleLoad", NONE, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("cccc") }, new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("ooo") } });
    }

    @Test
    public void testSimpleLoadWithFileCopy() throws Exception {
        String testName = tn.getMethodName();
        final byte[] TABLE_NAME = Bytes.toBytes(("mytable_" + testName));
        runTest(testName, buildHTD(TableName.valueOf(TABLE_NAME), NONE), false, null, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("cccc") }, new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("ooo") } }, false, true, 2);
    }

    /**
     * Test case that creates some regions and loads HFiles that cross the boundaries of those regions
     */
    @Test
    public void testRegionCrossingLoad() throws Exception {
        runTest("testRegionCrossingLoad", NONE, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("eee") }, new byte[][]{ Bytes.toBytes("fff"), Bytes.toBytes("zzz") } });
    }

    /**
     * Test loading into a column family that has a ROW bloom filter.
     */
    @Test
    public void testRegionCrossingRowBloom() throws Exception {
        runTest("testRegionCrossingLoadRowBloom", ROW, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("eee") }, new byte[][]{ Bytes.toBytes("fff"), Bytes.toBytes("zzz") } });
    }

    /**
     * Test loading into a column family that has a ROWCOL bloom filter.
     */
    @Test
    public void testRegionCrossingRowColBloom() throws Exception {
        runTest("testRegionCrossingLoadRowColBloom", ROWCOL, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("eee") }, new byte[][]{ Bytes.toBytes("fff"), Bytes.toBytes("zzz") } });
    }

    /**
     * Test case that creates some regions and loads HFiles that have different region boundaries than
     * the table pre-split.
     */
    @Test
    public void testSimpleHFileSplit() throws Exception {
        runTest("testHFileSplit", NONE, new byte[][]{ Bytes.toBytes("aaa"), Bytes.toBytes("fff"), Bytes.toBytes("jjj"), Bytes.toBytes("ppp"), Bytes.toBytes("uuu"), Bytes.toBytes("zzz") }, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("lll") }, new byte[][]{ Bytes.toBytes("mmm"), Bytes.toBytes("zzz") } });
    }

    /**
     * Test case that creates some regions and loads HFiles that cross the boundaries and have
     * different region boundaries than the table pre-split.
     */
    @Test
    public void testRegionCrossingHFileSplit() throws Exception {
        testRegionCrossingHFileSplit(NONE);
    }

    /**
     * Test case that creates some regions and loads HFiles that cross the boundaries have a ROW bloom
     * filter and a different region boundaries than the table pre-split.
     */
    @Test
    public void testRegionCrossingHFileSplitRowBloom() throws Exception {
        testRegionCrossingHFileSplit(ROW);
    }

    /**
     * Test case that creates some regions and loads HFiles that cross the boundaries have a ROWCOL
     * bloom filter and a different region boundaries than the table pre-split.
     */
    @Test
    public void testRegionCrossingHFileSplitRowColBloom() throws Exception {
        testRegionCrossingHFileSplit(ROWCOL);
    }

    @Test
    public void testSplitALot() throws Exception {
        runTest("testSplitALot", NONE, new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("bbb"), Bytes.toBytes("ccc"), Bytes.toBytes("ddd"), Bytes.toBytes("eee"), Bytes.toBytes("fff"), Bytes.toBytes("ggg"), Bytes.toBytes("hhh"), Bytes.toBytes("iii"), Bytes.toBytes("lll"), Bytes.toBytes("mmm"), Bytes.toBytes("nnn"), Bytes.toBytes("ooo"), Bytes.toBytes("ppp"), Bytes.toBytes("qqq"), Bytes.toBytes("rrr"), Bytes.toBytes("sss"), Bytes.toBytes("ttt"), Bytes.toBytes("uuu"), Bytes.toBytes("vvv"), Bytes.toBytes("zzz") }, new byte[][][]{ new byte[][]{ Bytes.toBytes("aaaa"), Bytes.toBytes("zzz") } });
    }

    /**
     * Test that tags survive through a bulk load that needs to split hfiles. This test depends on the
     * "hbase.client.rpc.codec" = KeyValueCodecWithTags so that the client can get tags in the
     * responses.
     */
    @Test
    public void testTagsSurviveBulkLoadSplit() throws Exception {
        Path dir = TestLoadIncrementalHFiles.util.getDataTestDirOnTestFS(tn.getMethodName());
        FileSystem fs = TestLoadIncrementalHFiles.util.getTestFileSystem();
        dir = dir.makeQualified(fs.getUri(), fs.getWorkingDirectory());
        Path familyDir = new Path(dir, Bytes.toString(TestLoadIncrementalHFiles.FAMILY));
        // table has these split points
        byte[][] tableSplitKeys = new byte[][]{ Bytes.toBytes("aaa"), Bytes.toBytes("fff"), Bytes.toBytes("jjj"), Bytes.toBytes("ppp"), Bytes.toBytes("uuu"), Bytes.toBytes("zzz") };
        // creating an hfile that has values that span the split points.
        byte[] from = Bytes.toBytes("ddd");
        byte[] to = Bytes.toBytes("ooo");
        HFileTestUtil.createHFileWithTags(TestLoadIncrementalHFiles.util.getConfiguration(), fs, new Path(familyDir, ((tn.getMethodName()) + "_hfile")), TestLoadIncrementalHFiles.FAMILY, TestLoadIncrementalHFiles.QUALIFIER, from, to, 1000);
        int expectedRows = 1000;
        TableName tableName = TableName.valueOf(tn.getMethodName());
        TableDescriptor htd = buildHTD(tableName, NONE);
        TestLoadIncrementalHFiles.util.getAdmin().createTable(htd, tableSplitKeys);
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(TestLoadIncrementalHFiles.util.getConfiguration());
        String[] args = new String[]{ dir.toString(), tableName.toString() };
        loader.run(args);
        Table table = TestLoadIncrementalHFiles.util.getConnection().getTable(tableName);
        try {
            Assert.assertEquals(expectedRows, TestLoadIncrementalHFiles.util.countRows(table));
            HFileTestUtil.verifyTags(table);
        } finally {
            table.close();
        }
        TestLoadIncrementalHFiles.util.deleteTable(tableName);
    }

    /**
     * Test loading into a column family that does not exist.
     */
    @Test
    public void testNonexistentColumnFamilyLoad() throws Exception {
        String testName = tn.getMethodName();
        byte[][][] hFileRanges = new byte[][][]{ new byte[][]{ Bytes.toBytes("aaa"), Bytes.toBytes("ccc") }, new byte[][]{ Bytes.toBytes("ddd"), Bytes.toBytes("ooo") } };
        byte[] TABLE = Bytes.toBytes(("mytable_" + testName));
        // set real family name to upper case in purpose to simulate the case that
        // family name in HFiles is invalid
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(TABLE)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(Bytes.toBytes(new String(TestLoadIncrementalHFiles.FAMILY).toUpperCase(Locale.ROOT)))).build();
        try {
            runTest(testName, htd, true, TestLoadIncrementalHFiles.SPLIT_KEYS, hFileRanges, false, false, 2);
            Assert.assertTrue("Loading into table with non-existent family should have failed", false);
        } catch (Exception e) {
            Assert.assertTrue("IOException expected", (e instanceof IOException));
            // further check whether the exception message is correct
            String errMsg = e.getMessage();
            Assert.assertTrue((((("Incorrect exception message, expected message: [" + (TestLoadIncrementalHFiles.EXPECTED_MSG_FOR_NON_EXISTING_FAMILY)) + "], current message: [") + errMsg) + "]"), errMsg.contains(TestLoadIncrementalHFiles.EXPECTED_MSG_FOR_NON_EXISTING_FAMILY));
        }
    }

    @Test
    public void testNonHfileFolderWithUnmatchedFamilyName() throws Exception {
        testNonHfileFolder("testNonHfileFolderWithUnmatchedFamilyName", true);
    }

    @Test
    public void testNonHfileFolder() throws Exception {
        testNonHfileFolder("testNonHfileFolder", false);
    }

    @Test
    public void testSplitStoreFile() throws IOException {
        Path dir = TestLoadIncrementalHFiles.util.getDataTestDirOnTestFS("testSplitHFile");
        FileSystem fs = TestLoadIncrementalHFiles.util.getTestFileSystem();
        Path testIn = new Path(dir, "testhfile");
        ColumnFamilyDescriptor familyDesc = ColumnFamilyDescriptorBuilder.of(TestLoadIncrementalHFiles.FAMILY);
        HFileTestUtil.createHFile(TestLoadIncrementalHFiles.util.getConfiguration(), fs, testIn, TestLoadIncrementalHFiles.FAMILY, TestLoadIncrementalHFiles.QUALIFIER, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), 1000);
        Path bottomOut = new Path(dir, "bottom.out");
        Path topOut = new Path(dir, "top.out");
        LoadIncrementalHFiles.splitStoreFile(TestLoadIncrementalHFiles.util.getConfiguration(), testIn, familyDesc, Bytes.toBytes("ggg"), bottomOut, topOut);
        int rowCount = verifyHFile(bottomOut);
        rowCount += verifyHFile(topOut);
        Assert.assertEquals(1000, rowCount);
    }

    @Test
    public void testSplitStoreFileWithNoneToNone() throws IOException {
        testSplitStoreFileWithDifferentEncoding(DataBlockEncoding.NONE, DataBlockEncoding.NONE);
    }

    @Test
    public void testSplitStoreFileWithEncodedToEncoded() throws IOException {
        testSplitStoreFileWithDifferentEncoding(DIFF, DIFF);
    }

    @Test
    public void testSplitStoreFileWithEncodedToNone() throws IOException {
        testSplitStoreFileWithDifferentEncoding(DIFF, DataBlockEncoding.NONE);
    }

    @Test
    public void testSplitStoreFileWithNoneToEncoded() throws IOException {
        testSplitStoreFileWithDifferentEncoding(DataBlockEncoding.NONE, DIFF);
    }

    @Test
    public void testInferBoundaries() {
        TreeMap<byte[], Integer> map = new TreeMap(Bytes.BYTES_COMPARATOR);
        /* Toy example c---------i o------p s---------t v------x a------e g-----k m-------------q r----s
        u----w Should be inferred as: a-----------------k m-------------q r--------------t
        u---------x The output should be (m,r,u)
         */
        String first;
        String last;
        first = "a";
        last = "e";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "r";
        last = "s";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "o";
        last = "p";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "g";
        last = "k";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "v";
        last = "x";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "c";
        last = "i";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "m";
        last = "q";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "s";
        last = "t";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        first = "u";
        last = "w";
        addStartEndKeysForTest(map, Bytes.toBytes(first), Bytes.toBytes(last));
        byte[][] keysArray = LoadIncrementalHFiles.inferBoundaries(map);
        byte[][] compare = new byte[3][];
        compare[0] = Bytes.toBytes("m");
        compare[1] = Bytes.toBytes("r");
        compare[2] = Bytes.toBytes("u");
        Assert.assertEquals(3, keysArray.length);
        for (int row = 0; row < (keysArray.length); row++) {
            Assert.assertArrayEquals(keysArray[row], compare[row]);
        }
    }

    @Test
    public void testLoadTooMayHFiles() throws Exception {
        Path dir = TestLoadIncrementalHFiles.util.getDataTestDirOnTestFS("testLoadTooMayHFiles");
        FileSystem fs = TestLoadIncrementalHFiles.util.getTestFileSystem();
        dir = dir.makeQualified(fs.getUri(), fs.getWorkingDirectory());
        Path familyDir = new Path(dir, Bytes.toString(TestLoadIncrementalHFiles.FAMILY));
        byte[] from = Bytes.toBytes("begin");
        byte[] to = Bytes.toBytes("end");
        for (int i = 0; i <= (TestLoadIncrementalHFiles.MAX_FILES_PER_REGION_PER_FAMILY); i++) {
            HFileTestUtil.createHFile(TestLoadIncrementalHFiles.util.getConfiguration(), fs, new Path(familyDir, ("hfile_" + i)), TestLoadIncrementalHFiles.FAMILY, TestLoadIncrementalHFiles.QUALIFIER, from, to, 1000);
        }
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(TestLoadIncrementalHFiles.util.getConfiguration());
        String[] args = new String[]{ dir.toString(), "mytable_testLoadTooMayHFiles" };
        try {
            loader.run(args);
            Assert.fail("Bulk loading too many files should fail");
        } catch (IOException ie) {
            Assert.assertTrue(ie.getMessage().contains((("Trying to load more than " + (TestLoadIncrementalHFiles.MAX_FILES_PER_REGION_PER_FAMILY)) + " hfiles")));
        }
    }

    @Test(expected = TableNotFoundException.class)
    public void testWithoutAnExistingTableAndCreateTableSetToNo() throws Exception {
        Configuration conf = TestLoadIncrementalHFiles.util.getConfiguration();
        conf.set(CREATE_TABLE_CONF_KEY, "no");
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(conf);
        String[] args = new String[]{ "directory", "nonExistingTable" };
        loader.run(args);
    }

    @Test
    public void testTableWithCFNameStartWithUnderScore() throws Exception {
        Path dir = TestLoadIncrementalHFiles.util.getDataTestDirOnTestFS("cfNameStartWithUnderScore");
        FileSystem fs = TestLoadIncrementalHFiles.util.getTestFileSystem();
        dir = dir.makeQualified(fs.getUri(), fs.getWorkingDirectory());
        String family = "_cf";
        Path familyDir = new Path(dir, family);
        byte[] from = Bytes.toBytes("begin");
        byte[] to = Bytes.toBytes("end");
        Configuration conf = TestLoadIncrementalHFiles.util.getConfiguration();
        String tableName = tn.getMethodName();
        Table table = TestLoadIncrementalHFiles.util.createTable(TableName.valueOf(tableName), family);
        HFileTestUtil.createHFile(conf, fs, new Path(familyDir, "hfile"), Bytes.toBytes(family), TestLoadIncrementalHFiles.QUALIFIER, from, to, 1000);
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(conf);
        String[] args = new String[]{ dir.toString(), tableName };
        try {
            loader.run(args);
            Assert.assertEquals(1000, TestLoadIncrementalHFiles.util.countRows(table));
        } finally {
            if (null != table) {
                table.close();
            }
        }
    }
}

