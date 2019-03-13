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


import HFile.MIN_FORMAT_VERSION_WITH_TAGS;
import HFileOutputFormat2.LOCALITY_SENSITIVE_CONF_KEY;
import HFileOutputFormat2.MULTI_TABLE_HFILEOUTPUTFORMAT_CONF_KEY;
import HFileOutputFormat2.OUTPUT_TABLE_NAME_CONF_KEY;
import HFileOutputFormat2.STORAGE_POLICY_PROPERTY;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Tag;
import org.apache.hadoop.hbase.TagType;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFile.Reader;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.apache.hadoop.hbase.regionserver.TestHRegionFileSystem;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.VerySlowMapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.partition.TotalOrderPartitioner;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HFileOutputFormat2.STORAGE_POLICY_PROPERTY_CF_PREFIX;
import static java.util.Arrays.stream;


/**
 * Simple test for {@link HFileOutputFormat2}.
 * Sets up and runs a mapreduce job that writes hfile output.
 * Creates a few inner classes to implement splits and an inputformat that
 * emits keys and values like those of {@link PerformanceEvaluation}.
 */
@Category({ VerySlowMapReduceTests.class, LargeTests.class })
public class TestHFileOutputFormat2 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHFileOutputFormat2.class);

    private static final int ROWSPERSPLIT = 1024;

    public static final byte[] FAMILY_NAME = TestHRegionFileSystem.FAMILY_NAME;

    private static final byte[][] FAMILIES = new byte[][]{ Bytes.add(TestHFileOutputFormat2.FAMILY_NAME, Bytes.toBytes("-A")), Bytes.add(TestHFileOutputFormat2.FAMILY_NAME, Bytes.toBytes("-B")) };

    private static final TableName[] TABLE_NAMES = Stream.of("TestTable", "TestTable2", "TestTable3").map(TableName::valueOf).toArray(TableName[]::new);

    private HBaseTestingUtility util = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestHFileOutputFormat2.class);

    /**
     * Simple mapper that makes KeyValue output.
     */
    static class RandomKVGeneratingMapper extends Mapper<NullWritable, NullWritable, ImmutableBytesWritable, Cell> {
        private int keyLength;

        private static final int KEYLEN_DEFAULT = 10;

        private static final String KEYLEN_CONF = "randomkv.key.length";

        private int valLength;

        private static final int VALLEN_DEFAULT = 10;

        private static final String VALLEN_CONF = "randomkv.val.length";

        private static final byte[] QUALIFIER = Bytes.toBytes("data");

        private boolean multiTableMapper = false;

        private TableName[] tables = null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            Configuration conf = context.getConfiguration();
            keyLength = conf.getInt(TestHFileOutputFormat2.RandomKVGeneratingMapper.KEYLEN_CONF, TestHFileOutputFormat2.RandomKVGeneratingMapper.KEYLEN_DEFAULT);
            valLength = conf.getInt(TestHFileOutputFormat2.RandomKVGeneratingMapper.VALLEN_CONF, TestHFileOutputFormat2.RandomKVGeneratingMapper.VALLEN_DEFAULT);
            multiTableMapper = conf.getBoolean(MULTI_TABLE_HFILEOUTPUTFORMAT_CONF_KEY, false);
            if (multiTableMapper) {
                tables = TestHFileOutputFormat2.TABLE_NAMES;
            } else {
                tables = new TableName[]{ TestHFileOutputFormat2.TABLE_NAMES[0] };
            }
        }

        @Override
        protected void map(NullWritable n1, NullWritable n2, Mapper.Context context) throws IOException, InterruptedException {
            byte[] keyBytes = new byte[keyLength];
            byte[] valBytes = new byte[valLength];
            int taskId = context.getTaskAttemptID().getTaskID().getId();
            assert taskId < (Byte.MAX_VALUE) : "Unit tests dont support > 127 tasks!";
            Random random = new Random();
            byte[] key;
            for (int j = 0; j < (tables.length); ++j) {
                for (int i = 0; i < (TestHFileOutputFormat2.ROWSPERSPLIT); i++) {
                    random.nextBytes(keyBytes);
                    // Ensure that unique tasks generate unique keys
                    keyBytes[((keyLength) - 1)] = ((byte) (taskId & 255));
                    random.nextBytes(valBytes);
                    key = keyBytes;
                    if (multiTableMapper) {
                        key = MultiTableHFileOutputFormat.createCompositeKey(tables[j].getName(), keyBytes);
                    }
                    for (byte[] family : TestHFileOutputFormat2.FAMILIES) {
                        Cell kv = new KeyValue(keyBytes, family, TestHFileOutputFormat2.RandomKVGeneratingMapper.QUALIFIER, valBytes);
                        context.write(new ImmutableBytesWritable(key), kv);
                    }
                }
            }
        }
    }

    /**
     * Simple mapper that makes Put output.
     */
    static class RandomPutGeneratingMapper extends Mapper<NullWritable, NullWritable, ImmutableBytesWritable, Put> {
        private int keyLength;

        private static final int KEYLEN_DEFAULT = 10;

        private static final String KEYLEN_CONF = "randomkv.key.length";

        private int valLength;

        private static final int VALLEN_DEFAULT = 10;

        private static final String VALLEN_CONF = "randomkv.val.length";

        private static final byte[] QUALIFIER = Bytes.toBytes("data");

        private boolean multiTableMapper = false;

        private TableName[] tables = null;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            Configuration conf = context.getConfiguration();
            keyLength = conf.getInt(TestHFileOutputFormat2.RandomPutGeneratingMapper.KEYLEN_CONF, TestHFileOutputFormat2.RandomPutGeneratingMapper.KEYLEN_DEFAULT);
            valLength = conf.getInt(TestHFileOutputFormat2.RandomPutGeneratingMapper.VALLEN_CONF, TestHFileOutputFormat2.RandomPutGeneratingMapper.VALLEN_DEFAULT);
            multiTableMapper = conf.getBoolean(MULTI_TABLE_HFILEOUTPUTFORMAT_CONF_KEY, false);
            if (multiTableMapper) {
                tables = TestHFileOutputFormat2.TABLE_NAMES;
            } else {
                tables = new TableName[]{ TestHFileOutputFormat2.TABLE_NAMES[0] };
            }
        }

        @Override
        protected void map(NullWritable n1, NullWritable n2, Mapper.Context context) throws IOException, InterruptedException {
            byte[] keyBytes = new byte[keyLength];
            byte[] valBytes = new byte[valLength];
            int taskId = context.getTaskAttemptID().getTaskID().getId();
            assert taskId < (Byte.MAX_VALUE) : "Unit tests dont support > 127 tasks!";
            Random random = new Random();
            byte[] key;
            for (int j = 0; j < (tables.length); ++j) {
                for (int i = 0; i < (TestHFileOutputFormat2.ROWSPERSPLIT); i++) {
                    random.nextBytes(keyBytes);
                    // Ensure that unique tasks generate unique keys
                    keyBytes[((keyLength) - 1)] = ((byte) (taskId & 255));
                    random.nextBytes(valBytes);
                    key = keyBytes;
                    if (multiTableMapper) {
                        key = MultiTableHFileOutputFormat.createCompositeKey(tables[j].getName(), keyBytes);
                    }
                    for (byte[] family : TestHFileOutputFormat2.FAMILIES) {
                        Put p = new Put(keyBytes);
                        p.addColumn(family, TestHFileOutputFormat2.RandomPutGeneratingMapper.QUALIFIER, valBytes);
                        // set TTL to very low so that the scan does not return any value
                        p.setTTL(1L);
                        context.write(new ImmutableBytesWritable(key), p);
                    }
                }
            }
        }
    }

    /**
     * Test that {@link HFileOutputFormat2} RecordWriter writes tags such as ttl into
     * hfile.
     */
    @Test
    public void test_WritingTagData() throws Exception {
        Configuration conf = new Configuration(this.util.getConfiguration());
        final String HFILE_FORMAT_VERSION_CONF_KEY = "hfile.format.version";
        conf.setInt(HFILE_FORMAT_VERSION_CONF_KEY, MIN_FORMAT_VERSION_WITH_TAGS);
        RecordWriter<ImmutableBytesWritable, Cell> writer = null;
        TaskAttemptContext context = null;
        Path dir = util.getDataTestDir("WritingTagData");
        try {
            conf.set(OUTPUT_TABLE_NAME_CONF_KEY, TestHFileOutputFormat2.TABLE_NAMES[0].getNameAsString());
            // turn locality off to eliminate getRegionLocation fail-and-retry time when writing kvs
            conf.setBoolean(LOCALITY_SENSITIVE_CONF_KEY, false);
            Job job = new Job(conf);
            FileOutputFormat.setOutputPath(job, dir);
            context = createTestTaskAttemptContext(job);
            HFileOutputFormat2 hof = new HFileOutputFormat2();
            writer = hof.getRecordWriter(context);
            final byte[] b = Bytes.toBytes("b");
            List<Tag> tags = new ArrayList<>();
            tags.add(new org.apache.hadoop.hbase.ArrayBackedTag(TagType.TTL_TAG_TYPE, Bytes.toBytes(978670)));
            KeyValue kv = new KeyValue(b, b, b, HConstants.LATEST_TIMESTAMP, b, tags);
            writer.write(new ImmutableBytesWritable(), kv);
            writer.close(context);
            writer = null;
            FileSystem fs = dir.getFileSystem(conf);
            RemoteIterator<LocatedFileStatus> iterator = fs.listFiles(dir, true);
            while (iterator.hasNext()) {
                LocatedFileStatus keyFileStatus = iterator.next();
                HFile.Reader reader = HFile.createReader(fs, keyFileStatus.getPath(), new org.apache.hadoop.hbase.io.hfile.CacheConfig(conf), true, conf);
                HFileScanner scanner = reader.getScanner(false, false, false);
                scanner.seekTo();
                Cell cell = scanner.getCell();
                List<Tag> tagsFromCell = PrivateCellUtil.getTags(cell);
                Assert.assertTrue(((tagsFromCell.size()) > 0));
                for (Tag tag : tagsFromCell) {
                    Assert.assertTrue(((tag.getType()) == (TagType.TTL_TAG_TYPE)));
                }
            } 
        } finally {
            if ((writer != null) && (context != null))
                writer.close(context);

            dir.getFileSystem(conf).delete(dir, true);
        }
    }

    // @Ignore("Wahtevs")
    @Test
    public void testMRIncrementalLoadWithPutSortReducer() throws Exception {
        TestHFileOutputFormat2.LOG.info("\nStarting test testMRIncrementalLoadWithPutSortReducer\n");
        doIncrementalLoadTest(false, false, true, "testMRIncrementalLoadWithPutSortReducer");
    }

    @Test
    public void testMultiMRIncrementalLoadWithPutSortReducer() throws Exception {
        TestHFileOutputFormat2.LOG.info("\nStarting test testMultiMRIncrementalLoadWithPutSortReducer\n");
        doIncrementalLoadTest(false, false, true, stream(TestHFileOutputFormat2.TABLE_NAMES).map(TableName::getNameAsString).collect(Collectors.toList()));
    }

    @Test
    public void testBlockStoragePolicy() throws Exception {
        util = new HBaseTestingUtility();
        Configuration conf = util.getConfiguration();
        conf.set(STORAGE_POLICY_PROPERTY, "ALL_SSD");
        conf.set(((STORAGE_POLICY_PROPERTY_CF_PREFIX) + (Bytes.toString(HFileOutputFormat2.combineTableNameSuffix(TestHFileOutputFormat2.TABLE_NAMES[0].getName(), TestHFileOutputFormat2.FAMILIES[0])))), "ONE_SSD");
        Path cf1Dir = new Path(util.getDataTestDir(), Bytes.toString(TestHFileOutputFormat2.FAMILIES[0]));
        Path cf2Dir = new Path(util.getDataTestDir(), Bytes.toString(TestHFileOutputFormat2.FAMILIES[1]));
        util.startMiniDFSCluster(3);
        FileSystem fs = util.getDFSCluster().getFileSystem();
        try {
            fs.mkdirs(cf1Dir);
            fs.mkdirs(cf2Dir);
            // the original block storage policy would be HOT
            String spA = getStoragePolicyName(fs, cf1Dir);
            String spB = getStoragePolicyName(fs, cf2Dir);
            TestHFileOutputFormat2.LOG.debug((("Storage policy of cf 0: [" + spA) + "]."));
            TestHFileOutputFormat2.LOG.debug((("Storage policy of cf 1: [" + spB) + "]."));
            Assert.assertEquals("HOT", spA);
            Assert.assertEquals("HOT", spB);
            // alter table cf schema to change storage policies
            HFileOutputFormat2.configureStoragePolicy(conf, fs, HFileOutputFormat2.combineTableNameSuffix(TestHFileOutputFormat2.TABLE_NAMES[0].getName(), TestHFileOutputFormat2.FAMILIES[0]), cf1Dir);
            HFileOutputFormat2.configureStoragePolicy(conf, fs, HFileOutputFormat2.combineTableNameSuffix(TestHFileOutputFormat2.TABLE_NAMES[0].getName(), TestHFileOutputFormat2.FAMILIES[1]), cf2Dir);
            spA = getStoragePolicyName(fs, cf1Dir);
            spB = getStoragePolicyName(fs, cf2Dir);
            TestHFileOutputFormat2.LOG.debug((("Storage policy of cf 0: [" + spA) + "]."));
            TestHFileOutputFormat2.LOG.debug((("Storage policy of cf 1: [" + spB) + "]."));
            Assert.assertNotNull(spA);
            Assert.assertEquals("ONE_SSD", spA);
            Assert.assertNotNull(spB);
            Assert.assertEquals("ALL_SSD", spB);
        } finally {
            fs.delete(cf1Dir, true);
            fs.delete(cf2Dir, true);
            util.shutdownMiniDFSCluster();
        }
    }

    @Test
    public void TestConfigurePartitioner() throws IOException {
        Configuration conf = util.getConfiguration();
        // Create a user who is not the current user
        String fooUserName = "foo1234";
        String fooGroupName = "group1";
        UserGroupInformation ugi = UserGroupInformation.createUserForTesting(fooUserName, new String[]{ fooGroupName });
        // Get user's home directory
        Path fooHomeDirectory = ugi.doAs(new PrivilegedAction<Path>() {
            @Override
            public Path run() {
                try (FileSystem fs = FileSystem.get(conf)) {
                    return fs.makeQualified(fs.getHomeDirectory());
                } catch (IOException ioe) {
                    TestHFileOutputFormat2.LOG.error("Failed to get foo's home directory", ioe);
                }
                return null;
            }
        });
        Job job = Mockito.mock(Job.class);
        Mockito.doReturn(conf).when(job).getConfiguration();
        ImmutableBytesWritable writable = new ImmutableBytesWritable();
        List<ImmutableBytesWritable> splitPoints = new LinkedList<ImmutableBytesWritable>();
        splitPoints.add(writable);
        ugi.doAs(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    HFileOutputFormat2.configurePartitioner(job, splitPoints, false);
                } catch (IOException ioe) {
                    TestHFileOutputFormat2.LOG.error("Failed to configure partitioner", ioe);
                }
                return null;
            }
        });
        FileSystem fs = FileSystem.get(conf);
        // verify that the job uses TotalOrderPartitioner
        Mockito.verify(job).setPartitionerClass(TotalOrderPartitioner.class);
        // verify that TotalOrderPartitioner.setPartitionFile() is called.
        String partitionPathString = conf.get("mapreduce.totalorderpartitioner.path");
        Assert.assertNotNull(partitionPathString);
        // Make sure the partion file is in foo1234's home directory, and that
        // the file exists.
        Assert.assertTrue(partitionPathString.startsWith(fooHomeDirectory.toString()));
        Assert.assertTrue(fs.exists(new Path(partitionPathString)));
    }
}

