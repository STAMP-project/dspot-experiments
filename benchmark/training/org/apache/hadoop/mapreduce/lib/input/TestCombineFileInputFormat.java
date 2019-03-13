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
package org.apache.hadoop.mapreduce.lib.input;


import CommonConfigurationKeys.FS_DEFAULT_NAME_KEY;
import com.google.common.collect.HashMultiset;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.HdfsBlockLocation;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat.OneBlockInfo;
import org.apache.hadoop.mapreduce.lib.input.CombineFileInputFormat.OneFileInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TestCombineFileInputFormat {
    private static final String[] rack1 = new String[]{ "/r1" };

    private static final String[] hosts1 = new String[]{ "host1.rack1.com" };

    private static final String[] rack2 = new String[]{ "/r2" };

    private static final String[] hosts2 = new String[]{ "host2.rack2.com" };

    private static final String[] rack3 = new String[]{ "/r3" };

    private static final String[] hosts3 = new String[]{ "host3.rack3.com" };

    final Path inDir = new Path("/racktesting");

    final Path outputPath = new Path("/output");

    final Path dir1 = new Path(inDir, "/dir1");

    final Path dir2 = new Path(inDir, "/dir2");

    final Path dir3 = new Path(inDir, "/dir3");

    final Path dir4 = new Path(inDir, "/dir4");

    final Path dir5 = new Path(inDir, "/dir5");

    static final int BLOCKSIZE = 1024;

    static final byte[] databuf = new byte[TestCombineFileInputFormat.BLOCKSIZE];

    @Mock
    private List<String> mockList;

    private static final String DUMMY_FS_URI = "dummyfs:///";

    /**
     * Dummy class to extend CombineFileInputFormat
     */
    private class DummyInputFormat extends CombineFileInputFormat<Text, Text> {
        @Override
        public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
            return null;
        }
    }

    /**
     * Dummy class to extend CombineFileInputFormat. It allows
     * non-existent files to be passed into the CombineFileInputFormat, allows
     * for easy testing without having to create real files.
     */
    private class DummyInputFormat1 extends TestCombineFileInputFormat.DummyInputFormat {
        @Override
        protected List<FileStatus> listStatus(JobContext job) throws IOException {
            Path[] files = getInputPaths(job);
            List<FileStatus> results = new ArrayList<FileStatus>();
            for (int i = 0; i < (files.length); i++) {
                Path p = files[i];
                FileSystem fs = p.getFileSystem(job.getConfiguration());
                results.add(fs.getFileStatus(p));
            }
            return results;
        }
    }

    /**
     * Dummy class to extend CombineFileInputFormat. It allows
     * testing with files having missing blocks without actually removing replicas.
     */
    public static class MissingBlockFileSystem extends DistributedFileSystem {
        String fileWithMissingBlocks;

        @Override
        public void initialize(URI name, Configuration conf) throws IOException {
            fileWithMissingBlocks = "";
            super.initialize(name, conf);
        }

        @Override
        public BlockLocation[] getFileBlockLocations(FileStatus stat, long start, long len) throws IOException {
            if (stat.isDirectory()) {
                return null;
            }
            System.out.println(("File " + (stat.getPath())));
            String name = stat.getPath().toUri().getPath();
            BlockLocation[] locs = super.getFileBlockLocations(stat, start, len);
            if (name.equals(fileWithMissingBlocks)) {
                System.out.println(("Returning missing blocks for " + (fileWithMissingBlocks)));
                locs[0] = new HdfsBlockLocation(new BlockLocation(new String[0], new String[0], locs[0].getOffset(), locs[0].getLength()), null);
            }
            return locs;
        }

        public void setFileWithMissingBlocks(String f) {
            fileWithMissingBlocks = f;
        }
    }

    private static final String DUMMY_KEY = "dummy.rr.key";

    private static class DummyRecordReader extends RecordReader<Text, Text> {
        private TaskAttemptContext context;

        private CombineFileSplit s;

        private int idx;

        private boolean used;

        public DummyRecordReader(CombineFileSplit split, TaskAttemptContext context, Integer i) {
            this.context = context;
            this.idx = i;
            this.s = split;
            this.used = true;
        }

        /**
         *
         *
         * @return a value specified in the context to check whether the
        context is properly updated by the initialize() method.
         */
        public String getDummyConfVal() {
            return this.context.getConfiguration().get(TestCombineFileInputFormat.DUMMY_KEY);
        }

        public void initialize(InputSplit split, TaskAttemptContext context) {
            this.context = context;
            this.s = ((CombineFileSplit) (split));
            // By setting used to true in the c'tor, but false in initialize,
            // we can check that initialize() is always called before use
            // (e.g., in testReinit()).
            this.used = false;
        }

        public boolean nextKeyValue() {
            boolean ret = !(used);
            this.used = true;
            return ret;
        }

        public Text getCurrentKey() {
            return new Text(this.context.getConfiguration().get(TestCombineFileInputFormat.DUMMY_KEY));
        }

        public Text getCurrentValue() {
            return new Text(this.s.getPath(idx).toString());
        }

        public float getProgress() {
            return used ? 1.0F : 0.0F;
        }

        public void close() {
        }
    }

    /**
     * Extend CFIF to use CFRR with DummyRecordReader
     */
    private class ChildRRInputFormat extends CombineFileInputFormat<Text, Text> {
        @SuppressWarnings("unchecked")
        @Override
        public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
            return new CombineFileRecordReader(((CombineFileSplit) (split)), context, ((Class) (TestCombineFileInputFormat.DummyRecordReader.class)));
        }
    }

    @Test
    public void testRecordReaderInit() throws IOException, InterruptedException {
        // Test that we properly initialize the child recordreader when
        // CombineFileInputFormat and CombineFileRecordReader are used.
        TaskAttemptID taskId = new TaskAttemptID("jt", 0, TaskType.MAP, 0, 0);
        Configuration conf1 = new Configuration();
        conf1.set(TestCombineFileInputFormat.DUMMY_KEY, "STATE1");
        TaskAttemptContext context1 = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf1, taskId);
        // This will create a CombineFileRecordReader that itself contains a
        // DummyRecordReader.
        InputFormat inputFormat = new TestCombineFileInputFormat.ChildRRInputFormat();
        Path[] files = new Path[]{ new Path("file1") };
        long[] lengths = new long[]{ 1 };
        CombineFileSplit split = new CombineFileSplit(files, lengths);
        RecordReader rr = inputFormat.createRecordReader(split, context1);
        Assert.assertTrue("Unexpected RR type!", (rr instanceof CombineFileRecordReader));
        // Verify that the initial configuration is the one being used.
        // Right after construction the dummy key should have value "STATE1"
        Assert.assertEquals("Invalid initial dummy key value", "STATE1", rr.getCurrentKey().toString());
        // Switch the active context for the RecordReader...
        Configuration conf2 = new Configuration();
        conf2.set(TestCombineFileInputFormat.DUMMY_KEY, "STATE2");
        TaskAttemptContext context2 = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf2, taskId);
        rr.initialize(split, context2);
        // And verify that the new context is updated into the child record reader.
        Assert.assertEquals("Invalid secondary dummy key value", "STATE2", rr.getCurrentKey().toString());
    }

    @Test
    public void testReinit() throws Exception {
        // Test that a split containing multiple files works correctly,
        // with the child RecordReader getting its initialize() method
        // called a second time.
        TaskAttemptID taskId = new TaskAttemptID("jt", 0, TaskType.MAP, 0, 0);
        Configuration conf = new Configuration();
        TaskAttemptContext context = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, taskId);
        // This will create a CombineFileRecordReader that itself contains a
        // DummyRecordReader.
        InputFormat inputFormat = new TestCombineFileInputFormat.ChildRRInputFormat();
        Path[] files = new Path[]{ new Path("file1"), new Path("file2") };
        long[] lengths = new long[]{ 1, 1 };
        CombineFileSplit split = new CombineFileSplit(files, lengths);
        RecordReader rr = inputFormat.createRecordReader(split, context);
        Assert.assertTrue("Unexpected RR type!", (rr instanceof CombineFileRecordReader));
        // first initialize() call comes from MapTask. We'll do it here.
        rr.initialize(split, context);
        // First value is first filename.
        Assert.assertTrue(rr.nextKeyValue());
        Assert.assertEquals("file1", rr.getCurrentValue().toString());
        // The inner RR will return false, because it only emits one (k, v) pair.
        // But there's another sub-split to process. This returns true to us.
        Assert.assertTrue(rr.nextKeyValue());
        // And the 2nd rr will have its initialize method called correctly.
        Assert.assertEquals("file2", rr.getCurrentValue().toString());
        // But after both child RR's have returned their singleton (k, v), this
        // should also return false.
        Assert.assertFalse(rr.nextKeyValue());
    }

    /**
     * For testing each split has the expected name, length, and offset.
     */
    private final class Split {
        private String name;

        private long length;

        private long offset;

        public Split(String name, long length, long offset) {
            this.name = name;
            this.length = length;
            this.offset = offset;
        }

        public String getName() {
            return name;
        }

        public long getLength() {
            return length;
        }

        public long getOffset() {
            return offset;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TestCombineFileInputFormat.Split) {
                TestCombineFileInputFormat.Split split = ((TestCombineFileInputFormat.Split) (obj));
                return ((split.name.equals(name)) && ((split.length) == (length))) && ((split.offset) == (offset));
            }
            return false;
        }
    }

    /**
     * The test suppresses unchecked warnings in
     * {@link org.mockito.Mockito#reset}. Although calling the method is
     * a bad manner, we call the method instead of splitting the test
     * (i.e. restarting MiniDFSCluster) to save time.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSplitPlacement() throws Exception {
        MiniDFSCluster dfs = null;
        FileSystem fileSys = null;
        try {
            /* Start 3 datanodes, one each in rack r1, r2, r3. Create five files
            1) file1 and file5, just after starting the datanode on r1, with 
               a repl factor of 1, and,
            2) file2, just after starting the datanode on r2, with 
               a repl factor of 2, and,
            3) file3, file4 after starting the all three datanodes, with a repl 
               factor of 3.
            At the end, file1, file5 will be present on only datanode1, file2 will 
            be present on datanode 1 and datanode2 and 
            file3, file4 will be present on all datanodes.
             */
            Configuration conf = new Configuration();
            conf.setBoolean("dfs.replication.considerLoad", false);
            dfs = racks(TestCombineFileInputFormat.rack1).hosts(TestCombineFileInputFormat.hosts1).build();
            dfs.waitActive();
            fileSys = dfs.getFileSystem();
            if (!(fileSys.mkdirs(inDir))) {
                throw new IOException(("Mkdirs failed to create " + (inDir.toString())));
            }
            Path file1 = new Path(((dir1) + "/file1"));
            TestCombineFileInputFormat.writeFile(conf, file1, ((short) (1)), 1);
            // create another file on the same datanode
            Path file5 = new Path(((dir5) + "/file5"));
            TestCombineFileInputFormat.writeFile(conf, file5, ((short) (1)), 1);
            // split it using a CombinedFile input format
            TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            Job job = Job.getInstance(conf);
            FileInputFormat.setInputPaths(job, (((dir1) + ",") + (dir5)));
            List<InputSplit> splits = inFormat.getSplits(job);
            System.out.println(("Made splits(Test0): " + (splits.size())));
            for (InputSplit split : splits) {
                System.out.println(("File split(Test0): " + split));
            }
            Assert.assertEquals(1, splits.size());
            CombineFileSplit fileSplit = ((CombineFileSplit) (splits.get(0)));
            Assert.assertEquals(2, fileSplit.getNumPaths());
            Assert.assertEquals(1, fileSplit.getLocations().length);
            Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
            Assert.assertEquals(0, fileSplit.getOffset(0));
            Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
            Assert.assertEquals(file5.getName(), fileSplit.getPath(1).getName());
            Assert.assertEquals(0, fileSplit.getOffset(1));
            Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(1));
            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
            dfs.startDataNodes(conf, 1, true, null, TestCombineFileInputFormat.rack2, TestCombineFileInputFormat.hosts2, null);
            dfs.waitActive();
            // create file on two datanodes.
            Path file2 = new Path(((dir2) + "/file2"));
            TestCombineFileInputFormat.writeFile(conf, file2, ((short) (2)), 2);
            // split it using a CombinedFile input format
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.setInputPaths(job, (((dir1) + ",") + (dir2)));
            setMinSplitSizeRack(TestCombineFileInputFormat.BLOCKSIZE);
            splits = inFormat.getSplits(job);
            System.out.println(("Made splits(Test1): " + (splits.size())));
            for (InputSplit split : splits) {
                System.out.println(("File split(Test1): " + split));
            }
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                /**
                 * If rack1 is processed first by
                 * {@link CombineFileInputFormat#createSplits},
                 * create only one split on rack1. Otherwise create two splits.
                 */
                if ((splits.size()) == 2) {
                    // first split is on rack2, contains file2
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(2, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(1).getName());
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getOffset(1));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(1));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    // second split is on rack1, contains file1
                    if (split.equals(splits.get(1))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 1) {
                        // first split is on rack1, contains file1 and file2.
                        Assert.assertEquals(3, fileSplit.getNumPaths());
                        Set<TestCombineFileInputFormat.Split> expected = new HashSet<>();
                        expected.add(new TestCombineFileInputFormat.Split(file1.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                        expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                        expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                        List<TestCombineFileInputFormat.Split> actual = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            String name = fileSplit.getPath(i).getName();
                            long length = fileSplit.getLength(i);
                            long offset = fileSplit.getOffset(i);
                            actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                        }
                        Assert.assertTrue(actual.containsAll(expected));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    } else {
                        Assert.fail(("Expected split size is 1 or 2, but actual size is " + (splits.size())));
                    }

            }
            // create another file on 3 datanodes and 3 racks.
            dfs.startDataNodes(conf, 1, true, null, TestCombineFileInputFormat.rack3, TestCombineFileInputFormat.hosts3, null);
            dfs.waitActive();
            Path file3 = new Path(((dir3) + "/file3"));
            TestCombineFileInputFormat.writeFile(conf, new Path(((dir3) + "/file3")), ((short) (3)), 3);
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.setInputPaths(job, (((((dir1) + ",") + (dir2)) + ",") + (dir3)));
            setMinSplitSizeRack(TestCombineFileInputFormat.BLOCKSIZE);
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test2): " + split));
            }
            Set<TestCombineFileInputFormat.Split> expected = new HashSet<>();
            expected.add(new TestCombineFileInputFormat.Split(file1.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
            expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
            expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, ((TestCombineFileInputFormat.BLOCKSIZE) * 2)));
            List<TestCombineFileInputFormat.Split> actual = new ArrayList<>();
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                /**
                 * If rack1 is processed first by
                 * {@link CombineFileInputFormat#createSplits},
                 * create only one split on rack1.
                 * If rack2 or rack3 is processed first and rack1 is processed second,
                 * create one split on rack2 or rack3 and the other split is on rack1.
                 * Otherwise create 3 splits for each rack.
                 */
                if ((splits.size()) == 3) {
                    // first split is on rack3, contains file3
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(3, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file3.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(file3.getName(), fileSplit.getPath(1).getName());
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getOffset(1));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(1));
                        Assert.assertEquals(file3.getName(), fileSplit.getPath(2).getName());
                        Assert.assertEquals((2 * (TestCombineFileInputFormat.BLOCKSIZE)), fileSplit.getOffset(2));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(2));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts3[0], fileSplit.getLocations()[0]);
                    }
                    // second split is on rack2, contains file2
                    if (split.equals(splits.get(1))) {
                        Assert.assertEquals(2, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(1).getName());
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getOffset(1));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(1));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    // third split is on rack1, contains file1
                    if (split.equals(splits.get(2))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 2) {
                        // first split is on rack2 or rack3, contains one or two files.
                        if (split.equals(splits.get(0))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts2[0])) {
                                Assert.assertEquals(2, fileSplit.getNumPaths());
                            } else
                                if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts3[0])) {
                                    Assert.assertEquals(3, fileSplit.getNumPaths());
                                } else {
                                    Assert.fail("First split should be on rack2 or rack3.");
                                }

                        }
                        // second split is on rack1, contains the rest files.
                        if (split.equals(splits.get(1))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        }
                    } else
                        if ((splits.size()) == 1) {
                            // first split is rack1, contains all three files.
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(6, fileSplit.getNumPaths());
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        } else {
                            Assert.fail("Split size should be 1, 2, or 3.");
                        }


                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
            }
            Assert.assertEquals(6, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // create file4 on all three racks
            Path file4 = new Path(((dir4) + "/file4"));
            TestCombineFileInputFormat.writeFile(conf, file4, ((short) (3)), 3);
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            setMinSplitSizeRack(TestCombineFileInputFormat.BLOCKSIZE);
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test3): " + split));
            }
            expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
            expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
            expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, ((TestCombineFileInputFormat.BLOCKSIZE) * 2)));
            actual.clear();
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                /**
                 * If rack1 is processed first by
                 * {@link CombineFileInputFormat#createSplits},
                 * create only one split on rack1.
                 * If rack2 or rack3 is processed first and rack1 is processed second,
                 * create one split on rack2 or rack3 and the other split is on rack1.
                 * Otherwise create 3 splits for each rack.
                 */
                if ((splits.size()) == 3) {
                    // first split is on rack3, contains file3 and file4
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(6, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts3[0], fileSplit.getLocations()[0]);
                    }
                    // second split is on rack2, contains file2
                    if (split.equals(splits.get(1))) {
                        Assert.assertEquals(2, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(1).getName());
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getOffset(1));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(1));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    // third split is on rack1, contains file1
                    if (split.equals(splits.get(2))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 2) {
                        // first split is on rack2 or rack3, contains two or three files.
                        if (split.equals(splits.get(0))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts2[0])) {
                                Assert.assertEquals(5, fileSplit.getNumPaths());
                            } else
                                if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts3[0])) {
                                    Assert.assertEquals(6, fileSplit.getNumPaths());
                                } else {
                                    Assert.fail("First split should be on rack2 or rack3.");
                                }

                        }
                        // second split is on rack1, contains the rest files.
                        if (split.equals(splits.get(1))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        }
                    } else
                        if ((splits.size()) == 1) {
                            // first split is rack1, contains all four files.
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(9, fileSplit.getNumPaths());
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        } else {
                            Assert.fail("Split size should be 1, 2, or 3.");
                        }


                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
            }
            Assert.assertEquals(9, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // maximum split size is 2 blocks
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            setMinSplitSizeNode(TestCombineFileInputFormat.BLOCKSIZE);
            setMaxSplitSize((2 * (TestCombineFileInputFormat.BLOCKSIZE)));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test4): " + split));
            }
            Assert.assertEquals(5, splits.size());
            actual.clear();
            Mockito.reset(mockList);
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(9, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // verify the splits are on all the racks
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts1[0]);
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts2[0]);
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts3[0]);
            // maximum split size is 3 blocks
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            setMinSplitSizeNode(TestCombineFileInputFormat.BLOCKSIZE);
            setMaxSplitSize((3 * (TestCombineFileInputFormat.BLOCKSIZE)));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test5): " + split));
            }
            Assert.assertEquals(3, splits.size());
            actual.clear();
            Mockito.reset(mockList);
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(9, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts1[0]);
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts2[0]);
            // maximum split size is 4 blocks
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            setMaxSplitSize((4 * (TestCombineFileInputFormat.BLOCKSIZE)));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test6): " + split));
            }
            Assert.assertEquals(3, splits.size());
            actual.clear();
            Mockito.reset(mockList);
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(9, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts1[0]);
            // maximum split size is 7 blocks and min is 3 blocks
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            setMaxSplitSize((7 * (TestCombineFileInputFormat.BLOCKSIZE)));
            setMinSplitSizeNode((3 * (TestCombineFileInputFormat.BLOCKSIZE)));
            setMinSplitSizeRack((3 * (TestCombineFileInputFormat.BLOCKSIZE)));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test7): " + split));
            }
            Assert.assertEquals(2, splits.size());
            actual.clear();
            Mockito.reset(mockList);
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(9, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts1[0]);
            // Rack 1 has file1, file2 and file3 and file4
            // Rack 2 has file2 and file3 and file4
            // Rack 3 has file3 and file4
            // setup a filter so that only (file1 and file2) or (file3 and file4)
            // can be combined
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.addInputPath(job, inDir);
            setMinSplitSizeRack(1);// everything is at least rack local

            createPool(new TestCombineFileInputFormat.TestFilter(dir1), new TestCombineFileInputFormat.TestFilter(dir2));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test1): " + split));
            }
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                if ((splits.size()) == 2) {
                    // first split is on rack1, contains file1 and file2.
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(3, fileSplit.getNumPaths());
                        expected.clear();
                        expected.add(new TestCombineFileInputFormat.Split(file1.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                        expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                        expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                        actual.clear();
                        for (int i = 0; i < 3; i++) {
                            String name = fileSplit.getPath(i).getName();
                            long length = fileSplit.getLength(i);
                            long offset = fileSplit.getOffset(i);
                            actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                        }
                        Assert.assertTrue(actual.containsAll(expected));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                    if (split.equals(splits.get(1))) {
                        // second split contains the file3 and file4, however,
                        // the locations is undetermined.
                        Assert.assertEquals(6, fileSplit.getNumPaths());
                        expected.clear();
                        expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                        expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                        expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, ((TestCombineFileInputFormat.BLOCKSIZE) * 2)));
                        expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                        expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                        expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, ((TestCombineFileInputFormat.BLOCKSIZE) * 2)));
                        actual.clear();
                        for (int i = 0; i < 6; i++) {
                            String name = fileSplit.getPath(i).getName();
                            long length = fileSplit.getLength(i);
                            long offset = fileSplit.getOffset(i);
                            actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                        }
                        Assert.assertTrue(actual.containsAll(expected));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                    }
                } else
                    if ((splits.size()) == 3) {
                        if (split.equals(splits.get(0))) {
                            // first split is on rack2, contains file2
                            Assert.assertEquals(2, fileSplit.getNumPaths());
                            expected.clear();
                            expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                            expected.add(new TestCombineFileInputFormat.Split(file2.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                            actual.clear();
                            for (int i = 0; i < 2; i++) {
                                String name = fileSplit.getPath(i).getName();
                                long length = fileSplit.getLength(i);
                                long offset = fileSplit.getOffset(i);
                                actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                            }
                            Assert.assertTrue(actual.containsAll(expected));
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                        }
                        if (split.equals(splits.get(1))) {
                            // second split is on rack1, contains file1
                            Assert.assertEquals(1, fileSplit.getNumPaths());
                            Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                            Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
                            Assert.assertEquals(0, fileSplit.getOffset(0));
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        }
                        if (split.equals(splits.get(2))) {
                            // third split contains file3 and file4, however,
                            // the locations is undetermined.
                            Assert.assertEquals(6, fileSplit.getNumPaths());
                            expected.clear();
                            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), TestCombineFileInputFormat.BLOCKSIZE, ((TestCombineFileInputFormat.BLOCKSIZE) * 2)));
                            expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, 0));
                            expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, TestCombineFileInputFormat.BLOCKSIZE));
                            expected.add(new TestCombineFileInputFormat.Split(file4.getName(), TestCombineFileInputFormat.BLOCKSIZE, ((TestCombineFileInputFormat.BLOCKSIZE) * 2)));
                            actual.clear();
                            for (int i = 0; i < 6; i++) {
                                String name = fileSplit.getPath(i).getName();
                                long length = fileSplit.getLength(i);
                                long offset = fileSplit.getOffset(i);
                                actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                            }
                            Assert.assertTrue(actual.containsAll(expected));
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                        }
                    } else {
                        Assert.fail("Split size should be 2 or 3.");
                    }

            }
            // measure performance when there are multiple pools and
            // many files in each pool.
            int numPools = 100;
            int numFiles = 1000;
            TestCombineFileInputFormat.DummyInputFormat1 inFormat1 = new TestCombineFileInputFormat.DummyInputFormat1();
            for (int i = 0; i < numFiles; i++) {
                FileInputFormat.setInputPaths(job, file1);
            }
            setMinSplitSizeRack(1);// everything is at least rack local

            final Path dirNoMatch1 = new Path(inDir, "/dirxx");
            final Path dirNoMatch2 = new Path(inDir, "/diryy");
            for (int i = 0; i < numPools; i++) {
                createPool(new TestCombineFileInputFormat.TestFilter(dirNoMatch1), new TestCombineFileInputFormat.TestFilter(dirNoMatch2));
            }
            long start = System.currentTimeMillis();
            splits = inFormat1.getSplits(job);
            long end = System.currentTimeMillis();
            System.out.println(((((((("Elapsed time for " + numPools) + " pools ") + " and ") + numFiles) + " files is ") + ((end - start) / 1000)) + " seconds."));
            // This file has three whole blocks. If the maxsplit size is
            // half the block size, then there should be six splits.
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            setMaxSplitSize(((TestCombineFileInputFormat.BLOCKSIZE) / 2));
            FileInputFormat.setInputPaths(job, dir3);
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test8): " + split));
            }
            Assert.assertEquals(splits.size(), 6);
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
        }
    }

    @Test
    public void testNodeDistribution() throws IOException, InterruptedException {
        TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
        int numBlocks = 60;
        long totLength = 0;
        long blockSize = 100;
        int numNodes = 10;
        long minSizeNode = 50;
        long minSizeRack = 50;
        int maxSplitSize = 200;// 4 blocks per split.

        String[] locations = new String[numNodes];
        for (int i = 0; i < numNodes; i++) {
            locations[i] = "h" + i;
        }
        String[] racks = new String[0];
        Path path = new Path("hdfs://file");
        OneBlockInfo[] blocks = new OneBlockInfo[numBlocks];
        int hostCountBase = 0;
        // Generate block list. Replication 3 per block.
        for (int i = 0; i < numBlocks; i++) {
            int localHostCount = hostCountBase;
            String[] blockHosts = new String[3];
            for (int j = 0; j < 3; j++) {
                int hostNum = localHostCount % numNodes;
                blockHosts[j] = "h" + hostNum;
                localHostCount++;
            }
            hostCountBase++;
            blocks[i] = new OneBlockInfo(path, (i * blockSize), blockSize, blockHosts, racks);
            totLength += blockSize;
        }
        List<InputSplit> splits = new ArrayList<InputSplit>();
        HashMap<String, Set<String>> rackToNodes = new HashMap<String, Set<String>>();
        HashMap<String, List<OneBlockInfo>> rackToBlocks = new HashMap<String, List<OneBlockInfo>>();
        HashMap<OneBlockInfo, String[]> blockToNodes = new HashMap<OneBlockInfo, String[]>();
        Map<String, Set<OneBlockInfo>> nodeToBlocks = new TreeMap<String, Set<OneBlockInfo>>();
        OneFileInfo.populateBlockInfo(blocks, rackToBlocks, blockToNodes, nodeToBlocks, rackToNodes);
        inFormat.createSplits(nodeToBlocks, blockToNodes, rackToBlocks, totLength, maxSplitSize, minSizeNode, minSizeRack, splits);
        int expectedSplitCount = ((int) (totLength / maxSplitSize));
        Assert.assertEquals(expectedSplitCount, splits.size());
        // Ensure 90+% of the splits have node local blocks.
        // 100% locality may not always be achieved.
        int numLocalSplits = 0;
        for (InputSplit inputSplit : splits) {
            Assert.assertEquals(maxSplitSize, inputSplit.getLength());
            if ((inputSplit.getLocations().length) == 1) {
                numLocalSplits++;
            }
        }
        Assert.assertTrue((numLocalSplits >= (0.9 * (splits.size()))));
    }

    @Test
    public void testNodeInputSplit() throws IOException, InterruptedException {
        // Regression test for MAPREDUCE-4892. There are 2 nodes with all blocks on
        // both nodes. The grouping ensures that both nodes get splits instead of
        // just the first node
        TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
        int numBlocks = 12;
        long totLength = 0;
        long blockSize = 100;
        long maxSize = 200;
        long minSizeNode = 50;
        long minSizeRack = 50;
        String[] locations = new String[]{ "h1", "h2" };
        String[] racks = new String[0];
        Path path = new Path("hdfs://file");
        OneBlockInfo[] blocks = new OneBlockInfo[numBlocks];
        for (int i = 0; i < numBlocks; ++i) {
            blocks[i] = new OneBlockInfo(path, (i * blockSize), blockSize, locations, racks);
            totLength += blockSize;
        }
        List<InputSplit> splits = new ArrayList<InputSplit>();
        HashMap<String, Set<String>> rackToNodes = new HashMap<String, Set<String>>();
        HashMap<String, List<OneBlockInfo>> rackToBlocks = new HashMap<String, List<OneBlockInfo>>();
        HashMap<OneBlockInfo, String[]> blockToNodes = new HashMap<OneBlockInfo, String[]>();
        HashMap<String, Set<OneBlockInfo>> nodeToBlocks = new HashMap<String, Set<OneBlockInfo>>();
        OneFileInfo.populateBlockInfo(blocks, rackToBlocks, blockToNodes, nodeToBlocks, rackToNodes);
        inFormat.createSplits(nodeToBlocks, blockToNodes, rackToBlocks, totLength, maxSize, minSizeNode, minSizeRack, splits);
        int expectedSplitCount = ((int) (totLength / maxSize));
        Assert.assertEquals(expectedSplitCount, splits.size());
        HashMultiset<String> nodeSplits = HashMultiset.create();
        for (int i = 0; i < expectedSplitCount; ++i) {
            InputSplit inSplit = splits.get(i);
            Assert.assertEquals(maxSize, inSplit.getLength());
            Assert.assertEquals(1, inSplit.getLocations().length);
            nodeSplits.add(inSplit.getLocations()[0]);
        }
        Assert.assertEquals(3, nodeSplits.count(locations[0]));
        Assert.assertEquals(3, nodeSplits.count(locations[1]));
    }

    /**
     * The test suppresses unchecked warnings in
     * {@link org.mockito.Mockito#reset}. Although calling the method is
     * a bad manner, we call the method instead of splitting the test
     * (i.e. restarting MiniDFSCluster) to save time.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSplitPlacementForCompressedFiles() throws Exception {
        MiniDFSCluster dfs = null;
        FileSystem fileSys = null;
        try {
            /* Start 3 datanodes, one each in rack r1, r2, r3. Create five gzipped
             files
            1) file1 and file5, just after starting the datanode on r1, with 
               a repl factor of 1, and,
            2) file2, just after starting the datanode on r2, with 
               a repl factor of 2, and,
            3) file3, file4 after starting the all three datanodes, with a repl 
               factor of 3.
            At the end, file1, file5 will be present on only datanode1, file2 will 
            be present on datanode 1 and datanode2 and 
            file3, file4 will be present on all datanodes.
             */
            Configuration conf = new Configuration();
            conf.setBoolean("dfs.replication.considerLoad", false);
            dfs = racks(TestCombineFileInputFormat.rack1).hosts(TestCombineFileInputFormat.hosts1).build();
            dfs.waitActive();
            fileSys = dfs.getFileSystem();
            if (!(fileSys.mkdirs(inDir))) {
                throw new IOException(("Mkdirs failed to create " + (inDir.toString())));
            }
            Path file1 = new Path(((dir1) + "/file1.gz"));
            FileStatus f1 = TestCombineFileInputFormat.writeGzipFile(conf, file1, ((short) (1)), 1);
            // create another file on the same datanode
            Path file5 = new Path(((dir5) + "/file5.gz"));
            FileStatus f5 = TestCombineFileInputFormat.writeGzipFile(conf, file5, ((short) (1)), 1);
            // split it using a CombinedFile input format
            TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            Job job = Job.getInstance(conf);
            FileInputFormat.setInputPaths(job, (((dir1) + ",") + (dir5)));
            List<InputSplit> splits = inFormat.getSplits(job);
            System.out.println(("Made splits(Test0): " + (splits.size())));
            for (InputSplit split : splits) {
                System.out.println(("File split(Test0): " + split));
            }
            Assert.assertEquals(1, splits.size());
            CombineFileSplit fileSplit = ((CombineFileSplit) (splits.get(0)));
            Assert.assertEquals(2, fileSplit.getNumPaths());
            Assert.assertEquals(1, fileSplit.getLocations().length);
            Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
            Assert.assertEquals(0, fileSplit.getOffset(0));
            Assert.assertEquals(f1.getLen(), fileSplit.getLength(0));
            Assert.assertEquals(file5.getName(), fileSplit.getPath(1).getName());
            Assert.assertEquals(0, fileSplit.getOffset(1));
            Assert.assertEquals(f5.getLen(), fileSplit.getLength(1));
            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
            dfs.startDataNodes(conf, 1, true, null, TestCombineFileInputFormat.rack2, TestCombineFileInputFormat.hosts2, null);
            dfs.waitActive();
            // create file on two datanodes.
            Path file2 = new Path(((dir2) + "/file2.gz"));
            FileStatus f2 = TestCombineFileInputFormat.writeGzipFile(conf, file2, ((short) (2)), 2);
            // split it using a CombinedFile input format
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.setInputPaths(job, (((dir1) + ",") + (dir2)));
            inFormat.setMinSplitSizeRack(f1.getLen());
            splits = inFormat.getSplits(job);
            System.out.println(("Made splits(Test1): " + (splits.size())));
            // make sure that each split has different locations
            for (InputSplit split : splits) {
                System.out.println(("File split(Test1): " + split));
            }
            Set<TestCombineFileInputFormat.Split> expected = new HashSet<>();
            expected.add(new TestCombineFileInputFormat.Split(file1.getName(), f1.getLen(), 0));
            expected.add(new TestCombineFileInputFormat.Split(file2.getName(), f2.getLen(), 0));
            List<TestCombineFileInputFormat.Split> actual = new ArrayList<>();
            /**
             * If rack1 is processed first by
             * {@link CombineFileInputFormat#createSplits},
             * create only one split on rack1. Otherwise create two splits.
             */
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                if ((splits.size()) == 2) {
                    if (split.equals(splits.get(0))) {
                        // first split is on rack2, contains file2.
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(f2.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    if (split.equals(splits.get(1))) {
                        // second split is on rack1, contains file1.
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(f1.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 1) {
                        // first split is on rack1, contains file1 and file2.
                        Assert.assertEquals(2, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    } else {
                        Assert.fail("Split size should be 1 or 2.");
                    }

                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
            }
            Assert.assertEquals(2, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // create another file on 3 datanodes and 3 racks.
            dfs.startDataNodes(conf, 1, true, null, TestCombineFileInputFormat.rack3, TestCombineFileInputFormat.hosts3, null);
            dfs.waitActive();
            Path file3 = new Path(((dir3) + "/file3.gz"));
            FileStatus f3 = TestCombineFileInputFormat.writeGzipFile(conf, file3, ((short) (3)), 3);
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.setInputPaths(job, (((((dir1) + ",") + (dir2)) + ",") + (dir3)));
            inFormat.setMinSplitSizeRack(f1.getLen());
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test2): " + split));
            }
            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), f3.getLen(), 0));
            actual.clear();
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                /**
                 * If rack1 is processed first by
                 * {@link CombineFileInputFormat#createSplits},
                 * create only one split on rack1.
                 * If rack2 or rack3 is processed first and rack1 is processed second,
                 * create one split on rack2 or rack3 and the other split is on rack1.
                 * Otherwise create 3 splits for each rack.
                 */
                if ((splits.size()) == 3) {
                    // first split is on rack3, contains file3
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(file3.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(f3.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts3[0], fileSplit.getLocations()[0]);
                    }
                    // second split is on rack2, contains file2
                    if (split.equals(splits.get(1))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(f2.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    // third split is on rack1, contains file1
                    if (split.equals(splits.get(2))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(f1.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 2) {
                        // first split is on rack2 or rack3, contains one or two files.
                        if (split.equals(splits.get(0))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts2[0])) {
                                Assert.assertEquals(2, fileSplit.getNumPaths());
                            } else
                                if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts3[0])) {
                                    Assert.assertEquals(1, fileSplit.getNumPaths());
                                } else {
                                    Assert.fail("First split should be on rack2 or rack3.");
                                }

                        }
                        // second split is on rack1, contains the rest files.
                        if (split.equals(splits.get(1))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        }
                    } else
                        if ((splits.size()) == 1) {
                            // first split is rack1, contains all three files.
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(3, fileSplit.getNumPaths());
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        } else {
                            Assert.fail("Split size should be 1, 2, or 3.");
                        }


                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
            }
            Assert.assertEquals(3, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // create file4 on all three racks
            Path file4 = new Path(((dir4) + "/file4.gz"));
            FileStatus f4 = TestCombineFileInputFormat.writeGzipFile(conf, file4, ((short) (3)), 3);
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            inFormat.setMinSplitSizeRack(f1.getLen());
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test3): " + split));
            }
            expected.add(new TestCombineFileInputFormat.Split(file3.getName(), f3.getLen(), 0));
            actual.clear();
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                /**
                 * If rack1 is processed first by
                 * {@link CombineFileInputFormat#createSplits},
                 * create only one split on rack1.
                 * If rack2 or rack3 is processed first and rack1 is processed second,
                 * create one split on rack2 or rack3 and the other split is on rack1.
                 * Otherwise create 3 splits for each rack.
                 */
                if ((splits.size()) == 3) {
                    // first split is on rack3, contains file3 and file4
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(2, fileSplit.getNumPaths());
                        Assert.assertEquals(TestCombineFileInputFormat.hosts3[0], fileSplit.getLocations()[0]);
                    }
                    // second split is on rack2, contains file2
                    if (split.equals(splits.get(1))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(file2.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(f2.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    // third split is on rack1, contains file1
                    if (split.equals(splits.get(2))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
                        Assert.assertEquals(f1.getLen(), fileSplit.getLength(0));
                        Assert.assertEquals(0, fileSplit.getOffset(0));
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 2) {
                        // first split is on rack2 or rack3, contains two or three files.
                        if (split.equals(splits.get(0))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts2[0])) {
                                Assert.assertEquals(3, fileSplit.getNumPaths());
                            } else
                                if (fileSplit.getLocations()[0].equals(TestCombineFileInputFormat.hosts3[0])) {
                                    Assert.assertEquals(2, fileSplit.getNumPaths());
                                } else {
                                    Assert.fail("First split should be on rack2 or rack3.");
                                }

                        }
                        // second split is on rack1, contains the rest files.
                        if (split.equals(splits.get(1))) {
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        }
                    } else
                        if ((splits.size()) == 1) {
                            // first split is rack1, contains all four files.
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(4, fileSplit.getNumPaths());
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        } else {
                            Assert.fail("Split size should be 1, 2, or 3.");
                        }


                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
            }
            Assert.assertEquals(4, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // maximum split size is file1's length
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            inFormat.setMinSplitSizeNode(f1.getLen());
            inFormat.setMaxSplitSize(f1.getLen());
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test4): " + split));
            }
            Assert.assertEquals(4, splits.size());
            actual.clear();
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(4, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts1[0]);
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts2[0]);
            Mockito.verify(mockList, Mockito.atLeastOnce()).add(TestCombineFileInputFormat.hosts3[0]);
            // maximum split size is twice file1's length
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            inFormat.setMinSplitSizeNode(f1.getLen());
            inFormat.setMaxSplitSize((2 * (f1.getLen())));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test5): " + split));
            }
            actual.clear();
            Mockito.reset(mockList);
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(4, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            if ((splits.size()) == 3) {
                // splits are on all the racks
                Mockito.verify(mockList, Mockito.times(1)).add(TestCombineFileInputFormat.hosts1[0]);
                Mockito.verify(mockList, Mockito.times(1)).add(TestCombineFileInputFormat.hosts2[0]);
                Mockito.verify(mockList, Mockito.times(1)).add(TestCombineFileInputFormat.hosts3[0]);
            } else
                if ((splits.size()) == 2) {
                    // one split is on rack1, another split is on rack2 or rack3
                    Mockito.verify(mockList, Mockito.times(1)).add(TestCombineFileInputFormat.hosts1[0]);
                } else {
                    Assert.fail("Split size should be 2 or 3.");
                }

            // maximum split size is 4 times file1's length
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            inFormat.setMinSplitSizeNode((2 * (f1.getLen())));
            inFormat.setMaxSplitSize((4 * (f1.getLen())));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test6): " + split));
            }
            /**
             * If rack1 is processed first by
             * {@link CombineFileInputFormat#createSplits},
             * create only one split on rack1. Otherwise create two splits.
             */
            Assert.assertTrue("Split size should be 1 or 2.", (((splits.size()) == 1) || ((splits.size()) == 2)));
            actual.clear();
            Mockito.reset(mockList);
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
                mockList.add(fileSplit.getLocations()[0]);
            }
            Assert.assertEquals(4, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            Mockito.verify(mockList, Mockito.times(1)).add(TestCombineFileInputFormat.hosts1[0]);
            // maximum split size and min-split-size per rack is 4 times file1's length
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            inFormat.setMaxSplitSize((4 * (f1.getLen())));
            inFormat.setMinSplitSizeRack((4 * (f1.getLen())));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test7): " + split));
            }
            Assert.assertEquals(1, splits.size());
            fileSplit = ((CombineFileSplit) (splits.get(0)));
            Assert.assertEquals(4, fileSplit.getNumPaths());
            Assert.assertEquals(1, fileSplit.getLocations().length);
            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
            // minimum split size per node is 4 times file1's length
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            inFormat.setMinSplitSizeNode((4 * (f1.getLen())));
            FileInputFormat.setInputPaths(job, (((((((dir1) + ",") + (dir2)) + ",") + (dir3)) + ",") + (dir4)));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test8): " + split));
            }
            Assert.assertEquals(1, splits.size());
            fileSplit = ((CombineFileSplit) (splits.get(0)));
            Assert.assertEquals(4, fileSplit.getNumPaths());
            Assert.assertEquals(1, fileSplit.getLocations().length);
            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
            // Rack 1 has file1, file2 and file3 and file4
            // Rack 2 has file2 and file3 and file4
            // Rack 3 has file3 and file4
            // setup a filter so that only file1 and file2 can be combined
            inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            FileInputFormat.addInputPath(job, inDir);
            setMinSplitSizeRack(1);// everything is at least rack local

            createPool(new TestCombineFileInputFormat.TestFilter(dir1), new TestCombineFileInputFormat.TestFilter(dir2));
            splits = inFormat.getSplits(job);
            for (InputSplit split : splits) {
                System.out.println(("File split(Test9): " + split));
            }
            actual.clear();
            for (InputSplit split : splits) {
                fileSplit = ((CombineFileSplit) (split));
                if ((splits.size()) == 3) {
                    // If rack2 is processed first
                    if (split.equals(splits.get(0))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts2[0], fileSplit.getLocations()[0]);
                    }
                    if (split.equals(splits.get(1))) {
                        Assert.assertEquals(1, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                    }
                    if (split.equals(splits.get(2))) {
                        Assert.assertEquals(2, fileSplit.getNumPaths());
                        Assert.assertEquals(1, fileSplit.getLocations().length);
                        Assert.assertEquals(TestCombineFileInputFormat.hosts3[0], fileSplit.getLocations()[0]);
                    }
                } else
                    if ((splits.size()) == 2) {
                        // If rack1 is processed first
                        if (split.equals(splits.get(0))) {
                            Assert.assertEquals(2, fileSplit.getNumPaths());
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
                        }
                        if (split.equals(splits.get(1))) {
                            Assert.assertEquals(2, fileSplit.getNumPaths());
                            Assert.assertEquals(1, fileSplit.getLocations().length);
                            Assert.assertEquals(TestCombineFileInputFormat.hosts3[0], fileSplit.getLocations()[0]);
                        }
                    } else {
                        Assert.fail("Split size should be 2 or 3.");
                    }

                for (int i = 0; i < (fileSplit.getNumPaths()); i++) {
                    String name = fileSplit.getPath(i).getName();
                    long length = fileSplit.getLength(i);
                    long offset = fileSplit.getOffset(i);
                    actual.add(new TestCombineFileInputFormat.Split(name, length, offset));
                }
            }
            Assert.assertEquals(4, actual.size());
            Assert.assertTrue(actual.containsAll(expected));
            // measure performance when there are multiple pools and
            // many files in each pool.
            int numPools = 100;
            int numFiles = 1000;
            TestCombineFileInputFormat.DummyInputFormat1 inFormat1 = new TestCombineFileInputFormat.DummyInputFormat1();
            for (int i = 0; i < numFiles; i++) {
                FileInputFormat.setInputPaths(job, file1);
            }
            setMinSplitSizeRack(1);// everything is at least rack local

            final Path dirNoMatch1 = new Path(inDir, "/dirxx");
            final Path dirNoMatch2 = new Path(inDir, "/diryy");
            for (int i = 0; i < numPools; i++) {
                createPool(new TestCombineFileInputFormat.TestFilter(dirNoMatch1), new TestCombineFileInputFormat.TestFilter(dirNoMatch2));
            }
            long start = System.currentTimeMillis();
            splits = inFormat1.getSplits(job);
            long end = System.currentTimeMillis();
            System.out.println(((((((("Elapsed time for " + numPools) + " pools ") + " and ") + numFiles) + " files is ") + (end - start)) + " milli seconds."));
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
        }
    }

    /**
     * Test that CFIF can handle missing blocks.
     */
    @Test
    public void testMissingBlocks() throws Exception {
        String namenode = null;
        MiniDFSCluster dfs = null;
        FileSystem fileSys = null;
        String testName = "testMissingBlocks";
        try {
            Configuration conf = new Configuration();
            conf.set("fs.hdfs.impl", TestCombineFileInputFormat.MissingBlockFileSystem.class.getName());
            conf.setBoolean("dfs.replication.considerLoad", false);
            dfs = racks(TestCombineFileInputFormat.rack1).hosts(TestCombineFileInputFormat.hosts1).build();
            dfs.waitActive();
            namenode = ((dfs.getFileSystem().getUri().getHost()) + ":") + (dfs.getFileSystem().getUri().getPort());
            fileSys = dfs.getFileSystem();
            if (!(fileSys.mkdirs(inDir))) {
                throw new IOException(("Mkdirs failed to create " + (inDir.toString())));
            }
            Path file1 = new Path(((dir1) + "/file1"));
            TestCombineFileInputFormat.writeFile(conf, file1, ((short) (1)), 1);
            // create another file on the same datanode
            Path file5 = new Path(((dir5) + "/file5"));
            TestCombineFileInputFormat.writeFile(conf, file5, ((short) (1)), 1);
            ((TestCombineFileInputFormat.MissingBlockFileSystem) (fileSys)).setFileWithMissingBlocks(file1.toUri().getPath());
            // split it using a CombinedFile input format
            TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            Job job = Job.getInstance(conf);
            FileInputFormat.setInputPaths(job, (((dir1) + ",") + (dir5)));
            List<InputSplit> splits = inFormat.getSplits(job);
            System.out.println(("Made splits(Test0): " + (splits.size())));
            for (InputSplit split : splits) {
                System.out.println(("File split(Test0): " + split));
            }
            Assert.assertEquals(splits.size(), 1);
            CombineFileSplit fileSplit = ((CombineFileSplit) (splits.get(0)));
            Assert.assertEquals(2, fileSplit.getNumPaths());
            Assert.assertEquals(1, fileSplit.getLocations().length);
            Assert.assertEquals(file1.getName(), fileSplit.getPath(0).getName());
            Assert.assertEquals(0, fileSplit.getOffset(0));
            Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(0));
            Assert.assertEquals(file5.getName(), fileSplit.getPath(1).getName());
            Assert.assertEquals(0, fileSplit.getOffset(1));
            Assert.assertEquals(TestCombineFileInputFormat.BLOCKSIZE, fileSplit.getLength(1));
            Assert.assertEquals(TestCombineFileInputFormat.hosts1[0], fileSplit.getLocations()[0]);
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
        }
    }

    /**
     * Test when the input file's length is 0.
     */
    @Test
    public void testForEmptyFile() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fileSys = FileSystem.get(conf);
        Path file = new Path(("test" + "/file"));
        FSDataOutputStream out = fileSys.create(file, true, conf.getInt("io.file.buffer.size", 4096), ((short) (1)), ((long) (TestCombineFileInputFormat.BLOCKSIZE)));
        out.write(new byte[0]);
        out.close();
        // split it using a CombinedFile input format
        TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
        Job job = Job.getInstance(conf);
        FileInputFormat.setInputPaths(job, "test");
        List<InputSplit> splits = inFormat.getSplits(job);
        Assert.assertEquals(1, splits.size());
        CombineFileSplit fileSplit = ((CombineFileSplit) (splits.get(0)));
        Assert.assertEquals(1, fileSplit.getNumPaths());
        Assert.assertEquals(file.getName(), fileSplit.getPath(0).getName());
        Assert.assertEquals(0, fileSplit.getOffset(0));
        Assert.assertEquals(0, fileSplit.getLength(0));
        fileSys.delete(file.getParent(), true);
    }

    /**
     * Test that directories do not get included as part of getSplits()
     */
    @Test
    public void testGetSplitsWithDirectory() throws Exception {
        MiniDFSCluster dfs = null;
        try {
            Configuration conf = new Configuration();
            dfs = racks(TestCombineFileInputFormat.rack1).hosts(TestCombineFileInputFormat.hosts1).build();
            dfs.waitActive();
            FileSystem fileSys = dfs.getFileSystem();
            // Set up the following directory structure:
            // /dir1/: directory
            // /dir1/file: regular file
            // /dir1/dir2/: directory
            Path dir1 = new Path("/dir1");
            Path file = new Path("/dir1/file1");
            Path dir2 = new Path("/dir1/dir2");
            if (!(fileSys.mkdirs(dir1))) {
                throw new IOException(("Mkdirs failed to create " + (dir1.toString())));
            }
            FSDataOutputStream out = fileSys.create(file);
            out.write(new byte[0]);
            out.close();
            if (!(fileSys.mkdirs(dir2))) {
                throw new IOException(("Mkdirs failed to create " + (dir2.toString())));
            }
            // split it using a CombinedFile input format
            TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
            Job job = Job.getInstance(conf);
            FileInputFormat.setInputPaths(job, "/dir1");
            List<InputSplit> splits = inFormat.getSplits(job);
            // directories should be omitted from getSplits() - we should only see file1 and not dir2
            Assert.assertEquals(1, splits.size());
            CombineFileSplit fileSplit = ((CombineFileSplit) (splits.get(0)));
            Assert.assertEquals(1, fileSplit.getNumPaths());
            Assert.assertEquals(file.getName(), fileSplit.getPath(0).getName());
            Assert.assertEquals(0, fileSplit.getOffset(0));
            Assert.assertEquals(0, fileSplit.getLength(0));
        } finally {
            if (dfs != null) {
                dfs.shutdown();
            }
        }
    }

    /**
     * Test when input files are from non-default file systems
     */
    @Test
    public void testForNonDefaultFileSystem() throws Throwable {
        Configuration conf = new Configuration();
        // use a fake file system scheme as default
        conf.set(FS_DEFAULT_NAME_KEY, TestCombineFileInputFormat.DUMMY_FS_URI);
        // default fs path
        Assert.assertEquals(TestCombineFileInputFormat.DUMMY_FS_URI, FileSystem.getDefaultUri(conf).toString());
        // add a local file
        String localPathRoot = System.getProperty("test.build.data", "build/test/data");
        Path localPath = new Path(localPathRoot, "testFile1");
        FileSystem lfs = FileSystem.getLocal(conf);
        FSDataOutputStream dos = lfs.create(localPath);
        dos.writeChars("Local file for CFIF");
        dos.close();
        Job job = Job.getInstance(conf);
        FileInputFormat.setInputPaths(job, lfs.makeQualified(localPath));
        TestCombineFileInputFormat.DummyInputFormat inFormat = new TestCombineFileInputFormat.DummyInputFormat();
        List<InputSplit> splits = inFormat.getSplits(job);
        Assert.assertTrue(((splits.size()) > 0));
        for (InputSplit s : splits) {
            CombineFileSplit cfs = ((CombineFileSplit) (s));
            for (Path p : cfs.getPaths()) {
                Assert.assertEquals(p.toUri().getScheme(), "file");
            }
        }
    }

    static class TestFilter implements PathFilter {
        private Path p;

        // store a path prefix in this TestFilter
        public TestFilter(Path p) {
            this.p = p;
        }

        // returns true if the specified path matches the prefix stored
        // in this TestFilter.
        public boolean accept(Path path) {
            if ((path.toUri().getPath().indexOf(p.toString())) == 0) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "PathFilter:" + (p);
        }
    }
}

