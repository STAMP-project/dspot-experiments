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


import FileInputFormat.INPUT_DIR;
import FileInputFormat.INPUT_DIR_NONRECURSIVE_IGNORE_SUBDIRS;
import FileInputFormat.INPUT_DIR_RECURSIVE;
import FileInputFormat.LIST_STATUS_NUM_THREADS;
import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.mapred.SplitLocationInfo;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestFileInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileInputFormat.class);

    private static String testTmpDir = System.getProperty("test.build.data", "/tmp");

    private static final Path TEST_ROOT_DIR = new Path(TestFileInputFormat.testTmpDir, "TestFIF");

    private static FileSystem localFs;

    private int numThreads;

    public TestFileInputFormat(int numThreads) {
        this.numThreads = numThreads;
        TestFileInputFormat.LOG.info(("Running with numThreads: " + numThreads));
    }

    @Test
    public void testNumInputFilesRecursively() throws Exception {
        Configuration conf = getConfiguration();
        conf.set(INPUT_DIR_RECURSIVE, "true");
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fileInputFormat = new TextInputFormat();
        List<InputSplit> splits = fileInputFormat.getSplits(job);
        Assert.assertEquals("Input splits are not correct", 3, splits.size());
        verifySplits(Lists.newArrayList("test:/a1/a2/file2", "test:/a1/a2/file3", "test:/a1/file1"), splits);
        // Using the deprecated configuration
        conf = getConfiguration();
        conf.set("mapred.input.dir.recursive", "true");
        job = Job.getInstance(conf);
        splits = fileInputFormat.getSplits(job);
        verifySplits(Lists.newArrayList("test:/a1/a2/file2", "test:/a1/a2/file3", "test:/a1/file1"), splits);
    }

    @Test
    public void testNumInputFilesWithoutRecursively() throws Exception {
        Configuration conf = getConfiguration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fileInputFormat = new TextInputFormat();
        List<InputSplit> splits = fileInputFormat.getSplits(job);
        Assert.assertEquals("Input splits are not correct", 2, splits.size());
        verifySplits(Lists.newArrayList("test:/a1/a2", "test:/a1/file1"), splits);
    }

    @Test
    public void testNumInputFilesIgnoreDirs() throws Exception {
        Configuration conf = getConfiguration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        conf.setBoolean(INPUT_DIR_NONRECURSIVE_IGNORE_SUBDIRS, true);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fileInputFormat = new TextInputFormat();
        List<InputSplit> splits = fileInputFormat.getSplits(job);
        Assert.assertEquals("Input splits are not correct", 1, splits.size());
        verifySplits(Lists.newArrayList("test:/a1/file1"), splits);
    }

    @Test
    public void testListLocatedStatus() throws Exception {
        Configuration conf = getConfiguration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        conf.setBoolean("fs.test.impl.disable.cache", false);
        conf.set(INPUT_DIR, "test:///a1/a2");
        TestFileInputFormat.MockFileSystem mockFs = ((TestFileInputFormat.MockFileSystem) (new Path("test:///").getFileSystem(conf)));
        Assert.assertEquals("listLocatedStatus already called", 0, mockFs.numListLocatedStatusCalls);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fileInputFormat = new TextInputFormat();
        List<InputSplit> splits = fileInputFormat.getSplits(job);
        Assert.assertEquals("Input splits are not correct", 2, splits.size());
        Assert.assertEquals("listLocatedStatuss calls", 1, mockFs.numListLocatedStatusCalls);
        FileSystem.closeAll();
    }

    @Test
    public void testSplitLocationInfo() throws Exception {
        Configuration conf = getConfiguration();
        conf.set(org.apache.hadoop.mapreduce.lib.input.FileInputFormat.INPUT_DIR, "test:///a1/a2");
        Job job = Job.getInstance(conf);
        TextInputFormat fileInputFormat = new TextInputFormat();
        List<InputSplit> splits = fileInputFormat.getSplits(job);
        String[] locations = splits.get(0).getLocations();
        Assert.assertEquals(2, locations.length);
        SplitLocationInfo[] locationInfo = splits.get(0).getLocationInfo();
        Assert.assertEquals(2, locationInfo.length);
        SplitLocationInfo localhostInfo = (locations[0].equals("localhost")) ? locationInfo[0] : locationInfo[1];
        SplitLocationInfo otherhostInfo = (locations[0].equals("otherhost")) ? locationInfo[0] : locationInfo[1];
        Assert.assertTrue(localhostInfo.isOnDisk());
        Assert.assertTrue(localhostInfo.isInMemory());
        Assert.assertTrue(otherhostInfo.isOnDisk());
        Assert.assertFalse(otherhostInfo.isInMemory());
    }

    @Test
    public void testListStatusSimple() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        List<Path> expectedPaths = TestFileInputFormat.configureTestSimple(conf, TestFileInputFormat.localFs);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fif = new TextInputFormat();
        List<FileStatus> statuses = fif.listStatus(job);
        TestFileInputFormat.verifyFileStatuses(expectedPaths, statuses, TestFileInputFormat.localFs);
    }

    @Test
    public void testListStatusNestedRecursive() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        List<Path> expectedPaths = TestFileInputFormat.configureTestNestedRecursive(conf, TestFileInputFormat.localFs);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fif = new TextInputFormat();
        List<FileStatus> statuses = fif.listStatus(job);
        TestFileInputFormat.verifyFileStatuses(expectedPaths, statuses, TestFileInputFormat.localFs);
    }

    @Test
    public void testListStatusNestedNonRecursive() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        List<Path> expectedPaths = TestFileInputFormat.configureTestNestedNonRecursive(conf, TestFileInputFormat.localFs);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fif = new TextInputFormat();
        List<FileStatus> statuses = fif.listStatus(job);
        TestFileInputFormat.verifyFileStatuses(expectedPaths, statuses, TestFileInputFormat.localFs);
    }

    @Test
    public void testListStatusErrorOnNonExistantDir() throws IOException {
        Configuration conf = new Configuration();
        conf.setInt(LIST_STATUS_NUM_THREADS, numThreads);
        TestFileInputFormat.configureTestErrorOnNonExistantDir(conf, TestFileInputFormat.localFs);
        Job job = Job.getInstance(conf);
        FileInputFormat<?, ?> fif = new TextInputFormat();
        try {
            fif.listStatus(job);
            Assert.fail("Expecting an IOException for a missing Input path");
        } catch (IOException e) {
            Path expectedExceptionPath = new Path(TestFileInputFormat.TEST_ROOT_DIR, "input2");
            expectedExceptionPath = TestFileInputFormat.localFs.makeQualified(expectedExceptionPath);
            Assert.assertTrue((e instanceof InvalidInputException));
            Assert.assertEquals(("Input path does not exist: " + (expectedExceptionPath.toString())), e.getMessage());
        }
    }

    static class MockFileSystem extends RawLocalFileSystem {
        int numListLocatedStatusCalls = 0;

        @Override
        public FileStatus[] listStatus(Path f) throws FileNotFoundException, IOException {
            if (f.toString().equals("test:/a1")) {
                return new FileStatus[]{ new FileStatus(0, true, 1, 150, 150, new Path("test:/a1/a2")), new FileStatus(10, false, 1, 150, 150, new Path("test:/a1/file1")) };
            } else
                if (f.toString().equals("test:/a1/a2")) {
                    return new FileStatus[]{ new FileStatus(10, false, 1, 150, 150, new Path("test:/a1/a2/file2")), new FileStatus(10, false, 1, 151, 150, new Path("test:/a1/a2/file3")) };
                }

            return new FileStatus[0];
        }

        @Override
        public FileStatus[] globStatus(Path pathPattern, PathFilter filter) throws IOException {
            return new FileStatus[]{ new FileStatus(10, true, 1, 150, 150, pathPattern) };
        }

        @Override
        public FileStatus[] listStatus(Path f, PathFilter filter) throws FileNotFoundException, IOException {
            return this.listStatus(f);
        }

        @Override
        public BlockLocation[] getFileBlockLocations(FileStatus file, long start, long len) throws IOException {
            return new BlockLocation[]{ new BlockLocation(new String[]{ "localhost:9866", "otherhost:9866" }, new String[]{ "localhost", "otherhost" }, new String[]{ "localhost" }, new String[0], 0, len, false) };
        }

        @Override
        protected RemoteIterator<LocatedFileStatus> listLocatedStatus(Path f, PathFilter filter) throws FileNotFoundException, IOException {
            ++(numListLocatedStatusCalls);
            return super.listLocatedStatus(f, filter);
        }
    }
}

