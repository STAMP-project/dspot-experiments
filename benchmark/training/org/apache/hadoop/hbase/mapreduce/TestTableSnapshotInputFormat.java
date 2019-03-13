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


import HBaseTestingUtility.SeenRowTracker;
import HConstants.EMPTY_START_ROW;
import HConstants.HFILE_BLOCK_CACHE_SIZE_DEFAULT;
import HConstants.HFILE_BLOCK_CACHE_SIZE_KEY;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HDFSBlocksDistribution;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TestTableSnapshotScanner;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.RegionSplitter;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ VerySlowMapReduceTests.class, LargeTests.class })
public class TestTableSnapshotInputFormat extends TableSnapshotInputFormatTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableSnapshotInputFormat.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableSnapshotInputFormat.class);

    private static final byte[] bbb = Bytes.toBytes("bbb");

    private static final byte[] yyy = Bytes.toBytes("yyy");

    private static final byte[] bbc = Bytes.toBytes("bbc");

    private static final byte[] yya = Bytes.toBytes("yya");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testGetBestLocations() throws IOException {
        TableSnapshotInputFormatImpl tsif = new TableSnapshotInputFormatImpl();
        Configuration conf = UTIL.getConfiguration();
        HDFSBlocksDistribution blockDistribution = new HDFSBlocksDistribution();
        Assert.assertEquals(null, TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h1" }, 1);
        Assert.assertEquals(Lists.newArrayList("h1"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h1" }, 1);
        Assert.assertEquals(Lists.newArrayList("h1"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h2" }, 1);
        Assert.assertEquals(Lists.newArrayList("h1"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution = new HDFSBlocksDistribution();
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h1" }, 10);
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h2" }, 7);
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h3" }, 5);
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h4" }, 1);
        Assert.assertEquals(Lists.newArrayList("h1"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h2" }, 2);
        Assert.assertEquals(Lists.newArrayList("h1", "h2"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h2" }, 3);
        Assert.assertEquals(Lists.newArrayList("h2", "h1"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h3" }, 6);
        blockDistribution.addHostsAndBlockWeight(new String[]{ "h4" }, 9);
        Assert.assertEquals(Lists.newArrayList("h2", "h3", "h4"), TableSnapshotInputFormatImpl.getBestLocations(conf, blockDistribution));
    }

    public static enum TestTableSnapshotCounters {

        VALIDATION_ERROR;}

    public static class TestTableSnapshotMapper extends TableMapper<ImmutableBytesWritable, NullWritable> {
        @Override
        protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
            // Validate a single row coming from the snapshot, and emit the row key
            TableSnapshotInputFormatTestBase.verifyRowFromMap(key, value);
            context.write(key, NullWritable.get());
        }
    }

    public static class TestTableSnapshotReducer extends Reducer<ImmutableBytesWritable, NullWritable, NullWritable, NullWritable> {
        SeenRowTracker rowTracker = new HBaseTestingUtility.SeenRowTracker(TestTableSnapshotInputFormat.bbb, TestTableSnapshotInputFormat.yyy);

        @Override
        protected void reduce(ImmutableBytesWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
            rowTracker.addRow(key.get());
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            rowTracker.validate();
        }
    }

    @Test
    public void testInitTableSnapshotMapperJobConfig() throws Exception {
        setupCluster();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        String snapshotName = "foo";
        try {
            TableSnapshotInputFormatTestBase.createTableAndSnapshot(UTIL, tableName, snapshotName, getStartRow(), getEndRow(), 1);
            Job job = new Job(UTIL.getConfiguration());
            Path tmpTableDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            TableMapReduceUtil.initTableSnapshotMapperJob(snapshotName, new Scan(), TestTableSnapshotInputFormat.TestTableSnapshotMapper.class, ImmutableBytesWritable.class, NullWritable.class, job, false, tmpTableDir);
            // TODO: would be better to examine directly the cache instance that results from this
            // config. Currently this is not possible because BlockCache initialization is static.
            Assert.assertEquals("Snapshot job should be configured for default LruBlockCache.", HFILE_BLOCK_CACHE_SIZE_DEFAULT, job.getConfiguration().getFloat(HFILE_BLOCK_CACHE_SIZE_KEY, (-1)), 0.01);
            Assert.assertEquals("Snapshot job should not use BucketCache.", 0, job.getConfiguration().getFloat("hbase.bucketcache.size", (-1)), 0.01);
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }

    @Test
    public void testWithMockedMapReduceWithSplitsPerRegion() throws Exception {
        setupCluster();
        String snapshotName = "testWithMockedMapReduceMultiRegion";
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try {
            TableSnapshotInputFormatTestBase.createTableAndSnapshot(UTIL, tableName, snapshotName, getStartRow(), getEndRow(), 10);
            Configuration conf = UTIL.getConfiguration();
            conf.setBoolean(TableSnapshotInputFormatImpl.SNAPSHOT_INPUTFORMAT_LOCALITY_ENABLED_KEY, false);
            Job job = new Job(conf);
            Path tmpTableDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            // test scan with startRow and stopRow
            Scan scan = new Scan(TestTableSnapshotInputFormat.bbc, TestTableSnapshotInputFormat.yya);
            TableMapReduceUtil.initTableSnapshotMapperJob(snapshotName, scan, TestTableSnapshotInputFormat.TestTableSnapshotMapper.class, ImmutableBytesWritable.class, NullWritable.class, job, false, tmpTableDir, new RegionSplitter.UniformSplit(), 5);
            verifyWithMockedMapReduce(job, 10, 40, TestTableSnapshotInputFormat.bbc, TestTableSnapshotInputFormat.yya);
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }

    @Test
    public void testWithMockedMapReduceWithNoStartRowStopRow() throws Exception {
        setupCluster();
        String snapshotName = "testWithMockedMapReduceMultiRegion";
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try {
            TableSnapshotInputFormatTestBase.createTableAndSnapshot(UTIL, tableName, snapshotName, getStartRow(), getEndRow(), 10);
            Configuration conf = UTIL.getConfiguration();
            conf.setBoolean(TableSnapshotInputFormatImpl.SNAPSHOT_INPUTFORMAT_LOCALITY_ENABLED_KEY, false);
            Job job = new Job(conf);
            Path tmpTableDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            // test scan without startRow and stopRow
            Scan scan2 = new Scan();
            TableMapReduceUtil.initTableSnapshotMapperJob(snapshotName, scan2, TestTableSnapshotInputFormat.TestTableSnapshotMapper.class, ImmutableBytesWritable.class, NullWritable.class, job, false, tmpTableDir, new RegionSplitter.UniformSplit(), 5);
            verifyWithMockedMapReduce(job, 10, 50, EMPTY_START_ROW, EMPTY_START_ROW);
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }

    @Test
    public void testNoDuplicateResultsWhenSplitting() throws Exception {
        setupCluster();
        TableName tableName = TableName.valueOf("testNoDuplicateResultsWhenSplitting");
        String snapshotName = "testSnapshotBug";
        try {
            if (UTIL.getAdmin().tableExists(tableName)) {
                UTIL.deleteTable(tableName);
            }
            UTIL.createTable(tableName, TableSnapshotInputFormatTestBase.FAMILIES);
            Admin admin = UTIL.getAdmin();
            // put some stuff in the table
            Table table = UTIL.getConnection().getTable(tableName);
            UTIL.loadTable(table, TableSnapshotInputFormatTestBase.FAMILIES);
            // split to 2 regions
            admin.split(tableName, Bytes.toBytes("eee"));
            TestTableSnapshotScanner.blockUntilSplitFinished(UTIL, tableName, 2);
            Path rootDir = FSUtils.getRootDir(UTIL.getConfiguration());
            FileSystem fs = rootDir.getFileSystem(UTIL.getConfiguration());
            SnapshotTestingUtils.createSnapshotAndValidate(admin, tableName, Arrays.asList(TableSnapshotInputFormatTestBase.FAMILIES), null, snapshotName, rootDir, fs, true);
            // load different values
            byte[] value = Bytes.toBytes("after_snapshot_value");
            UTIL.loadTable(table, TableSnapshotInputFormatTestBase.FAMILIES, value);
            // cause flush to create new files in the region
            admin.flush(tableName);
            table.close();
            Job job = new Job(UTIL.getConfiguration());
            Path tmpTableDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            // limit the scan
            Scan scan = new Scan().withStartRow(getStartRow()).withStopRow(getEndRow());
            TableMapReduceUtil.initTableSnapshotMapperJob(snapshotName, scan, TestTableSnapshotInputFormat.TestTableSnapshotMapper.class, ImmutableBytesWritable.class, NullWritable.class, job, false, tmpTableDir);
            verifyWithMockedMapReduce(job, 2, 2, getStartRow(), getEndRow());
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }

    @Test
    public void testWithMapReduceMultipleMappersPerRegion() throws Exception {
        testWithMapReduce(UTIL, "testWithMapReduceMultiRegion", 10, 5, 50, false);
    }
}

