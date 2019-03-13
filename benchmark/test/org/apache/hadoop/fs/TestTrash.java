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
package org.apache.hadoop.fs;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests commands from Trash.
 */
public class TestTrash {
    private static final File BASE_PATH = new File(GenericTestUtils.getTempPath("testTrash"));

    private static final Path TEST_DIR = new Path(TestTrash.BASE_PATH.getAbsolutePath());

    @Test
    public void testTrash() throws IOException {
        Configuration conf = new Configuration();
        conf.setClass("fs.file.impl", TestTrash.TestLFS.class, FileSystem.class);
        TestTrash.trashShell(FileSystem.getLocal(conf), TestTrash.TEST_DIR);
    }

    @Test
    public void testExistingFileTrash() throws IOException {
        Configuration conf = new Configuration();
        conf.setClass("fs.file.impl", TestTrash.TestLFS.class, FileSystem.class);
        FileSystem fs = FileSystem.getLocal(conf);
        conf.set("fs.defaultFS", fs.getUri().toString());
        conf.setLong(FS_TRASH_INTERVAL_KEY, 0);// disabled

        Assert.assertFalse(isEnabled());
        conf.setLong(FS_TRASH_INTERVAL_KEY, (-1));// disabled

        Assert.assertFalse(isEnabled());
        conf.setLong(FS_TRASH_INTERVAL_KEY, 10);// 10 minute

        Assert.assertTrue(isEnabled());
        FsShell shell = new FsShell();
        shell.setConf(conf);
        // First create a new directory with mkdirs
        Path myPath = new Path(TestTrash.TEST_DIR, "test/mkdirs");
        TestTrash.mkdir(fs, myPath);
        // Second, create a file in that directory.
        Path myFile = new Path(TestTrash.TEST_DIR, "test/mkdirs/myExistingFile");
        FileSystemTestHelper.writeFile(fs, myFile, 10);
        // First rm a file
        TestTrash.mkdir(fs, myPath);
        FileSystemTestHelper.writeFile(fs, myFile, 10);
        String[] args1 = new String[2];
        args1[0] = "-rm";
        args1[1] = myFile.toString();
        int val1 = -1;
        try {
            val1 = shell.run(args1);
        } catch (Exception e) {
            System.err.println(("Exception raised from Trash.run " + (e.getLocalizedMessage())));
        }
        Assert.assertTrue((val1 == 0));
        // Second  rm a file which parent path is the same as above
        TestTrash.mkdir(fs, myFile);
        FileSystemTestHelper.writeFile(fs, new Path(myFile, "mySubFile"), 10);
        String[] args2 = new String[2];
        args2[0] = "-rm";
        args2[1] = new Path(myFile, "mySubFile").toString();
        int val2 = -1;
        try {
            val2 = shell.run(args2);
        } catch (Exception e) {
            System.err.println(("Exception raised from Trash.run " + (e.getLocalizedMessage())));
        }
        Assert.assertTrue((val2 == 0));
    }

    @Test
    public void testNonDefaultFS() throws IOException {
        Configuration conf = new Configuration();
        conf.setClass("fs.file.impl", TestTrash.TestLFS.class, FileSystem.class);
        conf.set("fs.defaultFS", "invalid://host/bar/foo");
        TestTrash.trashNonDefaultFS(conf);
    }

    @Test
    public void testPluggableTrash() throws IOException {
        Configuration conf = new Configuration();
        // Test plugged TrashPolicy
        conf.setClass("fs.trash.classname", TestTrash.TestTrashPolicy.class, TrashPolicy.class);
        Trash trash = new Trash(conf);
        Assert.assertTrue(trash.getTrashPolicy().getClass().equals(TestTrash.TestTrashPolicy.class));
    }

    @Test
    public void testCheckpointInterval() throws IOException {
        // Verify if fs.trash.checkpoint.interval is set to positive number
        // but bigger than fs.trash.interval,
        // the value should be reset to fs.trash.interval
        verifyDefaultPolicyIntervalValues(10, 12, 10);
        // Verify if fs.trash.checkpoint.interval is set to positive number
        // and smaller than fs.trash.interval, the value should be respected
        verifyDefaultPolicyIntervalValues(10, 5, 5);
        // Verify if fs.trash.checkpoint.interval sets to 0
        // the value should be reset to fs.trash.interval
        verifyDefaultPolicyIntervalValues(10, 0, 10);
        // Verify if fs.trash.checkpoint.interval sets to a negative number
        // the value should be reset to fs.trash.interval
        verifyDefaultPolicyIntervalValues(10, (-1), 10);
    }

    @Test
    public void testMoveEmptyDirToTrash() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass(FS_FILE_IMPL_KEY, RawLocalFileSystem.class, FileSystem.class);
        conf.setLong(FS_TRASH_INTERVAL_KEY, 1);// 1 min

