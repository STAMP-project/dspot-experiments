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
package org.apache.hadoop.hbase.master.cleaner;


import CleanerChore.CHORE_POOL_SIZE;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.StoppableImplementation;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestCleanerChore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCleanerChore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCleanerChore.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testSavesFilesOnRequest() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.NeverDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // create the directory layout in the directory to clean
        Path parent = new Path(testDir, "parent");
        Path file = new Path(parent, "someFile");
        fs.mkdirs(parent);
        // touch a new file
        fs.create(file).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        // run the chore
        chore();
        // verify all the files were preserved
        Assert.assertTrue("File shouldn't have been deleted", fs.exists(file));
        Assert.assertTrue("directory shouldn't have been deleted", fs.exists(parent));
    }

    @Test
    public void retriesIOExceptionInStatus() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        Path child = new Path(testDir, "child");
        Path file = new Path(child, "file");
        fs.mkdirs(child);
        fs.create(file).close();
        Assert.assertTrue("test file didn't get created.", fs.exists(file));
        final AtomicBoolean fails = new AtomicBoolean(true);
        FilterFileSystem filtered = new FilterFileSystem(fs) {
            public FileStatus[] listStatus(Path f) throws IOException {
                if (fails.get()) {
                    throw new IOException("whomp whomp.");
                }
                return fs.listStatus(f);
            }
        };
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-retry-ioe", stop, conf, filtered, testDir, confKey);
        // trouble talking to the filesystem
        Boolean result = runCleaner();
        // verify that it couldn't clean the files.
        Assert.assertTrue("test rig failed to inject failure.", fs.exists(file));
        Assert.assertTrue("test rig failed to inject failure.", fs.exists(child));
        // and verify that it accurately reported the failure.
        Assert.assertFalse("chore should report that it failed.", result);
        // filesystem is back
        fails.set(false);
        result = runCleaner();
        // verify everything is gone.
        Assert.assertFalse("file should have been destroyed.", fs.exists(file));
        Assert.assertFalse("directory should have been destroyed.", fs.exists(child));
        // and verify that it accurately reported success.
        Assert.assertTrue("chore should claim it succeeded.", result);
    }

    @Test
    public void testDeletesEmptyDirectories() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // create the directory layout in the directory to clean
        Path parent = new Path(testDir, "parent");
        Path child = new Path(parent, "child");
        Path emptyChild = new Path(parent, "emptyChild");
        Path file = new Path(child, "someFile");
        fs.mkdirs(child);
        fs.mkdirs(emptyChild);
        // touch a new file
        fs.create(file).close();
        // also create a file in the top level directory
        Path topFile = new Path(testDir, "topFile");
        fs.create(topFile).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        Assert.assertTrue("Test file didn't get created.", fs.exists(topFile));
        // run the chore
        chore();
        // verify all the files got deleted
        Assert.assertFalse("File didn't get deleted", fs.exists(topFile));
        Assert.assertFalse("File didn't get deleted", fs.exists(file));
        Assert.assertFalse("Empty directory didn't get deleted", fs.exists(child));
        Assert.assertFalse("Empty directory didn't get deleted", fs.exists(parent));
    }

    /**
     * Test to make sure that we don't attempt to ask the delegate whether or not we should preserve a
     * directory.
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testDoesNotCheckDirectories() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // spy on the delegate to ensure that we don't check for directories
        TestCleanerChore.AlwaysDelete delegate = ((TestCleanerChore.AlwaysDelete) (chore.cleanersChain.get(0)));
        TestCleanerChore.AlwaysDelete spy = Mockito.spy(delegate);
        chore.cleanersChain.set(0, spy);
        // create the directory layout in the directory to clean
        Path parent = new Path(testDir, "parent");
        Path file = new Path(parent, "someFile");
        fs.mkdirs(parent);
        Assert.assertTrue("Test parent didn't get created.", fs.exists(parent));
        // touch a new file
        fs.create(file).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        FileStatus fStat = fs.getFileStatus(parent);
        chore();
        // make sure we never checked the directory
        Mockito.verify(spy, Mockito.never()).isFileDeletable(fStat);
        Mockito.reset(spy);
    }

    @Test
    public void testStoppedCleanerDoesNotDeleteFiles() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // also create a file in the top level directory
        Path topFile = new Path(testDir, "topFile");
        fs.create(topFile).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(topFile));
        // stop the chore
        stop.stop("testing stop");
        // run the chore
        chore();
        // test that the file still exists
        Assert.assertTrue("File got deleted while chore was stopped", fs.exists(topFile));
    }

    /**
     * While cleaning a directory, all the files in the directory may be deleted, but there may be
     * another file added, in which case the directory shouldn't be deleted.
     *
     * @throws IOException
     * 		on failure
     */
    @Test
    public void testCleanerDoesNotDeleteDirectoryWithLateAddedFiles() throws IOException {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        final Path testDir = getDataTestDir();
        final FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // spy on the delegate to ensure that we don't check for directories
        TestCleanerChore.AlwaysDelete delegate = ((TestCleanerChore.AlwaysDelete) (chore.cleanersChain.get(0)));
        TestCleanerChore.AlwaysDelete spy = Mockito.spy(delegate);
        chore.cleanersChain.set(0, spy);
        // create the directory layout in the directory to clean
        final Path parent = new Path(testDir, "parent");
        Path file = new Path(parent, "someFile");
        fs.mkdirs(parent);
        // touch a new file
        fs.create(file).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        final Path addedFile = new Path(parent, "addedFile");
        // when we attempt to delete the original file, add another file in the same directory
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                fs.create(addedFile).close();
                FSUtils.logFileSystemState(fs, testDir, TestCleanerChore.LOG);
                return ((Boolean) (invocation.callRealMethod()));
            }
        }).when(spy).isFileDeletable(Mockito.any());
        // run the chore
        chore();
        // make sure all the directories + added file exist, but the original file is deleted
        Assert.assertTrue("Added file unexpectedly deleted", fs.exists(addedFile));
        Assert.assertTrue("Parent directory deleted unexpectedly", fs.exists(parent));
        Assert.assertFalse("Original file unexpectedly retained", fs.exists(file));
        Mockito.verify(spy, Mockito.times(1)).isFileDeletable(Mockito.any());
        Mockito.reset(spy);
    }

    /**
     * The cleaner runs in a loop, where it first checks to see all the files under a directory can be
     * deleted. If they all can, then we try to delete the directory. However, a file may be added
     * that directory to after the original check. This ensures that we don't accidentally delete that
     * directory on and don't get spurious IOExceptions.
     * <p>
     * This was from HBASE-7465.
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testNoExceptionFromDirectoryWithRacyChildren() throws Exception {
        cleanupTestDir();
        Stoppable stop = new StoppableImplementation();
        // need to use a localutil to not break the rest of the test that runs on the local FS, which
        // gets hosed when we start to use a minicluster.
        HBaseTestingUtility localUtil = new HBaseTestingUtility();
        Configuration conf = localUtil.getConfiguration();
        final Path testDir = getDataTestDir();
        final FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        TestCleanerChore.LOG.debug(("Writing test data to: " + testDir));
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // spy on the delegate to ensure that we don't check for directories
        TestCleanerChore.AlwaysDelete delegate = ((TestCleanerChore.AlwaysDelete) (chore.cleanersChain.get(0)));
        TestCleanerChore.AlwaysDelete spy = Mockito.spy(delegate);
        chore.cleanersChain.set(0, spy);
        // create the directory layout in the directory to clean
        final Path parent = new Path(testDir, "parent");
        Path file = new Path(parent, "someFile");
        fs.mkdirs(parent);
        // touch a new file
        fs.create(file).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        final Path racyFile = new Path(parent, "addedFile");
        // when we attempt to delete the original file, add another file in the same directory
        Mockito.doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                fs.create(racyFile).close();
                FSUtils.logFileSystemState(fs, testDir, TestCleanerChore.LOG);
                return ((Boolean) (invocation.callRealMethod()));
            }
        }).when(spy).isFileDeletable(Mockito.any());
        // run the chore
        chore();
        // make sure all the directories + added file exist, but the original file is deleted
        Assert.assertTrue("Added file unexpectedly deleted", fs.exists(racyFile));
        Assert.assertTrue("Parent directory deleted unexpectedly", fs.exists(parent));
        Assert.assertFalse("Original file unexpectedly retained", fs.exists(file));
        Mockito.verify(spy, Mockito.times(1)).isFileDeletable(Mockito.any());
    }

    @Test
    public void testDeleteFileWithCleanerEnabled() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // Enable cleaner
        setEnabled(true);
        // create the directory layout in the directory to clean
        Path parent = new Path(testDir, "parent");
        Path child = new Path(parent, "child");
        Path file = new Path(child, "someFile");
        fs.mkdirs(child);
        // touch a new file
        fs.create(file).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        // run the chore
        chore();
        // verify all the files got deleted
        Assert.assertFalse("File didn't get deleted", fs.exists(file));
        Assert.assertFalse("Empty directory didn't get deleted", fs.exists(child));
        Assert.assertFalse("Empty directory didn't get deleted", fs.exists(parent));
    }

    @Test
    public void testDeleteFileWithCleanerDisabled() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        // Disable cleaner
        setEnabled(false);
        // create the directory layout in the directory to clean
        Path parent = new Path(testDir, "parent");
        Path child = new Path(parent, "child");
        Path file = new Path(child, "someFile");
        fs.mkdirs(child);
        // touch a new file
        fs.create(file).close();
        Assert.assertTrue("Test file didn't get created.", fs.exists(file));
        // run the chore
        chore();
        // verify all the files exist
        Assert.assertTrue("File got deleted with cleaner disabled", fs.exists(file));
        Assert.assertTrue("Directory got deleted", fs.exists(child));
        Assert.assertTrue("Directory got deleted", fs.exists(parent));
    }

    @Test
    public void testOnConfigurationChange() throws Exception {
        int availableProcessorNum = Runtime.getRuntime().availableProcessors();
        if (availableProcessorNum == 1) {
            // no need to run this test
            return;
        }
        // have at least 2 available processors/cores
        int initPoolSize = availableProcessorNum / 2;
        int changedPoolSize = availableProcessorNum;
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        conf.set(CHORE_POOL_SIZE, String.valueOf(initPoolSize));
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        setEnabled(true);
        // Create subdirs under testDir
        int dirNums = 6;
        Path[] subdirs = new Path[dirNums];
        for (int i = 0; i < dirNums; i++) {
            subdirs[i] = new Path(testDir, ("subdir-" + i));
            fs.mkdirs(subdirs[i]);
        }
        // Under each subdirs create 6 files
        for (Path subdir : subdirs) {
            createFiles(fs, subdir, 6);
        }
        // Start chore
        Thread t = new Thread(() -> chore.chore());
        t.setDaemon(true);
        t.start();
        // Change size of chore's pool
        conf.set(CHORE_POOL_SIZE, String.valueOf(changedPoolSize));
        chore.onConfigurationChange(conf);
        Assert.assertEquals(changedPoolSize, getChorePoolSize());
        // Stop chore
        t.join();
    }

    @Test
    public void testMinimumNumberOfThreads() throws Exception {
        Stoppable stop = new StoppableImplementation();
        Configuration conf = TestCleanerChore.UTIL.getConfiguration();
        Path testDir = getDataTestDir();
        FileSystem fs = TestCleanerChore.UTIL.getTestFileSystem();
        String confKey = "hbase.test.cleaner.delegates";
        conf.set(confKey, TestCleanerChore.AlwaysDelete.class.getName());
        conf.set(CHORE_POOL_SIZE, "2");
        TestCleanerChore.AllValidPaths chore = new TestCleanerChore.AllValidPaths("test-file-cleaner", stop, conf, fs, testDir, confKey);
        int numProcs = Runtime.getRuntime().availableProcessors();
        // Sanity
        Assert.assertEquals(numProcs, calculatePoolSize(Integer.toString(numProcs)));
        // The implementation does not allow us to set more threads than we have processors
        Assert.assertEquals(numProcs, calculatePoolSize(Integer.toString((numProcs + 2))));
        // Force us into the branch that is multiplying 0.0 against the number of processors
        Assert.assertEquals(1, calculatePoolSize("0.0"));
    }

    private static class AllValidPaths extends CleanerChore<BaseHFileCleanerDelegate> {
        public AllValidPaths(String name, Stoppable s, Configuration conf, FileSystem fs, Path oldFileDir, String confkey) {
            super(name, Integer.MAX_VALUE, s, conf, fs, oldFileDir, confkey);
        }

        // all paths are valid
        @Override
        protected boolean validate(Path file) {
            return true;
        }
    }

    public static class AlwaysDelete extends BaseHFileCleanerDelegate {
        @Override
        public boolean isFileDeletable(FileStatus fStat) {
            return true;
        }
    }

    public static class NeverDelete extends BaseHFileCleanerDelegate {
        @Override
        public boolean isFileDeletable(FileStatus fStat) {
            return false;
        }
    }
}

