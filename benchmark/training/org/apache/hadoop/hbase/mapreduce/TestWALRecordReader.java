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


import WALInputFormat.END_TIME_KEY;
import WALInputFormat.START_TIME_KEY;
import WALInputFormat.WALSplit;
import java.util.List;
import java.util.NavigableMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JUnit tests for the WALRecordReader
 */
@Category({ MapReduceTests.class, MediumTests.class })
public class TestWALRecordReader {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALRecordReader.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWALRecordReader.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf;

    private static FileSystem fs;

    private static Path hbaseDir;

    private static FileSystem walFs;

    private static Path walRootDir;

    // visible for TestHLogRecordReader
    static final TableName tableName = TableName.valueOf(TestWALRecordReader.getName());

    private static final byte[] rowName = TestWALRecordReader.tableName.getName();

    // visible for TestHLogRecordReader
    static final RegionInfo info = RegionInfoBuilder.newBuilder(TestWALRecordReader.tableName).build();

    private static final byte[] family = Bytes.toBytes("column");

    private static final byte[] value = Bytes.toBytes("value");

    private static Path logDir;

    protected MultiVersionConcurrencyControl mvcc;

    protected static NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);

    /**
     * Test partial reads from the log based on passed time range
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartialRead() throws Exception {
        final WALFactory walfactory = new WALFactory(TestWALRecordReader.conf, TestWALRecordReader.getName());
        WAL log = walfactory.getWAL(TestWALRecordReader.info);
        // This test depends on timestamp being millisecond based and the filename of the WAL also
        // being millisecond based.
        long ts = System.currentTimeMillis();
        WALEdit edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("1"), ts, TestWALRecordReader.value));
        log.append(TestWALRecordReader.info, getWalKeyImpl(ts, TestWALRecordReader.scopes), edit, true);
        edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("2"), (ts + 1), TestWALRecordReader.value));
        log.append(TestWALRecordReader.info, getWalKeyImpl((ts + 1), TestWALRecordReader.scopes), edit, true);
        log.sync();
        TestWALRecordReader.LOG.info(("Before 1st WAL roll " + (log.toString())));
        log.rollWriter();
        TestWALRecordReader.LOG.info(("Past 1st WAL roll " + (log.toString())));
        Thread.sleep(1);
        long ts1 = System.currentTimeMillis();
        edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("3"), (ts1 + 1), TestWALRecordReader.value));
        log.append(TestWALRecordReader.info, getWalKeyImpl((ts1 + 1), TestWALRecordReader.scopes), edit, true);
        edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("4"), (ts1 + 2), TestWALRecordReader.value));
        log.append(TestWALRecordReader.info, getWalKeyImpl((ts1 + 2), TestWALRecordReader.scopes), edit, true);
        log.sync();
        log.shutdown();
        walfactory.shutdown();
        TestWALRecordReader.LOG.info(("Closed WAL " + (log.toString())));
        WALInputFormat input = new WALInputFormat();
        Configuration jobConf = new Configuration(TestWALRecordReader.conf);
        jobConf.set("mapreduce.input.fileinputformat.inputdir", TestWALRecordReader.logDir.toString());
        jobConf.setLong(END_TIME_KEY, ts);
        // only 1st file is considered, and only its 1st entry is used
        List<InputSplit> splits = input.getSplits(MapreduceTestingShim.createJobContext(jobConf));
        Assert.assertEquals(1, splits.size());
        testSplit(splits.get(0), Bytes.toBytes("1"));
        jobConf.setLong(START_TIME_KEY, (ts + 1));
        jobConf.setLong(END_TIME_KEY, (ts1 + 1));
        splits = input.getSplits(MapreduceTestingShim.createJobContext(jobConf));
        // both files need to be considered
        Assert.assertEquals(2, splits.size());
        // only the 2nd entry from the 1st file is used
        testSplit(splits.get(0), Bytes.toBytes("2"));
        // only the 1nd entry from the 2nd file is used
        testSplit(splits.get(1), Bytes.toBytes("3"));
    }

    /**
     * Test basic functionality
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWALRecordReader() throws Exception {
        final WALFactory walfactory = new WALFactory(TestWALRecordReader.conf, TestWALRecordReader.getName());
        WAL log = walfactory.getWAL(TestWALRecordReader.info);
        byte[] value = Bytes.toBytes("value");
        WALEdit edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("1"), System.currentTimeMillis(), value));
        long txid = log.append(TestWALRecordReader.info, getWalKeyImpl(System.currentTimeMillis(), TestWALRecordReader.scopes), edit, true);
        log.sync(txid);
        Thread.sleep(1);// make sure 2nd log gets a later timestamp

        long secondTs = System.currentTimeMillis();
        log.rollWriter();
        edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("2"), System.currentTimeMillis(), value));
        txid = log.append(TestWALRecordReader.info, getWalKeyImpl(System.currentTimeMillis(), TestWALRecordReader.scopes), edit, true);
        log.sync(txid);
        log.shutdown();
        walfactory.shutdown();
        long thirdTs = System.currentTimeMillis();
        // should have 2 log files now
        WALInputFormat input = new WALInputFormat();
        Configuration jobConf = new Configuration(TestWALRecordReader.conf);
        jobConf.set("mapreduce.input.fileinputformat.inputdir", TestWALRecordReader.logDir.toString());
        // make sure both logs are found
        List<InputSplit> splits = input.getSplits(MapreduceTestingShim.createJobContext(jobConf));
        Assert.assertEquals(2, splits.size());
        // should return exactly one KV
        testSplit(splits.get(0), Bytes.toBytes("1"));
        // same for the 2nd split
        testSplit(splits.get(1), Bytes.toBytes("2"));
        // now test basic time ranges:
        // set an endtime, the 2nd log file can be ignored completely.
        jobConf.setLong(END_TIME_KEY, (secondTs - 1));
        splits = input.getSplits(MapreduceTestingShim.createJobContext(jobConf));
        Assert.assertEquals(1, splits.size());
        testSplit(splits.get(0), Bytes.toBytes("1"));
        // now set a start time
        jobConf.setLong(END_TIME_KEY, Long.MAX_VALUE);
        jobConf.setLong(START_TIME_KEY, thirdTs);
        splits = input.getSplits(MapreduceTestingShim.createJobContext(jobConf));
        // both logs need to be considered
        Assert.assertEquals(2, splits.size());
        // but both readers skip all edits
        testSplit(splits.get(0));
        testSplit(splits.get(1));
    }

    /**
     * Test WALRecordReader tolerance to moving WAL from active
     * to archive directory
     *
     * @throws Exception
     * 		exception
     */
    @Test
    public void testWALRecordReaderActiveArchiveTolerance() throws Exception {
        final WALFactory walfactory = new WALFactory(TestWALRecordReader.conf, TestWALRecordReader.getName());
        WAL log = walfactory.getWAL(TestWALRecordReader.info);
        byte[] value = Bytes.toBytes("value");
        WALEdit edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("1"), System.currentTimeMillis(), value));
        long txid = log.append(TestWALRecordReader.info, getWalKeyImpl(System.currentTimeMillis(), TestWALRecordReader.scopes), edit, true);
        log.sync(txid);
        Thread.sleep(10);// make sure 2nd edit gets a later timestamp

        edit = new WALEdit();
        edit.add(new org.apache.hadoop.hbase.KeyValue(TestWALRecordReader.rowName, TestWALRecordReader.family, Bytes.toBytes("2"), System.currentTimeMillis(), value));
        txid = log.append(TestWALRecordReader.info, getWalKeyImpl(System.currentTimeMillis(), TestWALRecordReader.scopes), edit, true);
        log.sync(txid);
        log.shutdown();
        // should have 2 log entries now
        WALInputFormat input = new WALInputFormat();
        Configuration jobConf = new Configuration(TestWALRecordReader.conf);
        jobConf.set("mapreduce.input.fileinputformat.inputdir", TestWALRecordReader.logDir.toString());
        // make sure log is found
        List<InputSplit> splits = input.getSplits(MapreduceTestingShim.createJobContext(jobConf));
        Assert.assertEquals(1, splits.size());
        WALInputFormat.WALSplit split = ((WALInputFormat.WALSplit) (splits.get(0)));
        TestWALRecordReader.LOG.debug(((("log=" + (TestWALRecordReader.logDir)) + " file=") + (split.getLogFileName())));
        testSplitWithMovingWAL(splits.get(0), Bytes.toBytes("1"), Bytes.toBytes("2"));
    }
}

