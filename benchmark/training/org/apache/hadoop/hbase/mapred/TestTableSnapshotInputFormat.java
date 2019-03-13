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
package org.apache.hadoop.hbase.mapred;


import HBaseTestingUtility.SeenRowTracker;
import HConstants.HFILE_BLOCK_CACHE_SIZE_DEFAULT;
import HConstants.HFILE_BLOCK_CACHE_SIZE_KEY;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableSnapshotInputFormatTestBase;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ VerySlowMapReduceTests.class, LargeTests.class })
public class TestTableSnapshotInputFormat extends TableSnapshotInputFormatTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableSnapshotInputFormat.class);

    private static final byte[] aaa = Bytes.toBytes("aaa");

    private static final byte[] after_zzz = Bytes.toBytes("zz{");// 'z' + 1 => '{'


    private static final String COLUMNS = ((Bytes.toString(TableSnapshotInputFormatTestBase.FAMILIES[0])) + " ") + (Bytes.toString(TableSnapshotInputFormatTestBase.FAMILIES[1]));

    @Rule
    public TestName name = new TestName();

    static class TestTableSnapshotMapper extends MapReduceBase implements TableMap<ImmutableBytesWritable, NullWritable> {
        @Override
        public void map(ImmutableBytesWritable key, Result value, OutputCollector<ImmutableBytesWritable, NullWritable> collector, Reporter reporter) throws IOException {
            TableSnapshotInputFormatTestBase.verifyRowFromMap(key, value);
            collector.collect(key, NullWritable.get());
        }
    }

    public static class TestTableSnapshotReducer extends MapReduceBase implements Reducer<ImmutableBytesWritable, NullWritable, NullWritable, NullWritable> {
        SeenRowTracker rowTracker = new HBaseTestingUtility.SeenRowTracker(TestTableSnapshotInputFormat.aaa, TestTableSnapshotInputFormat.after_zzz);

        @Override
        public void reduce(ImmutableBytesWritable key, Iterator<NullWritable> values, OutputCollector<NullWritable, NullWritable> collector, Reporter reporter) throws IOException {
            rowTracker.addRow(key.get());
        }

        @Override
        public void close() {
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
            JobConf job = new JobConf(UTIL.getConfiguration());
            Path tmpTableDir = UTIL.getDataTestDirOnTestFS(snapshotName);
            TableMapReduceUtil.initTableSnapshotMapJob(snapshotName, TestTableSnapshotInputFormat.COLUMNS, TestTableSnapshotInputFormat.TestTableSnapshotMapper.class, ImmutableBytesWritable.class, NullWritable.class, job, false, tmpTableDir);
            // TODO: would be better to examine directly the cache instance that results from this
            // config. Currently this is not possible because BlockCache initialization is static.
            Assert.assertEquals("Snapshot job should be configured for default LruBlockCache.", HFILE_BLOCK_CACHE_SIZE_DEFAULT, job.getFloat(HFILE_BLOCK_CACHE_SIZE_KEY, (-1)), 0.01);
            Assert.assertEquals("Snapshot job should not use BucketCache.", 0, job.getFloat("hbase.bucketcache.size", (-1)), 0.01);
        } finally {
            UTIL.getAdmin().deleteSnapshot(snapshotName);
            UTIL.deleteTable(tableName);
            tearDownCluster();
        }
    }

    // TODO: mapred does not support limiting input range by startrow, endrow.
    // Thus the following tests must override parameterverification.
    @Test
    @Override
    public void testWithMockedMapReduceMultiRegion() throws Exception {
        testWithMockedMapReduce(UTIL, "testWithMockedMapReduceMultiRegion", 10, 1, 10, true);
        // It does not matter whether true or false is given to setLocalityEnabledTo,
        // because it is not read in testWithMockedMapReduce().
    }

    @Test
    @Override
    public void testWithMapReduceMultiRegion() throws Exception {
        testWithMapReduce(UTIL, "testWithMapReduceMultiRegion", 10, 1, 10, false);
    }

    // run the MR job while HBase is offline
    @Test
    @Override
    public void testWithMapReduceAndOfflineHBaseMultiRegion() throws Exception {
        testWithMapReduce(UTIL, "testWithMapReduceAndOfflineHBaseMultiRegion", 10, 1, 10, true);
    }
}

