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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MapReduceTests.class, LargeTests.class })
public class TestTableMapReduceUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableMapReduceUtil.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableMapReduceUtil.class);

    private static Table presidentsTable;

    private static final String TABLE_NAME = "People";

    private static final byte[] COLUMN_FAMILY = Bytes.toBytes("info");

    private static final byte[] COLUMN_QUALIFIER = Bytes.toBytes("name");

    private static ImmutableSet<String> presidentsRowKeys = ImmutableSet.of("president1", "president2", "president3");

    private static Iterator<String> presidentNames = ImmutableSet.of("John F. Kennedy", "George W. Bush", "Barack Obama").iterator();

    private static ImmutableSet<String> actorsRowKeys = ImmutableSet.of("actor1", "actor2");

    private static Iterator<String> actorNames = ImmutableSet.of("Jack Nicholson", "Martin Freeman").iterator();

    private static String PRESIDENT_PATTERN = "president";

    private static String ACTOR_PATTERN = "actor";

    private static ImmutableMap<String, ImmutableSet<String>> relation = ImmutableMap.of(TestTableMapReduceUtil.PRESIDENT_PATTERN, TestTableMapReduceUtil.presidentsRowKeys, TestTableMapReduceUtil.ACTOR_PATTERN, TestTableMapReduceUtil.actorsRowKeys);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    /**
     * Check what the given number of reduce tasks for the given job configuration
     * does not exceed the number of regions for the given table.
     */
    @Test
    public void shouldNumberOfReduceTaskNotExceedNumberOfRegionsForGivenTable() throws IOException {
        Assert.assertNotNull(TestTableMapReduceUtil.presidentsTable);
        Configuration cfg = TestTableMapReduceUtil.UTIL.getConfiguration();
        JobConf jobConf = new JobConf(cfg);
        TableMapReduceUtil.setNumReduceTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        TableMapReduceUtil.limitNumReduceTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        TableMapReduceUtil.setScannerCaching(jobConf, 100);
        Assert.assertEquals(1, jobConf.getNumReduceTasks());
        Assert.assertEquals(100, jobConf.getInt("hbase.client.scanner.caching", 0));
        jobConf.setNumReduceTasks(10);
        TableMapReduceUtil.setNumMapTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        TableMapReduceUtil.limitNumReduceTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        Assert.assertEquals(1, jobConf.getNumReduceTasks());
    }

    @Test
    public void shouldNumberOfMapTaskNotExceedNumberOfRegionsForGivenTable() throws IOException {
        Configuration cfg = TestTableMapReduceUtil.UTIL.getConfiguration();
        JobConf jobConf = new JobConf(cfg);
        TableMapReduceUtil.setNumReduceTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        TableMapReduceUtil.limitNumMapTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        Assert.assertEquals(1, jobConf.getNumMapTasks());
        jobConf.setNumMapTasks(10);
        TableMapReduceUtil.setNumMapTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        TableMapReduceUtil.limitNumMapTasks(TestTableMapReduceUtil.TABLE_NAME, jobConf);
        Assert.assertEquals(1, jobConf.getNumMapTasks());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shoudBeValidMapReduceEvaluation() throws Exception {
        Configuration cfg = TestTableMapReduceUtil.UTIL.getConfiguration();
        JobConf jobConf = new JobConf(cfg);
        try {
            jobConf.setJobName("process row task");
            jobConf.setNumReduceTasks(1);
            TableMapReduceUtil.initTableMapJob(TestTableMapReduceUtil.TABLE_NAME, new String(TestTableMapReduceUtil.COLUMN_FAMILY), TestTableMapReduceUtil.ClassificatorMapper.class, ImmutableBytesWritable.class, Put.class, jobConf);
            TableMapReduceUtil.initTableReduceJob(TestTableMapReduceUtil.TABLE_NAME, TestTableMapReduceUtil.ClassificatorRowReduce.class, jobConf);
            RunningJob job = JobClient.runJob(jobConf);
            Assert.assertTrue(job.isSuccessful());
        } finally {
            if (jobConf != null)
                FileUtil.fullyDelete(new File(jobConf.get("hadoop.tmp.dir")));

        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shoudBeValidMapReduceWithPartitionerEvaluation() throws IOException {
        Configuration cfg = TestTableMapReduceUtil.UTIL.getConfiguration();
        JobConf jobConf = new JobConf(cfg);
        try {
            jobConf.setJobName("process row task");
            jobConf.setNumReduceTasks(2);
            TableMapReduceUtil.initTableMapJob(TestTableMapReduceUtil.TABLE_NAME, new String(TestTableMapReduceUtil.COLUMN_FAMILY), TestTableMapReduceUtil.ClassificatorMapper.class, ImmutableBytesWritable.class, Put.class, jobConf);
            TableMapReduceUtil.initTableReduceJob(TestTableMapReduceUtil.TABLE_NAME, TestTableMapReduceUtil.ClassificatorRowReduce.class, jobConf, HRegionPartitioner.class);
            RunningJob job = JobClient.runJob(jobConf);
            Assert.assertTrue(job.isSuccessful());
        } finally {
            if (jobConf != null)
                FileUtil.fullyDelete(new File(jobConf.get("hadoop.tmp.dir")));

        }
    }

    @SuppressWarnings("deprecation")
    static class ClassificatorRowReduce extends MapReduceBase implements TableReduce<ImmutableBytesWritable, Put> {
        @Override
        public void reduce(ImmutableBytesWritable key, Iterator<Put> values, OutputCollector<ImmutableBytesWritable, Put> output, Reporter reporter) throws IOException {
            String strKey = Bytes.toString(key.get());
            List<Put> result = new ArrayList<>();
            while (values.hasNext())
                result.add(values.next());

            if (TestTableMapReduceUtil.relation.keySet().contains(strKey)) {
                Set<String> set = TestTableMapReduceUtil.relation.get(strKey);
                if (set != null) {
                    Assert.assertEquals(set.size(), result.size());
                } else {
                    throwAccertionError("Test infrastructure error: set is null");
                }
            } else {
                throwAccertionError("Test infrastructure error: key not found in map");
            }
        }

        private void throwAccertionError(String errorMessage) throws AssertionError {
            throw new AssertionError(errorMessage);
        }
    }

    @SuppressWarnings("deprecation")
    static class ClassificatorMapper extends MapReduceBase implements TableMap<ImmutableBytesWritable, Put> {
        @Override
        public void map(ImmutableBytesWritable row, Result result, OutputCollector<ImmutableBytesWritable, Put> outCollector, Reporter reporter) throws IOException {
            String rowKey = Bytes.toString(result.getRow());
            final ImmutableBytesWritable pKey = new ImmutableBytesWritable(Bytes.toBytes(TestTableMapReduceUtil.PRESIDENT_PATTERN));
            final ImmutableBytesWritable aKey = new ImmutableBytesWritable(Bytes.toBytes(TestTableMapReduceUtil.ACTOR_PATTERN));
            ImmutableBytesWritable outKey = null;
            if (rowKey.startsWith(TestTableMapReduceUtil.PRESIDENT_PATTERN)) {
                outKey = pKey;
            } else
                if (rowKey.startsWith(TestTableMapReduceUtil.ACTOR_PATTERN)) {
                    outKey = aKey;
                } else {
                    throw new AssertionError("unexpected rowKey");
                }

            String name = Bytes.toString(result.getValue(TestTableMapReduceUtil.COLUMN_FAMILY, TestTableMapReduceUtil.COLUMN_QUALIFIER));
            outCollector.collect(outKey, new Put(Bytes.toBytes("rowKey2")).addColumn(TestTableMapReduceUtil.COLUMN_FAMILY, TestTableMapReduceUtil.COLUMN_QUALIFIER, Bytes.toBytes(name)));
        }
    }
}