        FileSystem fs = FileSystem.get(conf);
        TestTrash.verifyMoveEmptyDirToTrash(fs, conf);
    }

    /**
     * Simulate the carrier process of the trash emptier restarts,
     * verify it honors the <b>fs.trash.interval</b> before and after restart.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTrashRestarts() throws Exception {
        Configuration conf = new Configuration();
        conf.setClass("fs.trash.classname", TestTrash.AuditableTrashPolicy.class, TrashPolicy.class);
        conf.setClass("fs.file.impl", TestTrash.TestLFS.class, FileSystem.class);
        conf.set(FS_TRASH_INTERVAL_KEY, "50");// in milliseconds for test

        Trash trash = new Trash(conf);
        // create 5 checkpoints
        for (int i = 0; i < 5; i++) {
            trash.checkpoint();
        }
        // Run the trash emptier for 120ms, it should run
        // 2 times deletion as the interval is 50ms.
        // Verify the checkpoints number when shutting down the emptier.
        verifyAuditableTrashEmptier(trash, 120, 3);
        // reconfigure the interval to 100 ms
        conf.set(FS_TRASH_INTERVAL_KEY, "100");
        Trash trashNew = new Trash(conf);
        // Run the trash emptier for 120ms, it should run
        // 1 time deletion.
        verifyAuditableTrashEmptier(trashNew, 120, 2);
    }

    @Test
    public void testTrashPermission() throws IOException {
        Configuration conf = new Configuration();
        conf.setClass("fs.trash.classname", TrashPolicyDefault.class, TrashPolicy.class);
        conf.setClass("fs.file.impl", TestTrash.TestLFS.class, FileSystem.class);
        conf.set(FS_TRASH_INTERVAL_KEY, "0.2");
        TestTrash.verifyTrashPermission(FileSystem.getLocal(conf), conf);
    }

    @Test
    public void testTrashEmptier() throws Exception {
        Configuration conf = new Configuration();
        // Trash with 12 second deletes and 6 seconds checkpoints
        conf.set(FS_TRASH_INTERVAL_KEY, "0.2");// 12 seconds

        conf.setClass("fs.file.impl", TestTrash.TestLFS.class, FileSystem.class);
        conf.set(FS_TRASH_CHECKPOINT_INTERVAL_KEY, "0.1");// 6 seconds

        FileSystem fs = FileSystem.getLocal(conf);
        conf.set("fs.default.name", fs.getUri().toString());
        Trash trash = new Trash(conf);
        // Start Emptier in background
        Runnable emptier = trash.getEmptier();
        Thread emptierThread = new Thread(emptier);
        emptierThread.start();
        FsShell shell = new FsShell();
        shell.setConf(conf);
        shell.init();
        // First create a new directory with mkdirs
        Path myPath = new Path(TestTrash.TEST_DIR, "test/mkdirs");
        TestTrash.mkdir(fs, myPath);
        int fileIndex = 0;
        Set<String> checkpoints = new HashSet<String>();
        while (true) {
            // Create a file with a new name
            Path myFile = new Path(TestTrash.TEST_DIR, ("test/mkdirs/myFile" + (fileIndex++)));
            FileSystemTestHelper.writeFile(fs, myFile, 10);
            // Delete the file to trash
            String[] args = new String[2];
            args[0] = "-rm";
            args[1] = myFile.toString();
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from Trash.run " + (e.getLocalizedMessage())));
            }
            Assert.assertTrue((val == 0));
            Path trashDir = shell.getCurrentTrashDir();
            FileStatus[] files = fs.listStatus(trashDir.getParent());
            // Scan files in .Trash and add them to set of checkpoints
            for (FileStatus file : files) {
                String fileName = file.getPath().getName();
                checkpoints.add(fileName);
            }
            // If checkpoints has 4 objects it is Current + 3 checkpoint directories
            if ((checkpoints.size()) == 4) {
                // The actual contents should be smaller since the last checkpoint
                // should've been deleted and Current might not have been recreated yet
                Assert.assertTrue(((checkpoints.size()) > (files.length)));
                break;
            }
            Thread.sleep(5000);
        } 
        emptierThread.interrupt();
        emptierThread.join();
    }

    static class TestLFS extends LocalFileSystem {
        Path home;

        TestLFS() {
            this(TestTrash.TEST_DIR);
        }

        TestLFS(final Path home) {
            super(new RawLocalFileSystem() {
                @Override
                protected Path getInitialWorkingDirectory() {
                    return makeQualified(home);
                }

                @Override
                public Path getHomeDirectory() {
                    return makeQualified(home);
                }
            });
            this.home = home;
        }

        @Override
        public Path getHomeDirectory() {
            return home;
        }
    }

    // Test TrashPolicy. Don't care about implementation.
    public static class TestTrashPolicy extends TrashPolicy {
        public TestTrashPolicy() {
        }

        @Override
        public void initialize(Configuration conf, FileSystem fs, Path home) {
        }

        @Override
        public void initialize(Configuration conf, FileSystem fs) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean moveToTrash(Path path) throws IOException {
            return false;
        }

        @Override
        public void createCheckpoint() throws IOException {
        }

        @Override
        public void deleteCheckpoint() throws IOException {
        }

        @Override
        public void deleteCheckpointsImmediately() throws IOException {
        }

        @Override
        public Path getCurrentTrashDir() {
            return null;
        }

        @Override
        public Path getCurrentTrashDir(Path path) throws IOException {
            return null;
        }

        @Override
        public Runnable getEmptier() throws IOException {
            return null;
        }
    }

    /**
     * A fake {@link TrashPolicy} implementation, it keeps a count
     * on number of checkpoints in the trash. It doesn't do anything
     * other than updating the count.
     */
    public static class AuditableTrashPolicy extends TrashPolicy {
        public AuditableTrashPolicy() {
        }

        public AuditableTrashPolicy(Configuration conf) throws IOException {
            this.initialize(conf, null);
        }

        @Override
        @Deprecated
        public void initialize(Configuration conf, FileSystem fs, Path home) {
            this.deletionInterval = ((long) (conf.getFloat(FS_TRASH_INTERVAL_KEY, FS_TRASH_INTERVAL_DEFAULT)));
        }

        @Override
        public void initialize(Configuration conf, FileSystem fs) {
            this.deletionInterval = ((long) (conf.getFloat(FS_TRASH_INTERVAL_KEY, FS_TRASH_INTERVAL_DEFAULT)));
        }

        @Override
        public boolean moveToTrash(Path path) throws IOException {
            return false;
        }

        @Override
        public void createCheckpoint() throws IOException {
            TestTrash.AuditableCheckpoints.add();
        }

        @Override
        public void deleteCheckpoint() throws IOException {
            TestTrash.AuditableCheckpoints.delete();
        }

        @Override
        public void deleteCheckpointsImmediately() throws IOException {
            TestTrash.AuditableCheckpoints.deleteAll();
        }

        @Override
        public Path getCurrentTrashDir() {
            return null;
        }

        @Override
        public Runnable getEmptier() throws IOException {
            return new TestTrash.AuditableTrashPolicy.AuditableEmptier(getConf());
        }

        public int getNumberOfCheckpoints() {
            return TestTrash.AuditableCheckpoints.get();
        }

        /**
         * A fake emptier that simulates to delete a checkpoint
         * in a fixed interval.
         */
        private class AuditableEmptier implements Runnable {
            private Configuration conf = null;

            public AuditableEmptier(Configuration conf) {
                this.conf = conf;
            }

            @Override
            public void run() {
                TestTrash.AuditableTrashPolicy trash = null;
                try {
                    trash = new TestTrash.AuditableTrashPolicy(conf);
                } catch (IOException e1) {
                }
                while (true) {
                    try {
                        Thread.sleep(deletionInterval);
                        trash.deleteCheckpoint();
                    } catch (IOException e) {
                        // no exception
                    } catch (InterruptedException e) {
                        break;
                    }
                } 
            }
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    /**
     * Only counts the number of checkpoints, not do anything more.
     * Declared as an inner static class to share state between
     * testing threads.
     */
    private static class AuditableCheckpoints {
        private static final Logger LOG = LoggerFactory.getLogger(TestTrash.AuditableCheckpoints.class);

        private static AtomicInteger numOfCheckpoint = new AtomicInteger(0);

        private static void add() {
            TestTrash.AuditableCheckpoints.numOfCheckpoint.incrementAndGet();
            TestTrash.AuditableCheckpoints.LOG.info("Create a checkpoint, current number of checkpoints {}", TestTrash.AuditableCheckpoints.numOfCheckpoint.get());
        }

        private static void delete() {
            if ((TestTrash.AuditableCheckpoints.numOfCheckpoint.get()) > 0) {
                TestTrash.AuditableCheckpoints.numOfCheckpoint.decrementAndGet();
                TestTrash.AuditableCheckpoints.LOG.info("Delete a checkpoint, current number of checkpoints {}", TestTrash.AuditableCheckpoints.numOfCheckpoint.get());
            }
        }

        private static void deleteAll() {
            TestTrash.AuditableCheckpoints.numOfCheckpoint.set(0);
            TestTrash.AuditableCheckpoints.LOG.info("Delete all checkpoints, current number of checkpoints {}", TestTrash.AuditableCheckpoints.numOfCheckpoint.get());
        }

        private static int get() {
            return TestTrash.AuditableCheckpoints.numOfCheckpoint.get();
        }
    }
}

