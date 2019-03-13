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


import FileInputFormat.NUM_INPUT_FILES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestMRCJCFileInputFormat {
    @Test
    public void testAddInputPath() throws IOException {
        final Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///abc/");
        final Job j = Job.getInstance(conf);
        // setup default fs
        final FileSystem defaultfs = FileSystem.get(conf);
        System.out.println(("defaultfs.getUri() = " + (defaultfs.getUri())));
        {
            // test addInputPath
            final Path original = new Path("file:/foo");
            System.out.println(("original = " + original));
            FileInputFormat.addInputPath(j, original);
            final Path[] results = FileInputFormat.getInputPaths(j);
            System.out.println(("results = " + (Arrays.asList(results))));
            Assert.assertEquals(1, results.length);
            Assert.assertEquals(original, results[0]);
        }
        {
            // test setInputPaths
            final Path original = new Path("file:/bar");
            System.out.println(("original = " + original));
            FileInputFormat.setInputPaths(j, original);
            final Path[] results = FileInputFormat.getInputPaths(j);
            System.out.println(("results = " + (Arrays.asList(results))));
            Assert.assertEquals(1, results.length);
            Assert.assertEquals(original, results[0]);
        }
    }

    @Test
    public void testNumInputFiles() throws Exception {
        Configuration conf = Mockito.spy(new Configuration());
        Job mockedJob = Mockito.mock(Job.class);
        Mockito.when(mockedJob.getConfiguration()).thenReturn(conf);
        FileStatus stat = Mockito.mock(FileStatus.class);
        Mockito.when(stat.getLen()).thenReturn(0L);
        TextInputFormat ispy = Mockito.spy(new TextInputFormat());
        Mockito.doReturn(Arrays.asList(stat)).when(ispy).listStatus(mockedJob);
        ispy.getSplits(mockedJob);
        Mockito.verify(conf).setLong(NUM_INPUT_FILES, 1);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testLastInputSplitAtSplitBoundary() throws Exception {
        FileInputFormat fif = new TestMRCJCFileInputFormat.FileInputFormatForTest(((1024L * 1024) * 1024), ((128L * 1024) * 1024));
        Configuration conf = new Configuration();
        JobContext jobContext = Mockito.mock(JobContext.class);
        Mockito.when(jobContext.getConfiguration()).thenReturn(conf);
        List<InputSplit> splits = fif.getSplits(jobContext);
        Assert.assertEquals(8, splits.size());
        for (int i = 0; i < (splits.size()); i++) {
            InputSplit split = splits.get(i);
            Assert.assertEquals(("host" + i), split.getLocations()[0]);
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testLastInputSplitExceedingSplitBoundary() throws Exception {
        FileInputFormat fif = new TestMRCJCFileInputFormat.FileInputFormatForTest(((1027L * 1024) * 1024), ((128L * 1024) * 1024));
        Configuration conf = new Configuration();
        JobContext jobContext = Mockito.mock(JobContext.class);
        Mockito.when(jobContext.getConfiguration()).thenReturn(conf);
        List<InputSplit> splits = fif.getSplits(jobContext);
        Assert.assertEquals(8, splits.size());
        for (int i = 0; i < (splits.size()); i++) {
            InputSplit split = splits.get(i);
            Assert.assertEquals(("host" + i), split.getLocations()[0]);
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testLastInputSplitSingleSplit() throws Exception {
        FileInputFormat fif = new TestMRCJCFileInputFormat.FileInputFormatForTest(((100L * 1024) * 1024), ((128L * 1024) * 1024));
        Configuration conf = new Configuration();
        JobContext jobContext = Mockito.mock(JobContext.class);
        Mockito.when(jobContext.getConfiguration()).thenReturn(conf);
        List<InputSplit> splits = fif.getSplits(jobContext);
        Assert.assertEquals(1, splits.size());
        for (int i = 0; i < (splits.size()); i++) {
            InputSplit split = splits.get(i);
            Assert.assertEquals(("host" + i), split.getLocations()[0]);
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
        FSDataOutputStream out = fileSys.create(file, true, conf.getInt("io.file.buffer.size", 4096), ((short) (1)), ((long) (1024)));
        out.write(new byte[0]);
        out.close();
        // split it using a File input format
        TestMRCJCFileInputFormat.DummyInputFormat inFormat = new TestMRCJCFileInputFormat.DummyInputFormat();
        Job job = Job.getInstance(conf);
        FileInputFormat.setInputPaths(job, "test");
        List<InputSplit> splits = inFormat.getSplits(job);
        Assert.assertEquals(1, splits.size());
        FileSplit fileSplit = ((FileSplit) (splits.get(0)));
        Assert.assertEquals(0, fileSplit.getLocations().length);
        Assert.assertEquals(file.getName(), fileSplit.getPath().getName());
        Assert.assertEquals(0, fileSplit.getStart());
        Assert.assertEquals(0, fileSplit.getLength());
        fileSys.delete(file.getParent(), true);
    }

    /**
     * Dummy class to extend FileInputFormat
     */
    private class DummyInputFormat extends FileInputFormat<Text, Text> {
        @Override
        public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException {
            return null;
        }
    }

    private class FileInputFormatForTest<K, V> extends FileInputFormat<K, V> {
        long splitSize;

        long length;

        FileInputFormatForTest(long length, long splitSize) {
            this.length = length;
            this.splitSize = splitSize;
        }

        @Override
        public RecordReader<K, V> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
            return null;
        }

        @Override
        protected List<FileStatus> listStatus(JobContext job) throws IOException {
            FileStatus mockFileStatus = Mockito.mock(FileStatus.class);
            Mockito.when(mockFileStatus.getBlockSize()).thenReturn(splitSize);
            Path mockPath = Mockito.mock(Path.class);
            FileSystem mockFs = Mockito.mock(FileSystem.class);
            BlockLocation[] blockLocations = mockBlockLocations(length, splitSize);
            Mockito.when(mockFs.getFileBlockLocations(mockFileStatus, 0, length)).thenReturn(blockLocations);
            Mockito.when(mockPath.getFileSystem(ArgumentMatchers.any(Configuration.class))).thenReturn(mockFs);
            Mockito.when(mockFileStatus.getPath()).thenReturn(mockPath);
            Mockito.when(mockFileStatus.getLen()).thenReturn(length);
            List<FileStatus> list = new ArrayList<FileStatus>();
            list.add(mockFileStatus);
            return list;
        }

        @Override
        protected long computeSplitSize(long blockSize, long minSize, long maxSize) {
            return splitSize;
        }

        private BlockLocation[] mockBlockLocations(long size, long splitSize) {
            int numLocations = ((int) (size / splitSize));
            if ((size % splitSize) != 0)
                numLocations++;

            BlockLocation[] blockLocations = new BlockLocation[numLocations];
            for (int i = 0; i < numLocations; i++) {
                String[] names = new String[]{ "b" + i };
                String[] hosts = new String[]{ "host" + i };
                blockLocations[i] = new BlockLocation(names, hosts, (i * splitSize), Math.min(splitSize, (size - (splitSize * i))));
            }
            return blockLocations;
        }
    }
}

