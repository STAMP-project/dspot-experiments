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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_NAMENODE_REPLICATION_MIN_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_SUPERUSERGROUP_KEY;
import FSDirectory.DOT_INODES_STRING;
import FSDirectory.DOT_RESERVED_STRING;
import FSInputChecker.LOG;
import HdfsClientConfigKeys.DFS_NAMENODE_RPC_PORT_DEFAULT;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.Permission;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.DistributedFileSystem;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.server.datanode.FsDatasetTestUtils;
import org.apache.hadoop.hdfs.server.namenode.AclTestHelpers;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Level;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.TIMED_WAITING;


/**
 * This class tests commands from DFSShell.
 */
public class TestDFSShell {
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSShell.class);

    private static final AtomicInteger counter = new AtomicInteger();

    private final int SUCCESS = 0;

    private final int ERROR = 1;

    static final String TEST_ROOT_DIR = PathUtils.getTestDirName(TestDFSShell.class);

    private static final String RAW_A1 = "raw.a1";

    private static final String TRUSTED_A1 = "trusted.a1";

    private static final String USER_A1 = "user.a1";

    private static final byte[] RAW_A1_VALUE = new byte[]{ 50, 50, 50 };

    private static final byte[] TRUSTED_A1_VALUE = new byte[]{ 49, 49, 49 };

    private static final byte[] USER_A1_VALUE = new byte[]{ 49, 50, 51 };

    private static final int BLOCK_SIZE = 1024;

    private static MiniDFSCluster miniCluster;

    private static DistributedFileSystem dfs;

    @Rule
    public Timeout globalTimeout = new Timeout((30 * 1000));// 30s


    @Test(timeout = 30000)
    public void testZeroSizeFile() throws IOException {
        // create a zero size file
        final File f1 = new File(TestDFSShell.TEST_ROOT_DIR, "f1");
        Assert.assertTrue((!(f1.exists())));
        Assert.assertTrue(f1.createNewFile());
        Assert.assertTrue(f1.exists());
        Assert.assertTrue(f1.isFile());
        Assert.assertEquals(0L, f1.length());
        // copy to remote
        final Path root = TestDFSShell.mkdir(TestDFSShell.dfs, new Path("/testZeroSizeFile/zeroSizeFile"));
        final Path remotef = new Path(root, "dst");
        TestDFSShell.show(((("copy local " + f1) + " to remote ") + remotef));
        TestDFSShell.dfs.copyFromLocalFile(false, false, new Path(f1.getPath()), remotef);
        // getBlockSize() should not throw exception
        TestDFSShell.show(("Block size = " + (TestDFSShell.dfs.getFileStatus(remotef).getBlockSize())));
        // copy back
        final File f2 = new File(TestDFSShell.TEST_ROOT_DIR, "f2");
        Assert.assertTrue((!(f2.exists())));
        TestDFSShell.dfs.copyToLocalFile(remotef, new Path(f2.getPath()));
        Assert.assertTrue(f2.exists());
        Assert.assertTrue(f2.isFile());
        Assert.assertEquals(0L, f2.length());
        f1.delete();
        f2.delete();
    }

    @Test(timeout = 30000)
    public void testRecursiveRm() throws IOException {
        final Path parent = new Path("/testRecursiveRm", "parent");
        final Path child = new Path(parent, "child");
        TestDFSShell.dfs.mkdirs(child);
        try {
            TestDFSShell.dfs.delete(parent, false);
            Assert.fail("Should have failed because dir is not empty");
        } catch (IOException e) {
            // should have thrown an exception
        }
        TestDFSShell.dfs.delete(parent, true);
        Assert.assertFalse(TestDFSShell.dfs.exists(parent));
    }

    @Test(timeout = 30000)
    public void testDu() throws IOException {
        int replication = 2;
        PrintStream psBackup = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream psOut = new PrintStream(out);
        System.setOut(psOut);
        FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
        try {
            final Path myPath = new Path("/testDu", "dir");
            Assert.assertTrue(TestDFSShell.dfs.mkdirs(myPath));
            Assert.assertTrue(TestDFSShell.dfs.exists(myPath));
            final Path myFile = new Path(myPath, "file");
            TestDFSShell.writeFile(TestDFSShell.dfs, myFile);
            Assert.assertTrue(TestDFSShell.dfs.exists(myFile));
            final Path myFile2 = new Path(myPath, "file2");
            TestDFSShell.writeFile(TestDFSShell.dfs, myFile2);
            Assert.assertTrue(TestDFSShell.dfs.exists(myFile2));
            Long myFileLength = TestDFSShell.dfs.getFileStatus(myFile).getLen();
            Long myFileDiskUsed = myFileLength * replication;
            Long myFile2Length = TestDFSShell.dfs.getFileStatus(myFile2).getLen();
            Long myFile2DiskUsed = myFile2Length * replication;
            String[] args = new String[2];
            args[0] = "-du";
            args[1] = myPath.toString();
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertTrue((val == 0));
            String returnString = out.toString();
            out.reset();
            // Check if size matches as expected
            Assert.assertThat(returnString, StringContains.containsString(myFileLength.toString()));
            Assert.assertThat(returnString, StringContains.containsString(myFileDiskUsed.toString()));
            Assert.assertThat(returnString, StringContains.containsString(myFile2Length.toString()));
            Assert.assertThat(returnString, StringContains.containsString(myFile2DiskUsed.toString()));
            // Check that -du -s reports the state of the snapshot
            String snapshotName = "ss1";
            Path snapshotPath = new Path(myPath, (".snapshot/" + snapshotName));
            TestDFSShell.dfs.allowSnapshot(myPath);
            Assert.assertThat(TestDFSShell.dfs.createSnapshot(myPath, snapshotName), CoreMatchers.is(snapshotPath));
            Assert.assertThat(TestDFSShell.dfs.delete(myFile, false), CoreMatchers.is(true));
            Assert.assertThat(TestDFSShell.dfs.exists(myFile), CoreMatchers.is(false));
            args = new String[3];
            args[0] = "-du";
            args[1] = "-s";
            args[2] = snapshotPath.toString();
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertThat(val, CoreMatchers.is(0));
            returnString = out.toString();
            out.reset();
            Long combinedLength = myFileLength + myFile2Length;
            Long combinedDiskUsed = myFileDiskUsed + myFile2DiskUsed;
            Assert.assertThat(returnString, StringContains.containsString(combinedLength.toString()));
            Assert.assertThat(returnString, StringContains.containsString(combinedDiskUsed.toString()));
            // Check if output is rendered properly with multiple input paths
            final Path myFile3 = new Path(myPath, "file3");
            TestDFSShell.writeByte(TestDFSShell.dfs, myFile3);
            Assert.assertTrue(TestDFSShell.dfs.exists(myFile3));
            args = new String[3];
            args[0] = "-du";
            args[1] = myFile3.toString();
            args[2] = myFile2.toString();
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals("Return code should be 0.", 0, val);
            returnString = out.toString();
            out.reset();
            Assert.assertTrue(returnString.contains(("1   2   " + (myFile3.toString()))));
            Assert.assertTrue(returnString.contains(("25  50  " + (myFile2.toString()))));
        } finally {
            System.setOut(psBackup);
        }
    }

    @Test(timeout = 180000)
    public void testDuSnapshots() throws IOException {
        final int replication = 2;
        final PrintStream psBackup = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream psOut = new PrintStream(out);
        final FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
        try {
            System.setOut(psOut);
            final Path parent = new Path("/testDuSnapshots");
            final Path dir = new Path(parent, "dir");
            TestDFSShell.mkdir(TestDFSShell.dfs, dir);
            final Path file = new Path(dir, "file");
            TestDFSShell.writeFile(TestDFSShell.dfs, file);
            final Path file2 = new Path(dir, "file2");
            TestDFSShell.writeFile(TestDFSShell.dfs, file2);
            final Long fileLength = TestDFSShell.dfs.getFileStatus(file).getLen();
            final Long fileDiskUsed = fileLength * replication;
            final Long file2Length = TestDFSShell.dfs.getFileStatus(file2).getLen();
            final Long file2DiskUsed = file2Length * replication;
            /* Construct dir as follows:
            /test/dir/file   <- this will later be deleted after snapshot taken.
            /test/dir/newfile <- this will be created after snapshot taken.
            /test/dir/file2
            Snapshot enabled on /test
             */
            // test -du on /test/dir
            int ret = -1;
            try {
                ret = shell.run(new String[]{ "-du", dir.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, ret);
            String returnString = out.toString();
            TestDFSShell.LOG.info(("-du return is:\n" + returnString));
            // Check if size matches as expected
            Assert.assertTrue(returnString.contains(fileLength.toString()));
            Assert.assertTrue(returnString.contains(fileDiskUsed.toString()));
            Assert.assertTrue(returnString.contains(file2Length.toString()));
            Assert.assertTrue(returnString.contains(file2DiskUsed.toString()));
            out.reset();
            // take a snapshot, then remove file and add newFile
            final String snapshotName = "ss1";
            final Path snapshotPath = new Path(parent, (".snapshot/" + snapshotName));
            TestDFSShell.dfs.allowSnapshot(parent);
            Assert.assertThat(TestDFSShell.dfs.createSnapshot(parent, snapshotName), CoreMatchers.is(snapshotPath));
            TestDFSShell.rmr(TestDFSShell.dfs, file);
            final Path newFile = new Path(dir, "newfile");
            TestDFSShell.writeFile(TestDFSShell.dfs, newFile);
            final Long newFileLength = TestDFSShell.dfs.getFileStatus(newFile).getLen();
            final Long newFileDiskUsed = newFileLength * replication;
            // test -du -s on /test
            ret = -1;
            try {
                ret = shell.run(new String[]{ "-du", "-s", parent.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, ret);
            returnString = out.toString();
            TestDFSShell.LOG.info(("-du -s return is:\n" + returnString));
            Long combinedLength = (fileLength + file2Length) + newFileLength;
            Long combinedDiskUsed = (fileDiskUsed + file2DiskUsed) + newFileDiskUsed;
            Assert.assertTrue(returnString.contains(combinedLength.toString()));
            Assert.assertTrue(returnString.contains(combinedDiskUsed.toString()));
            out.reset();
            // test -du on /test
            ret = -1;
            try {
                ret = shell.run(new String[]{ "-du", parent.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, ret);
            returnString = out.toString();
            TestDFSShell.LOG.info(("-du return is:\n" + returnString));
            Assert.assertTrue(returnString.contains(combinedLength.toString()));
            Assert.assertTrue(returnString.contains(combinedDiskUsed.toString()));
            out.reset();
            // test -du -s -x on /test
            ret = -1;
            try {
                ret = shell.run(new String[]{ "-du", "-s", "-x", parent.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, ret);
            returnString = out.toString();
            TestDFSShell.LOG.info(("-du -s -x return is:\n" + returnString));
            Long exludeSnapshotLength = file2Length + newFileLength;
            Long excludeSnapshotDiskUsed = file2DiskUsed + newFileDiskUsed;
            Assert.assertTrue(returnString.contains(exludeSnapshotLength.toString()));
            Assert.assertTrue(returnString.contains(excludeSnapshotDiskUsed.toString()));
            out.reset();
            // test -du -x on /test
            ret = -1;
            try {
                ret = shell.run(new String[]{ "-du", "-x", parent.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, ret);
            returnString = out.toString();
            TestDFSShell.LOG.info(("-du -x return is:\n" + returnString));
            Assert.assertTrue(returnString.contains(exludeSnapshotLength.toString()));
            Assert.assertTrue(returnString.contains(excludeSnapshotDiskUsed.toString()));
            out.reset();
        } finally {
            System.setOut(psBackup);
        }
    }

    @Test(timeout = 180000)
    public void testCountSnapshots() throws IOException {
        final PrintStream psBackup = System.out;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream psOut = new PrintStream(out);
        System.setOut(psOut);
        final FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
        try {
            final Path parent = new Path("/testCountSnapshots");
            final Path dir = new Path(parent, "dir");
            TestDFSShell.mkdir(TestDFSShell.dfs, dir);
            final Path file = new Path(dir, "file");
            TestDFSShell.writeFile(TestDFSShell.dfs, file);
            final Path file2 = new Path(dir, "file2");
            TestDFSShell.writeFile(TestDFSShell.dfs, file2);
            final long fileLength = TestDFSShell.dfs.getFileStatus(file).getLen();
            final long file2Length = TestDFSShell.dfs.getFileStatus(file2).getLen();
            final Path dir2 = new Path(parent, "dir2");
            TestDFSShell.mkdir(TestDFSShell.dfs, dir2);
            /* Construct dir as follows:
            /test/dir/file   <- this will later be deleted after snapshot taken.
            /test/dir/newfile <- this will be created after snapshot taken.
            /test/dir/file2
            /test/dir2       <- this will later be deleted after snapshot taken.
            Snapshot enabled on /test
             */
            // take a snapshot
            // then create /test/dir/newfile and remove /test/dir/file, /test/dir2
            final String snapshotName = "s1";
            final Path snapshotPath = new Path(parent, (".snapshot/" + snapshotName));
            TestDFSShell.dfs.allowSnapshot(parent);
            Assert.assertThat(TestDFSShell.dfs.createSnapshot(parent, snapshotName), CoreMatchers.is(snapshotPath));
            TestDFSShell.rmr(TestDFSShell.dfs, file);
            TestDFSShell.rmr(TestDFSShell.dfs, dir2);
            final Path newFile = new Path(dir, "new file");
            TestDFSShell.writeFile(TestDFSShell.dfs, newFile);
            final Long newFileLength = TestDFSShell.dfs.getFileStatus(newFile).getLen();
            // test -count on /test. Include header for easier debugging.
            int val = -1;
            try {
                val = shell.run(new String[]{ "-count", "-v", parent.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            String returnString = out.toString();
            TestDFSShell.LOG.info(("-count return is:\n" + returnString));
            Scanner in = new Scanner(returnString);
            in.nextLine();
            Assert.assertEquals(3, in.nextLong());// DIR_COUNT

            Assert.assertEquals(3, in.nextLong());// FILE_COUNT

            Assert.assertEquals(((fileLength + file2Length) + newFileLength), in.nextLong());// CONTENT_SIZE

            out.reset();
            // test -count -x on /test. Include header for easier debugging.
            val = -1;
            try {
                val = shell.run(new String[]{ "-count", "-x", "-v", parent.toString() });
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            returnString = out.toString();
            TestDFSShell.LOG.info(("-count -x return is:\n" + returnString));
            in = new Scanner(returnString);
            in.nextLine();
            Assert.assertEquals(2, in.nextLong());// DIR_COUNT

            Assert.assertEquals(2, in.nextLong());// FILE_COUNT

            Assert.assertEquals((file2Length + newFileLength), in.nextLong());// CONTENT_SIZE

            out.reset();
        } finally {
            System.setOut(psBackup);
        }
    }

    @Test(timeout = 30000)
    public void testPut() throws IOException {
        // remove left over crc files:
        new File(TestDFSShell.TEST_ROOT_DIR, ".f1.crc").delete();
        new File(TestDFSShell.TEST_ROOT_DIR, ".f2.crc").delete();
        final File f1 = TestDFSShell.createLocalFile(new File(TestDFSShell.TEST_ROOT_DIR, "f1"));
        final File f2 = TestDFSShell.createLocalFile(new File(TestDFSShell.TEST_ROOT_DIR, "f2"));
        final Path root = TestDFSShell.mkdir(TestDFSShell.dfs, new Path("/testPut"));
        final Path dst = new Path(root, "dst");
        TestDFSShell.show("begin");
        final Thread copy2ndFileThread = new Thread() {
            @Override
            public void run() {
                try {
                    TestDFSShell.show(((("copy local " + f2) + " to remote ") + dst));
                    TestDFSShell.dfs.copyFromLocalFile(false, false, new Path(f2.getPath()), dst);
                } catch (IOException ioe) {
                    TestDFSShell.show(("good " + (StringUtils.stringifyException(ioe))));
                    return;
                }
                // should not be here, must got IOException
                Assert.assertTrue(false);
            }
        };
        // use SecurityManager to pause the copying of f1 and begin copying f2
        SecurityManager sm = System.getSecurityManager();
        System.out.println(("SecurityManager = " + sm));
        System.setSecurityManager(new SecurityManager() {
            private boolean firstTime = true;

            @Override
            public void checkPermission(Permission perm) {
                if (firstTime) {
                    Thread t = Thread.currentThread();
                    if (!(t.toString().contains("DataNode"))) {
                        String s = "" + (Arrays.asList(t.getStackTrace()));
                        if (s.contains("FileUtil.copyContent")) {
                            // pause at FileUtil.copyContent
                            firstTime = false;
                            copy2ndFileThread.start();
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }
            }
        });
        TestDFSShell.show(((("copy local " + f1) + " to remote ") + dst));
        TestDFSShell.dfs.copyFromLocalFile(false, false, new Path(f1.getPath()), dst);
        TestDFSShell.show("done");
        try {
            copy2ndFileThread.join();
        } catch (InterruptedException e) {
        }
        System.setSecurityManager(sm);
        // copy multiple files to destination directory
        final Path destmultiple = TestDFSShell.mkdir(TestDFSShell.dfs, new Path(root, "putmultiple"));
        Path[] srcs = new Path[2];
        srcs[0] = new Path(f1.getPath());
        srcs[1] = new Path(f2.getPath());
        TestDFSShell.dfs.copyFromLocalFile(false, false, srcs, destmultiple);
        srcs[0] = new Path(destmultiple, "f1");
        srcs[1] = new Path(destmultiple, "f2");
        Assert.assertTrue(TestDFSShell.dfs.exists(srcs[0]));
        Assert.assertTrue(TestDFSShell.dfs.exists(srcs[1]));
        // move multiple files to destination directory
        final Path destmultiple2 = TestDFSShell.mkdir(TestDFSShell.dfs, new Path(root, "movemultiple"));
        srcs[0] = new Path(f1.getPath());
        srcs[1] = new Path(f2.getPath());
        TestDFSShell.dfs.moveFromLocalFile(srcs, destmultiple2);
        Assert.assertFalse(f1.exists());
        Assert.assertFalse(f2.exists());
        srcs[0] = new Path(destmultiple2, "f1");
        srcs[1] = new Path(destmultiple2, "f2");
        Assert.assertTrue(TestDFSShell.dfs.exists(srcs[0]));
        Assert.assertTrue(TestDFSShell.dfs.exists(srcs[1]));
        f1.delete();
        f2.delete();
    }

    /**
     * check command error outputs and exit statuses.
     */
    @Test(timeout = 30000)
    public void testErrOutPut() throws Exception {
        PrintStream bak = null;
        try {
            Path root = new Path("/nonexistentfile");
            bak = System.err;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream tmp = new PrintStream(out);
            System.setErr(tmp);
            String[] argv = new String[2];
            argv[0] = "-cat";
            argv[1] = root.toUri().getPath();
            int ret = ToolRunner.run(new FsShell(), argv);
            Assert.assertEquals(" -cat returned 1 ", 1, ret);
            String returned = out.toString();
            Assert.assertTrue("cat does not print exceptions ", ((returned.lastIndexOf("Exception")) == (-1)));
            out.reset();
            argv[0] = "-rm";
            argv[1] = root.toString();
            FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" -rm returned 1 ", 1, ret);
            returned = out.toString();
            out.reset();
            Assert.assertTrue("rm prints reasonable error ", ((returned.lastIndexOf("No such file or directory")) != (-1)));
            argv[0] = "-rmr";
            argv[1] = root.toString();
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" -rmr returned 1", 1, ret);
            returned = out.toString();
            Assert.assertTrue("rmr prints reasonable error ", ((returned.lastIndexOf("No such file or directory")) != (-1)));
            out.reset();
            argv[0] = "-du";
            argv[1] = "/nonexistentfile";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertTrue(" -du prints reasonable error ", ((returned.lastIndexOf("No such file or directory")) != (-1)));
            out.reset();
            argv[0] = "-dus";
            argv[1] = "/nonexistentfile";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertTrue(" -dus prints reasonable error", ((returned.lastIndexOf("No such file or directory")) != (-1)));
            out.reset();
            argv[0] = "-ls";
            argv[1] = "/nonexistenfile";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertTrue(" -ls does not return Found 0 items", ((returned.lastIndexOf("Found 0")) == (-1)));
            out.reset();
            argv[0] = "-ls";
            argv[1] = "/nonexistentfile";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" -lsr should fail ", 1, ret);
            out.reset();
            TestDFSShell.dfs.mkdirs(new Path("/testdir"));
            argv[0] = "-ls";
            argv[1] = "/testdir";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertTrue(" -ls does not print out anything ", ((returned.lastIndexOf("Found 0")) == (-1)));
            out.reset();
            argv[0] = "-ls";
            argv[1] = "/user/nonxistant/*";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" -ls on nonexistent glob returns 1", 1, ret);
            out.reset();
            argv[0] = "-mkdir";
            argv[1] = "/testdir";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertEquals(" -mkdir returned 1 ", 1, ret);
            Assert.assertTrue(" -mkdir returned File exists", ((returned.lastIndexOf("File exists")) != (-1)));
            Path testFile = new Path("/testfile");
            OutputStream outtmp = TestDFSShell.dfs.create(testFile);
            outtmp.write(testFile.toString().getBytes());
            outtmp.close();
            out.reset();
            argv[0] = "-mkdir";
            argv[1] = "/testfile";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertEquals(" -mkdir returned 1", 1, ret);
            Assert.assertTrue(" -mkdir returned this is a file ", ((returned.lastIndexOf("not a directory")) != (-1)));
            out.reset();
            argv[0] = "-mkdir";
            argv[1] = "/testParent/testChild";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertEquals(" -mkdir returned 1", 1, ret);
            Assert.assertTrue(" -mkdir returned there is No file or directory but has testChild in the path", ((returned.lastIndexOf("testChild")) == (-1)));
            out.reset();
            argv = new String[3];
            argv[0] = "-mv";
            argv[1] = "/testfile";
            argv[2] = "/no-such-dir/file";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("mv failed to rename", 1, ret);
            out.reset();
            argv = new String[3];
            argv[0] = "-mv";
            argv[1] = "/testfile";
            argv[2] = "/testfiletest";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertTrue("no output from rename", ((returned.lastIndexOf("Renamed")) == (-1)));
            out.reset();
            argv[0] = "-mv";
            argv[1] = "/testfile";
            argv[2] = "/testfiletmp";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertTrue(" unix like output", ((returned.lastIndexOf("No such file or")) != (-1)));
            out.reset();
            argv = new String[1];
            argv[0] = "-du";
            TestDFSShell.dfs.mkdirs(TestDFSShell.dfs.getHomeDirectory());
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertEquals(" no error ", 0, ret);
            Assert.assertTrue("empty path specified", ((returned.lastIndexOf("empty string")) == (-1)));
            out.reset();
            argv = new String[3];
            argv[0] = "-test";
            argv[1] = "-d";
            argv[2] = "/no/such/dir";
            ret = ToolRunner.run(shell, argv);
            returned = out.toString();
            Assert.assertEquals(" -test -d wrong result ", 1, ret);
            Assert.assertTrue(returned.isEmpty());
        } finally {
            if (bak != null) {
                System.setErr(bak);
            }
        }
    }

    @Test
    public void testMoveWithTargetPortEmpty() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).format(true).numDataNodes(2).nameNodePort(ServerSocketUtil.waitForPort(DFS_NAMENODE_RPC_PORT_DEFAULT, 60)).waitSafeMode(true).build();
            FileSystem srcFs = cluster.getFileSystem();
            FsShell shell = new FsShell();
            shell.setConf(conf);
            String[] argv = new String[2];
            argv[0] = "-mkdir";
            argv[1] = "/testfile";
            ToolRunner.run(shell, argv);
            argv = new String[3];
            argv[0] = "-mv";
            argv[1] = (getUri()) + "/testfile";
            argv[2] = ("hdfs://" + (getUri().getHost())) + "/testfile2";
            int ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("mv should have succeeded", 0, ret);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 30000)
    public void testURIPaths() throws Exception {
        Configuration srcConf = new HdfsConfiguration();
        Configuration dstConf = new HdfsConfiguration();
        MiniDFSCluster srcCluster = null;
        MiniDFSCluster dstCluster = null;
        File bak = new File(PathUtils.getTestDir(getClass()), "testURIPaths");
        bak.mkdirs();
        try {
            srcCluster = new MiniDFSCluster.Builder(srcConf).numDataNodes(2).build();
            dstConf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, bak.getAbsolutePath());
            dstCluster = new MiniDFSCluster.Builder(dstConf).numDataNodes(2).build();
            FileSystem srcFs = srcCluster.getFileSystem();
            FileSystem dstFs = dstCluster.getFileSystem();
            FsShell shell = new FsShell();
            shell.setConf(srcConf);
            // check for ls
            String[] argv = new String[2];
            argv[0] = "-ls";
            argv[1] = (getUri().toString()) + "/";
            int ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("ls works on remote uri ", 0, ret);
            // check for rm -r
            dstFs.mkdirs(new Path("/hadoopdir"));
            argv = new String[2];
            argv[0] = "-rmr";
            argv[1] = (getUri().toString()) + "/hadoopdir";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(("-rmr works on remote uri " + (argv[1])), 0, ret);
            // check du
            argv[0] = "-du";
            argv[1] = (getUri().toString()) + "/";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("du works on remote uri ", 0, ret);
            // check put
            File furi = new File(TestDFSShell.TEST_ROOT_DIR, "furi");
            TestDFSShell.createLocalFile(furi);
            argv = new String[3];
            argv[0] = "-put";
            argv[1] = furi.toURI().toString();
            argv[2] = (getUri().toString()) + "/furi";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" put is working ", 0, ret);
            // check cp
            argv[0] = "-cp";
            argv[1] = (getUri().toString()) + "/furi";
            argv[2] = (getUri().toString()) + "/furi";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" cp is working ", 0, ret);
            Assert.assertTrue(srcFs.exists(new Path("/furi")));
            // check cat
            argv = new String[2];
            argv[0] = "-cat";
            argv[1] = (getUri().toString()) + "/furi";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" cat is working ", 0, ret);
            // check chown
            dstFs.delete(new Path("/furi"), true);
            dstFs.delete(new Path("/hadoopdir"), true);
            String file = "/tmp/chownTest";
            Path path = new Path(file);
            Path parent = new Path("/tmp");
            Path root = new Path("/");
            TestDFSShell.writeFile(dstFs, path);
            TestDFSShell.runCmd(shell, "-chgrp", "-R", "herbivores", ((getUri().toString()) + "/*"));
            confirmOwner(null, "herbivores", dstFs, parent, path);
            TestDFSShell.runCmd(shell, "-chown", "-R", ":reptiles", ((getUri().toString()) + "/"));
            confirmOwner(null, "reptiles", dstFs, root, parent, path);
            // check if default hdfs:/// works
            argv[0] = "-cat";
            argv[1] = "hdfs:///furi";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals(" default works for cat", 0, ret);
            argv[0] = "-ls";
            argv[1] = "hdfs:///";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("default works for ls ", 0, ret);
            argv[0] = "-rmr";
            argv[1] = "hdfs:///furi";
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("default works for rm/rmr", 0, ret);
        } finally {
            if (null != srcCluster) {
                srcCluster.shutdown();
            }
            if (null != dstCluster) {
                dstCluster.shutdown();
            }
        }
    }

    /**
     * Test that -head displays first kilobyte of the file to stdout.
     */
    @Test(timeout = 30000)
    public void testHead() throws Exception {
        final int fileLen = 5 * (TestDFSShell.BLOCK_SIZE);
        // create a text file with multiple KB bytes (and multiple blocks)
        final Path testFile = new Path("testHead", "file1");
        final String text = RandomStringUtils.randomAscii(fileLen);
        try (OutputStream pout = TestDFSShell.dfs.create(testFile)) {
            pout.write(text.getBytes());
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        final String[] argv = new String[]{ "-head", testFile.toString() };
        final int ret = ToolRunner.run(new FsShell(TestDFSShell.dfs.getConf()), argv);
        Assert.assertEquals((((Arrays.toString(argv)) + " returned ") + ret), 0, ret);
        Assert.assertEquals((("-head returned " + (out.size())) + " bytes data, expected 1KB"), 1024, out.size());
        // tailed out last 1KB of the file content
        Assert.assertArrayEquals("Head output doesn't match input", text.substring(0, 1024).getBytes(), out.toByteArray());
        out.reset();
    }

    /**
     * Test that -tail displays last kilobyte of the file to stdout.
     */
    @Test(timeout = 30000)
    public void testTail() throws Exception {
        final int fileLen = 5 * (TestDFSShell.BLOCK_SIZE);
        // create a text file with multiple KB bytes (and multiple blocks)
        final Path testFile = new Path("testTail", "file1");
        final String text = RandomStringUtils.randomAscii(fileLen);
        try (OutputStream pout = TestDFSShell.dfs.create(testFile)) {
            pout.write(text.getBytes());
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        final String[] argv = new String[]{ "-tail", testFile.toString() };
        final int ret = ToolRunner.run(new FsShell(TestDFSShell.dfs.getConf()), argv);
        Assert.assertEquals((((Arrays.toString(argv)) + " returned ") + ret), 0, ret);
        Assert.assertEquals((("-tail returned " + (out.size())) + " bytes data, expected 1KB"), 1024, out.size());
        // tailed out last 1KB of the file content
        Assert.assertArrayEquals("Tail output doesn't match input", text.substring((fileLen - 1024)).getBytes(), out.toByteArray());
        out.reset();
    }

    /**
     * Test that -tail -f outputs appended data as the file grows.
     */
    @Test(timeout = 30000)
    public void testTailWithFresh() throws Exception {
        final Path testFile = new Path("testTailWithFresh", "file1");
        TestDFSShell.dfs.create(testFile);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        final Thread tailer = new Thread() {
            @Override
            public void run() {
                final String[] argv = new String[]{ "-tail", "-f", testFile.toString() };
                try {
                    ToolRunner.run(new FsShell(TestDFSShell.dfs.getConf()), argv);
                } catch (Exception e) {
                    TestDFSShell.LOG.error("Client that tails the test file fails", e);
                } finally {
                    out.reset();
                }
            }
        };
        tailer.start();
        // wait till the tailer is sleeping
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (tailer.getState()) == (TIMED_WAITING);
            }
        }, 100, 10000);
        final String text = RandomStringUtils.randomAscii(((TestDFSShell.BLOCK_SIZE) / 2));
        try (OutputStream pout = TestDFSShell.dfs.create(testFile)) {
            pout.write(text.getBytes());
        }
        // The tailer should eventually show the file contents
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return Arrays.equals(text.getBytes(), out.toByteArray());
            }
        }, 100, 10000);
    }

    @Test(timeout = 30000)
    public void testText() throws Exception {
        final Configuration conf = TestDFSShell.dfs.getConf();
        textTest(new Path("/texttest").makeQualified(TestDFSShell.dfs.getUri(), TestDFSShell.dfs.getWorkingDirectory()), conf);
        final FileSystem lfs = FileSystem.getLocal(conf);
        textTest(new Path(TestDFSShell.TEST_ROOT_DIR, "texttest").makeQualified(getUri(), getWorkingDirectory()), conf);
    }

    @Test(timeout = 30000)
    public void testCopyToLocal() throws IOException {
        FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
        String root = TestDFSShell.createTree(TestDFSShell.dfs, "copyToLocal");
        // Verify copying the tree
        {
            try {
                Assert.assertEquals(0, TestDFSShell.runCmd(shell, "-copyToLocal", (root + "*"), TestDFSShell.TEST_ROOT_DIR));
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            File localroot = new File(TestDFSShell.TEST_ROOT_DIR, "copyToLocal");
            File localroot2 = new File(TestDFSShell.TEST_ROOT_DIR, "copyToLocal2");
            File f1 = new File(localroot, "f1");
            Assert.assertTrue("Copying failed.", f1.isFile());
            File f2 = new File(localroot, "f2");
            Assert.assertTrue("Copying failed.", f2.isFile());
            File sub = new File(localroot, "sub");
            Assert.assertTrue("Copying failed.", sub.isDirectory());
            File f3 = new File(sub, "f3");
            Assert.assertTrue("Copying failed.", f3.isFile());
            File f4 = new File(sub, "f4");
            Assert.assertTrue("Copying failed.", f4.isFile());
            File f5 = new File(localroot2, "f1");
            Assert.assertTrue("Copying failed.", f5.isFile());
            f1.delete();
            f2.delete();
            f3.delete();
            f4.delete();
            f5.delete();
            sub.delete();
        }
        // Verify copying non existing sources do not create zero byte
        // destination files
        {
            String[] args = new String[]{ "-copyToLocal", "nosuchfile", TestDFSShell.TEST_ROOT_DIR };
            try {
                Assert.assertEquals(1, shell.run(args));
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            File f6 = new File(TestDFSShell.TEST_ROOT_DIR, "nosuchfile");
            Assert.assertTrue((!(f6.exists())));
        }
    }

    @Test(timeout = 30000)
    public void testCount() throws Exception {
        FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
        String root = TestDFSShell.createTree(TestDFSShell.dfs, "count");
        // Verify the counts
        TestDFSShell.runCount(root, 2, 4, shell);
        TestDFSShell.runCount((root + "2"), 2, 1, shell);
        TestDFSShell.runCount((root + "2/f1"), 0, 1, shell);
        TestDFSShell.runCount((root + "2/sub"), 1, 0, shell);
        final FileSystem localfs = FileSystem.getLocal(TestDFSShell.dfs.getConf());
        Path localpath = new Path(TestDFSShell.TEST_ROOT_DIR, "testcount");
        localpath = localpath.makeQualified(getUri(), getWorkingDirectory());
        localfs.mkdirs(localpath);
        final String localstr = localpath.toString();
        System.out.println(("localstr=" + localstr));
        TestDFSShell.runCount(localstr, 1, 0, shell);
        Assert.assertEquals(0, TestDFSShell.runCmd(shell, "-count", root, localstr));
    }

    @Test(timeout = 30000)
    public void testTotalSizeOfAllFiles() throws Exception {
        final Path root = new Path("/testTotalSizeOfAllFiles");
        TestDFSShell.dfs.mkdirs(root);
        // create file under root
        FSDataOutputStream File1 = TestDFSShell.dfs.create(new Path(root, "File1"));
        File1.write("hi".getBytes());
        File1.close();
        // create file under sub-folder
        FSDataOutputStream File2 = TestDFSShell.dfs.create(new Path(root, "Folder1/File2"));
        File2.write("hi".getBytes());
        File2.close();
        // getUsed() should return total length of all the files in Filesystem
        Assert.assertEquals(4, TestDFSShell.dfs.getUsed(root));
    }

    @Test(timeout = 30000)
    public void testFilePermissions() throws IOException {
        Configuration conf = new HdfsConfiguration();
        // test chmod on local fs
        FileSystem fs = FileSystem.getLocal(conf);
        testChmod(conf, fs, new File(TestDFSShell.TEST_ROOT_DIR, "chmodTest").getAbsolutePath());
        conf.set(DFS_PERMISSIONS_ENABLED_KEY, "true");
        // test chmod on DFS
        fs = TestDFSShell.dfs;
        conf = TestDFSShell.dfs.getConf();
        testChmod(conf, fs, "/tmp/chmodTest");
        // test chown and chgrp on DFS:
        FsShell shell = new FsShell();
        shell.setConf(conf);
        /* For dfs, I am the super user and I can change owner of any file to
        anything. "-R" option is already tested by chmod test above.
         */
        String file = "/tmp/chownTest";
        Path path = new Path(file);
        Path parent = new Path("/tmp");
        Path root = new Path("/");
        TestDFSShell.writeFile(fs, path);
        TestDFSShell.runCmd(shell, "-chgrp", "-R", "herbivores", "/*", "unknownFile*");
        confirmOwner(null, "herbivores", fs, parent, path);
        TestDFSShell.runCmd(shell, "-chgrp", "mammals", file);
        confirmOwner(null, "mammals", fs, path);
        TestDFSShell.runCmd(shell, "-chown", "-R", ":reptiles", "/");
        confirmOwner(null, "reptiles", fs, root, parent, path);
        TestDFSShell.runCmd(shell, "-chown", "python:", "/nonExistentFile", file);
        confirmOwner("python", "reptiles", fs, path);
        TestDFSShell.runCmd(shell, "-chown", "-R", "hadoop:toys", "unknownFile", "/");
        confirmOwner("hadoop", "toys", fs, root, parent, path);
        // Test different characters in names
        TestDFSShell.runCmd(shell, "-chown", "hdfs.user", file);
        confirmOwner("hdfs.user", null, fs, path);
        TestDFSShell.runCmd(shell, "-chown", "_Hdfs.User-10:_hadoop.users--", file);
        confirmOwner("_Hdfs.User-10", "_hadoop.users--", fs, path);
        TestDFSShell.runCmd(shell, "-chown", "hdfs/hadoop-core@apache.org:asf-projects", file);
        confirmOwner("hdfs/hadoop-core@apache.org", "asf-projects", fs, path);
        TestDFSShell.runCmd(shell, "-chgrp", "hadoop-core@apache.org/100", file);
        confirmOwner(null, "hadoop-core@apache.org/100", fs, path);
    }

    /**
     * Tests various options of DFSShell.
     */
    @Test(timeout = 120000)
    public void testDFSShell() throws Exception {
        /* This tests some properties of ChecksumFileSystem as well.
        Make sure that we create ChecksumDFS
         */
        FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
        // First create a new directory with mkdirs
        Path myPath = new Path("/testDFSShell/mkdirs");
        Assert.assertTrue(TestDFSShell.dfs.mkdirs(myPath));
        Assert.assertTrue(TestDFSShell.dfs.exists(myPath));
        Assert.assertTrue(TestDFSShell.dfs.mkdirs(myPath));
        // Second, create a file in that directory.
        Path myFile = new Path("/testDFSShell/mkdirs/myFile");
        TestDFSShell.writeFile(TestDFSShell.dfs, myFile);
        Assert.assertTrue(TestDFSShell.dfs.exists(myFile));
        Path myFile2 = new Path("/testDFSShell/mkdirs/myFile2");
        TestDFSShell.writeFile(TestDFSShell.dfs, myFile2);
        Assert.assertTrue(TestDFSShell.dfs.exists(myFile2));
        // Verify that rm with a pattern
        {
            String[] args = new String[2];
            args[0] = "-rm";
            args[1] = "/testDFSShell/mkdirs/myFile*";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertTrue((val == 0));
            Assert.assertFalse(TestDFSShell.dfs.exists(myFile));
            Assert.assertFalse(TestDFSShell.dfs.exists(myFile2));
            // re-create the files for other tests
            TestDFSShell.writeFile(TestDFSShell.dfs, myFile);
            Assert.assertTrue(TestDFSShell.dfs.exists(myFile));
            TestDFSShell.writeFile(TestDFSShell.dfs, myFile2);
            Assert.assertTrue(TestDFSShell.dfs.exists(myFile2));
        }
        // Verify that we can read the file
        {
            String[] args = new String[3];
            args[0] = "-cat";
            args[1] = "/testDFSShell/mkdirs/myFile";
            args[2] = "/testDFSShell/mkdirs/myFile2";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run: " + (StringUtils.stringifyException(e))));
            }
            Assert.assertTrue((val == 0));
        }
        TestDFSShell.dfs.delete(myFile2, true);
        // Verify that we get an error while trying to read an nonexistent file
        {
            String[] args = new String[2];
            args[0] = "-cat";
            args[1] = "/testDFSShell/mkdirs/myFile1";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertTrue((val != 0));
        }
        // Verify that we get an error while trying to delete an nonexistent file
        {
            String[] args = new String[2];
            args[0] = "-rm";
            args[1] = "/testDFSShell/mkdirs/myFile1";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertTrue((val != 0));
        }
        // Verify that we succeed in removing the file we created
        {
            String[] args = new String[2];
            args[0] = "-rm";
            args[1] = "/testDFSShell/mkdirs/myFile";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertTrue((val == 0));
        }
        // Verify touch/test
        {
            String[] args;
            int val;
            args = new String[3];
            args[0] = "-test";
            args[1] = "-e";
            args[2] = "/testDFSShell/mkdirs/noFileHere";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
            args[1] = "-z";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
            args = new String[2];
            args[0] = "-touchz";
            args[1] = "/testDFSShell/mkdirs/isFileHere";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            args = new String[2];
            args[0] = "-touchz";
            args[1] = "/testDFSShell/mkdirs/thisDirNotExists/isFileHere";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
            args = new String[3];
            args[0] = "-test";
            args[1] = "-e";
            args[2] = "/testDFSShell/mkdirs/isFileHere";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            args[1] = "-d";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
            args[1] = "-z";
            val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
        }
        // Verify that cp from a directory to a subdirectory fails
        {
            String[] args = new String[2];
            args[0] = "-mkdir";
            args[1] = "/testDFSShell/dir1";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            // this should fail
            String[] args1 = new String[3];
            args1[0] = "-cp";
            args1[1] = "/testDFSShell/dir1";
            args1[2] = "/testDFSShell/dir1/dir2";
            val = 0;
            try {
                val = shell.run(args1);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
            // this should succeed
            args1[0] = "-cp";
            args1[1] = "/testDFSShell/dir1";
            args1[2] = "/testDFSShell/dir1foo";
            val = -1;
            try {
                val = shell.run(args1);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            // this should fail
            args1[0] = "-cp";
            args1[1] = "/";
            args1[2] = "/test";
            val = 0;
            try {
                val = shell.run(args1);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
        }
        // Verify -test -f negative case (missing file)
        {
            String[] args = new String[3];
            args[0] = "-test";
            args[1] = "-f";
            args[2] = "/testDFSShell/mkdirs/noFileHere";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
        }
        // Verify -test -f negative case (directory rather than file)
        {
            String[] args = new String[3];
            args[0] = "-test";
            args[1] = "-f";
            args[2] = "/testDFSShell/mkdirs";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
        }
        // Verify -test -f positive case
        {
            TestDFSShell.writeFile(TestDFSShell.dfs, myFile);
            Assert.assertTrue(TestDFSShell.dfs.exists(myFile));
            String[] args = new String[3];
            args[0] = "-test";
            args[1] = "-f";
            args[2] = myFile.toString();
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
        }
        // Verify -test -s negative case (missing file)
        {
            String[] args = new String[3];
            args[0] = "-test";
            args[1] = "-s";
            args[2] = "/testDFSShell/mkdirs/noFileHere";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
        }
        // Verify -test -s negative case (zero length file)
        {
            String[] args = new String[3];
            args[0] = "-test";
            args[1] = "-s";
            args[2] = "/testDFSShell/mkdirs/isFileHere";
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(1, val);
        }
        // Verify -test -s positive case (nonzero length file)
        {
            String[] args = new String[3];
            args[0] = "-test";
            args[1] = "-s";
            args[2] = myFile.toString();
            int val = -1;
            try {
                val = shell.run(args);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
        }
        // Verify -test -w/-r
        {
            Path permDir = new Path("/testDFSShell/permDir");
            Path permFile = new Path("/testDFSShell/permDir/permFile");
            TestDFSShell.mkdir(TestDFSShell.dfs, permDir);
            TestDFSShell.writeFile(TestDFSShell.dfs, permFile);
            // Verify -test -w positive case (dir exists and can write)
            final String[] wargs = new String[3];
            wargs[0] = "-test";
            wargs[1] = "-w";
            wargs[2] = permDir.toString();
            int val = -1;
            try {
                val = shell.run(wargs);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            // Verify -test -r positive case (file exists and can read)
            final String[] rargs = new String[3];
            rargs[0] = "-test";
            rargs[1] = "-r";
            rargs[2] = permFile.toString();
            try {
                val = shell.run(rargs);
            } catch (Exception e) {
                System.err.println(("Exception raised from DFSShell.run " + (e.getLocalizedMessage())));
            }
            Assert.assertEquals(0, val);
            // Verify -test -r negative case (file exists but cannot read)
            TestDFSShell.runCmd(shell, "-chmod", "600", permFile.toString());
            UserGroupInformation smokeUser = UserGroupInformation.createUserForTesting("smokeUser", new String[]{ "hadoop" });
            smokeUser.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws Exception {
                    FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
                    int exitCode = shell.run(rargs);
                    Assert.assertEquals(1, exitCode);
                    return null;
                }
            });
            // Verify -test -w negative case (dir exists but cannot write)
            TestDFSShell.runCmd(shell, "-chown", "-R", "not_allowed", permDir.toString());
            TestDFSShell.runCmd(shell, "-chmod", "-R", "700", permDir.toString());
            smokeUser.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() throws Exception {
                    FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
                    int exitCode = shell.run(wargs);
                    Assert.assertEquals(1, exitCode);
                    return null;
                }
            });
            // cleanup
            TestDFSShell.dfs.delete(permDir, true);
        }
    }

    static interface TestGetRunner {
        String run(int exitcode, String... options) throws IOException;
    }

    @Test(timeout = 30000)
    public void testRemoteException() throws Exception {
        UserGroupInformation tmpUGI = UserGroupInformation.createUserForTesting("tmpname", new String[]{ "mygroup" });
        PrintStream bak = null;
        try {
            Path p = new Path("/foo");
            TestDFSShell.dfs.mkdirs(p);
            TestDFSShell.dfs.setPermission(p, new FsPermission(((short) (448))));
            bak = System.err;
            tmpUGI.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    FsShell fshell = new FsShell(TestDFSShell.dfs.getConf());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    PrintStream tmp = new PrintStream(out);
                    System.setErr(tmp);
                    String[] args = new String[2];
                    args[0] = "-ls";
                    args[1] = "/foo";
                    int ret = ToolRunner.run(fshell, args);
                    Assert.assertEquals("returned should be 1", 1, ret);
                    String str = out.toString();
                    Assert.assertTrue("permission denied printed", ((str.indexOf("Permission denied")) != (-1)));
                    out.reset();
                    return null;
                }
            });
        } finally {
            if (bak != null) {
                System.setErr(bak);
            }
        }
    }

    @Test(timeout = 30000)
    public void testGet() throws IOException {
        GenericTestUtils.setLogLevel(FSInputChecker.LOG, Level.ALL);
        final String fname = "testGet.txt";
        Path root = new Path("/test/get");
        final Path remotef = new Path(root, fname);
        final Configuration conf = new HdfsConfiguration();
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        TestDFSShell.TestGetRunner runner = new TestDFSShell.TestGetRunner() {
            private int count = 0;

            private final FsShell shell = new FsShell(conf);

            public String run(int exitcode, String... options) throws IOException {
                String dst = new File(TestDFSShell.TEST_ROOT_DIR, (fname + (++(count)))).getAbsolutePath();
                String[] args = new String[(options.length) + 3];
                args[0] = "-get";
                args[((args.length) - 2)] = remotef.toString();
                args[((args.length) - 1)] = dst;
                for (int i = 0; i < (options.length); i++) {
                    args[(i + 1)] = options[i];
                }
                TestDFSShell.show(("args=" + (Arrays.asList(args))));
                try {
                    Assert.assertEquals(exitcode, shell.run(args));
                } catch (Exception e) {
                    Assert.assertTrue(StringUtils.stringifyException(e), false);
                }
                return exitcode == 0 ? DFSTestUtil.readFile(new File(dst)) : null;
            }
        };
        File localf = TestDFSShell.createLocalFile(new File(TestDFSShell.TEST_ROOT_DIR, fname));
        MiniDFSCluster cluster = null;
        DistributedFileSystem dfs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).format(true).build();
            dfs = cluster.getFileSystem();
            TestDFSShell.mkdir(dfs, root);
            dfs.copyFromLocalFile(false, false, new Path(localf.getPath()), remotef);
            String localfcontent = DFSTestUtil.readFile(localf);
            Assert.assertEquals(localfcontent, runner.run(0));
            Assert.assertEquals(localfcontent, runner.run(0, "-ignoreCrc"));
            // find block files to modify later
            List<FsDatasetTestUtils.MaterializedReplica> replicas = TestDFSShell.getMaterializedReplicas(cluster);
            // Shut down miniCluster and then corrupt the block files by overwriting a
            // portion with junk data.  We must shut down the miniCluster so that threads
            // in the data node do not hold locks on the block files while we try to
            // write into them.  Particularly on Windows, the data node's use of the
            // FileChannel.transferTo method can cause block files to be memory mapped
            // in read-only mode during the transfer to a client, and this causes a
            // locking conflict.  The call to shutdown the miniCluster blocks until all
            // DataXceiver threads exit, preventing this problem.
            dfs.close();
            cluster.shutdown();
            TestDFSShell.show(("replicas=" + replicas));
            TestDFSShell.corrupt(replicas, localfcontent);
            // Start the miniCluster again, but do not reformat, so prior files remain.
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).format(false).build();
            dfs = cluster.getFileSystem();
            Assert.assertEquals(null, runner.run(1));
            String corruptedcontent = runner.run(0, "-ignoreCrc");
            Assert.assertEquals(localfcontent.substring(1), corruptedcontent.substring(1));
            Assert.assertEquals(((localfcontent.charAt(0)) + 1), corruptedcontent.charAt(0));
        } finally {
            if (null != dfs) {
                try {
                    dfs.close();
                } catch (Exception e) {
                }
            }
            if (null != cluster) {
                cluster.shutdown();
            }
            localf.delete();
        }
    }

    /**
     * Test -stat [format] <path>... prints statistics about the file/directory
     * at <path> in the specified format.
     */
    @Test(timeout = 30000)
    public void testStat() throws Exception {
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Path testDir1 = new Path("testStat", "dir1");
        TestDFSShell.dfs.mkdirs(testDir1);
        final Path testFile2 = new Path(testDir1, "file2");
        DFSTestUtil.createFile(TestDFSShell.dfs, testFile2, (2 * (TestDFSShell.BLOCK_SIZE)), ((short) (3)), 0);
        final FileStatus status1 = TestDFSShell.dfs.getFileStatus(testDir1);
        final String mtime1 = fmt.format(new Date(status1.getModificationTime()));
        final String atime1 = fmt.format(new Date(status1.getAccessTime()));
        long now = Time.now();
        TestDFSShell.dfs.setTimes(testFile2, (now + 3000), (now + 6000));
        final FileStatus status2 = TestDFSShell.dfs.getFileStatus(testFile2);
        final String mtime2 = fmt.format(new Date(status2.getModificationTime()));
        final String atime2 = fmt.format(new Date(status2.getAccessTime()));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        TestDFSShell.doFsStat(TestDFSShell.dfs.getConf(), null);
        out.reset();
        TestDFSShell.doFsStat(TestDFSShell.dfs.getConf(), null, testDir1);
        Assert.assertEquals(("Unexpected -stat output: " + out), out.toString(), String.format("%s%n", mtime1));
        out.reset();
        TestDFSShell.doFsStat(TestDFSShell.dfs.getConf(), null, testDir1, testFile2);
        Assert.assertEquals(("Unexpected -stat output: " + out), out.toString(), String.format("%s%n%s%n", mtime1, mtime2));
        TestDFSShell.doFsStat(TestDFSShell.dfs.getConf(), "%F %u:%g %b %y %n");
        out.reset();
        TestDFSShell.doFsStat(TestDFSShell.dfs.getConf(), "%F %a %A %u:%g %b %y %n", testDir1);
        Assert.assertTrue(out.toString(), out.toString().contains(mtime1));
        Assert.assertTrue(out.toString(), out.toString().contains("directory"));
        Assert.assertTrue(out.toString(), out.toString().contains(status1.getGroup()));
        Assert.assertTrue(out.toString(), out.toString().contains(status1.getPermission().toString()));
        int n = status1.getPermission().toShort();
        int octal = (((((n >>> 9) & 1) * 1000) + (((n >>> 6) & 7) * 100)) + (((n >>> 3) & 7) * 10)) + (n & 7);
        Assert.assertTrue(out.toString(), out.toString().contains(String.valueOf(octal)));
        out.reset();
        TestDFSShell.doFsStat(TestDFSShell.dfs.getConf(), "%F %a %A %u:%g %b %x %y %n", testDir1, testFile2);
        n = status2.getPermission().toShort();
        octal = (((((n >>> 9) & 1) * 1000) + (((n >>> 6) & 7) * 100)) + (((n >>> 3) & 7) * 10)) + (n & 7);
        Assert.assertTrue(out.toString(), out.toString().contains(mtime1));
        Assert.assertTrue(out.toString(), out.toString().contains(atime1));
        Assert.assertTrue(out.toString(), out.toString().contains("regular file"));
        Assert.assertTrue(out.toString(), out.toString().contains(status2.getPermission().toString()));
        Assert.assertTrue(out.toString(), out.toString().contains(String.valueOf(octal)));
        Assert.assertTrue(out.toString(), out.toString().contains(mtime2));
        Assert.assertTrue(out.toString(), out.toString().contains(atime2));
    }

    @Test(timeout = 30000)
    public void testLsr() throws Exception {
        final Configuration conf = TestDFSShell.dfs.getConf();
        final String root = TestDFSShell.createTree(TestDFSShell.dfs, "lsr");
        TestDFSShell.dfs.mkdirs(new Path(root, "zzz"));
        TestDFSShell.runLsr(new FsShell(conf), root, 0);
        final Path sub = new Path(root, "sub");
        TestDFSShell.dfs.setPermission(sub, new FsPermission(((short) (0))));
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final String tmpusername = (ugi.getShortUserName()) + "1";
        UserGroupInformation tmpUGI = UserGroupInformation.createUserForTesting(tmpusername, new String[]{ tmpusername });
        String results = tmpUGI.doAs(new PrivilegedExceptionAction<String>() {
            @Override
            public String run() throws Exception {
                return TestDFSShell.runLsr(new FsShell(conf), root, 1);
            }
        });
        Assert.assertTrue(results.contains("zzz"));
    }

    /**
     * default setting is file:// which is not a DFS
     * so DFSAdmin should throw and catch InvalidArgumentException
     * and return -1 exit code.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testInvalidShell() throws Exception {
        Configuration conf = new Configuration();// default FS (non-DFS)

        DFSAdmin admin = new DFSAdmin();
        admin.setConf(conf);
        int res = admin.run(new String[]{ "-refreshNodes" });
        Assert.assertEquals("expected to fail -1", res, (-1));
    }

    // Preserve Copy Option is -ptopxa (timestamps, ownership, permission, XATTR,
    // ACLs)
    @Test(timeout = 120000)
    public void testCopyCommandsWithPreserveOption() throws Exception {
        FsShell shell = null;
        final String testdir = "/tmp/TestDFSShell-testCopyCommandsWithPreserveOption-" + (TestDFSShell.counter.getAndIncrement());
        final Path hdfsTestDir = new Path(testdir);
        try {
            TestDFSShell.dfs.mkdirs(hdfsTestDir);
            Path src = new Path(hdfsTestDir, "srcfile");
            TestDFSShell.dfs.create(src).close();
            TestDFSShell.dfs.setAcl(src, Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, "bar", READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, OTHER, EXECUTE)));
            FileStatus status = TestDFSShell.dfs.getFileStatus(src);
            final long mtime = status.getModificationTime();
            final long atime = status.getAccessTime();
            final String owner = status.getOwner();
            final String group = status.getGroup();
            final FsPermission perm = status.getPermission();
            TestDFSShell.dfs.setXAttr(src, TestDFSShell.USER_A1, TestDFSShell.USER_A1_VALUE);
            TestDFSShell.dfs.setXAttr(src, TestDFSShell.TRUSTED_A1, TestDFSShell.TRUSTED_A1_VALUE);
            shell = new FsShell(TestDFSShell.dfs.getConf());
            // -p
            Path target1 = new Path(hdfsTestDir, "targetfile1");
            String[] argv = new String[]{ "-cp", "-p", src.toUri().toString(), target1.toUri().toString() };
            int ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -p is not working", SUCCESS, ret);
            FileStatus targetStatus = TestDFSShell.dfs.getFileStatus(target1);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            FsPermission targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            Map<String, byte[]> xattrs = TestDFSShell.dfs.getXAttrs(target1);
            Assert.assertTrue(xattrs.isEmpty());
            List<AclEntry> acls = TestDFSShell.dfs.getAclStatus(target1).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptop
            Path target2 = new Path(hdfsTestDir, "targetfile2");
            argv = new String[]{ "-cp", "-ptop", src.toUri().toString(), target2.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptop is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(target2);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(target2);
            Assert.assertTrue(xattrs.isEmpty());
            acls = TestDFSShell.dfs.getAclStatus(target2).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptopx
            Path target3 = new Path(hdfsTestDir, "targetfile3");
            argv = new String[]{ "-cp", "-ptopx", src.toUri().toString(), target3.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptopx is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(target3);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(target3);
            Assert.assertEquals(xattrs.size(), 2);
            Assert.assertArrayEquals(TestDFSShell.USER_A1_VALUE, xattrs.get(TestDFSShell.USER_A1));
            Assert.assertArrayEquals(TestDFSShell.TRUSTED_A1_VALUE, xattrs.get(TestDFSShell.TRUSTED_A1));
            acls = TestDFSShell.dfs.getAclStatus(target3).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptopa
            Path target4 = new Path(hdfsTestDir, "targetfile4");
            argv = new String[]{ "-cp", "-ptopa", src.toUri().toString(), target4.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptopa is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(target4);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(target4);
            Assert.assertTrue(xattrs.isEmpty());
            acls = TestDFSShell.dfs.getAclStatus(target4).getEntries();
            Assert.assertFalse(acls.isEmpty());
            Assert.assertTrue(targetStatus.hasAcl());
            Assert.assertEquals(TestDFSShell.dfs.getAclStatus(src), TestDFSShell.dfs.getAclStatus(target4));
            // -ptoa (verify -pa option will preserve permissions also)
            Path target5 = new Path(hdfsTestDir, "targetfile5");
            argv = new String[]{ "-cp", "-ptoa", src.toUri().toString(), target5.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptoa is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(target5);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(target5);
            Assert.assertTrue(xattrs.isEmpty());
            acls = TestDFSShell.dfs.getAclStatus(target5).getEntries();
            Assert.assertFalse(acls.isEmpty());
            Assert.assertTrue(targetStatus.hasAcl());
            Assert.assertEquals(TestDFSShell.dfs.getAclStatus(src), TestDFSShell.dfs.getAclStatus(target5));
        } finally {
            if (null != shell) {
                shell.close();
            }
        }
    }

    @Test(timeout = 120000)
    public void testCopyCommandsWithRawXAttrs() throws Exception {
        FsShell shell = null;
        final String testdir = "/tmp/TestDFSShell-testCopyCommandsWithRawXAttrs-" + (TestDFSShell.counter.getAndIncrement());
        final Path hdfsTestDir = new Path(testdir);
        final Path rawHdfsTestDir = new Path(("/.reserved/raw" + testdir));
        try {
            TestDFSShell.dfs.mkdirs(hdfsTestDir);
            final Path src = new Path(hdfsTestDir, "srcfile");
            final String rawSrcBase = "/.reserved/raw" + testdir;
            final Path rawSrc = new Path(rawSrcBase, "srcfile");
            TestDFSShell.dfs.create(src).close();
            final Path srcDir = new Path(hdfsTestDir, "srcdir");
            final Path rawSrcDir = new Path(("/.reserved/raw" + testdir), "srcdir");
            TestDFSShell.dfs.mkdirs(srcDir);
            final Path srcDirFile = new Path(srcDir, "srcfile");
            final Path rawSrcDirFile = new Path(("/.reserved/raw" + srcDirFile));
            TestDFSShell.dfs.create(srcDirFile).close();
            final Path[] paths = new org.apache.hadoop.fs.Path[]{ rawSrc, rawSrcDir, rawSrcDirFile };
            final String[] xattrNames = new String[]{ TestDFSShell.USER_A1, TestDFSShell.RAW_A1 };
            final byte[][] xattrVals = new byte[][]{ TestDFSShell.USER_A1_VALUE, TestDFSShell.RAW_A1_VALUE };
            for (int i = 0; i < (paths.length); i++) {
                for (int j = 0; j < (xattrNames.length); j++) {
                    TestDFSShell.dfs.setXAttr(paths[i], xattrNames[j], xattrVals[j]);
                }
            }
            shell = new FsShell(TestDFSShell.dfs.getConf());
            /* Check that a file as the source path works ok. */
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, src, hdfsTestDir, false);
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, rawSrc, hdfsTestDir, false);
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, src, rawHdfsTestDir, false);
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, rawSrc, rawHdfsTestDir, true);
            /* Use a relative /.reserved/raw path. */
            final Path savedWd = TestDFSShell.dfs.getWorkingDirectory();
            try {
                TestDFSShell.dfs.setWorkingDirectory(new Path(rawSrcBase));
                final Path relRawSrc = new Path("../srcfile");
                final Path relRawHdfsTestDir = new Path("..");
                doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, relRawSrc, relRawHdfsTestDir, true);
            } finally {
                TestDFSShell.dfs.setWorkingDirectory(savedWd);
            }
            /* Check that a directory as the source path works ok. */
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, srcDir, hdfsTestDir, false);
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, rawSrcDir, hdfsTestDir, false);
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, srcDir, rawHdfsTestDir, false);
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, rawSrcDir, rawHdfsTestDir, true);
            /* Use relative in an absolute path. */
            final String relRawSrcDir = ("./.reserved/../.reserved/raw/../raw" + testdir) + "/srcdir";
            final String relRawDstDir = "./.reserved/../.reserved/raw/../raw" + testdir;
            doTestCopyCommandsWithRawXAttrs(shell, TestDFSShell.dfs, new Path(relRawSrcDir), new Path(relRawDstDir), true);
        } finally {
            if (null != shell) {
                shell.close();
            }
            TestDFSShell.dfs.delete(hdfsTestDir, true);
        }
    }

    // verify cp -ptopxa option will preserve directory attributes.
    @Test(timeout = 120000)
    public void testCopyCommandsToDirectoryWithPreserveOption() throws Exception {
        FsShell shell = null;
        final String testdir = "/tmp/TestDFSShell-testCopyCommandsToDirectoryWithPreserveOption-" + (TestDFSShell.counter.getAndIncrement());
        final Path hdfsTestDir = new Path(testdir);
        try {
            TestDFSShell.dfs.mkdirs(hdfsTestDir);
            Path srcDir = new Path(hdfsTestDir, "srcDir");
            TestDFSShell.dfs.mkdirs(srcDir);
            TestDFSShell.dfs.setAcl(srcDir, Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.DEFAULT, GROUP, "bar", READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, OTHER, EXECUTE)));
            // set sticky bit
            TestDFSShell.dfs.setPermission(srcDir, new FsPermission(ALL, READ_EXECUTE, EXECUTE, true));
            // Create a file in srcDir to check if modification time of
            // srcDir to be preserved after copying the file.
            // If cp -p command is to preserve modification time and then copy child
            // (srcFile), modification time will not be preserved.
            Path srcFile = new Path(srcDir, "srcFile");
            TestDFSShell.dfs.create(srcFile).close();
            FileStatus status = TestDFSShell.dfs.getFileStatus(srcDir);
            final long mtime = status.getModificationTime();
            final long atime = status.getAccessTime();
            final String owner = status.getOwner();
            final String group = status.getGroup();
            final FsPermission perm = status.getPermission();
            TestDFSShell.dfs.setXAttr(srcDir, TestDFSShell.USER_A1, TestDFSShell.USER_A1_VALUE);
            TestDFSShell.dfs.setXAttr(srcDir, TestDFSShell.TRUSTED_A1, TestDFSShell.TRUSTED_A1_VALUE);
            shell = new FsShell(TestDFSShell.dfs.getConf());
            // -p
            Path targetDir1 = new Path(hdfsTestDir, "targetDir1");
            String[] argv = new String[]{ "-cp", "-p", srcDir.toUri().toString(), targetDir1.toUri().toString() };
            int ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -p is not working", SUCCESS, ret);
            FileStatus targetStatus = TestDFSShell.dfs.getFileStatus(targetDir1);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            FsPermission targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            Map<String, byte[]> xattrs = TestDFSShell.dfs.getXAttrs(targetDir1);
            Assert.assertTrue(xattrs.isEmpty());
            List<AclEntry> acls = TestDFSShell.dfs.getAclStatus(targetDir1).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptop
            Path targetDir2 = new Path(hdfsTestDir, "targetDir2");
            argv = new String[]{ "-cp", "-ptop", srcDir.toUri().toString(), targetDir2.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptop is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(targetDir2);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(targetDir2);
            Assert.assertTrue(xattrs.isEmpty());
            acls = TestDFSShell.dfs.getAclStatus(targetDir2).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptopx
            Path targetDir3 = new Path(hdfsTestDir, "targetDir3");
            argv = new String[]{ "-cp", "-ptopx", srcDir.toUri().toString(), targetDir3.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptopx is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(targetDir3);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(targetDir3);
            Assert.assertEquals(xattrs.size(), 2);
            Assert.assertArrayEquals(TestDFSShell.USER_A1_VALUE, xattrs.get(TestDFSShell.USER_A1));
            Assert.assertArrayEquals(TestDFSShell.TRUSTED_A1_VALUE, xattrs.get(TestDFSShell.TRUSTED_A1));
            acls = TestDFSShell.dfs.getAclStatus(targetDir3).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptopa
            Path targetDir4 = new Path(hdfsTestDir, "targetDir4");
            argv = new String[]{ "-cp", "-ptopa", srcDir.toUri().toString(), targetDir4.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptopa is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(targetDir4);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(targetDir4);
            Assert.assertTrue(xattrs.isEmpty());
            acls = TestDFSShell.dfs.getAclStatus(targetDir4).getEntries();
            Assert.assertFalse(acls.isEmpty());
            Assert.assertTrue(targetStatus.hasAcl());
            Assert.assertEquals(TestDFSShell.dfs.getAclStatus(srcDir), TestDFSShell.dfs.getAclStatus(targetDir4));
            // -ptoa (verify -pa option will preserve permissions also)
            Path targetDir5 = new Path(hdfsTestDir, "targetDir5");
            argv = new String[]{ "-cp", "-ptoa", srcDir.toUri().toString(), targetDir5.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptoa is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(targetDir5);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            xattrs = TestDFSShell.dfs.getXAttrs(targetDir5);
            Assert.assertTrue(xattrs.isEmpty());
            acls = TestDFSShell.dfs.getAclStatus(targetDir5).getEntries();
            Assert.assertFalse(acls.isEmpty());
            Assert.assertTrue(targetStatus.hasAcl());
            Assert.assertEquals(TestDFSShell.dfs.getAclStatus(srcDir), TestDFSShell.dfs.getAclStatus(targetDir5));
        } finally {
            if (shell != null) {
                shell.close();
            }
        }
    }

    // Verify cp -pa option will preserve both ACL and sticky bit.
    @Test(timeout = 120000)
    public void testCopyCommandsPreserveAclAndStickyBit() throws Exception {
        FsShell shell = null;
        final String testdir = "/tmp/TestDFSShell-testCopyCommandsPreserveAclAndStickyBit-" + (TestDFSShell.counter.getAndIncrement());
        final Path hdfsTestDir = new Path(testdir);
        try {
            TestDFSShell.dfs.mkdirs(hdfsTestDir);
            Path src = new Path(hdfsTestDir, "srcfile");
            TestDFSShell.dfs.create(src).close();
            TestDFSShell.dfs.setAcl(src, Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, GROUP, "bar", READ_EXECUTE), AclTestHelpers.aclEntry(AclEntryScope.ACCESS, OTHER, EXECUTE)));
            // set sticky bit
            TestDFSShell.dfs.setPermission(src, new FsPermission(ALL, READ_EXECUTE, EXECUTE, true));
            FileStatus status = TestDFSShell.dfs.getFileStatus(src);
            final long mtime = status.getModificationTime();
            final long atime = status.getAccessTime();
            final String owner = status.getOwner();
            final String group = status.getGroup();
            final FsPermission perm = status.getPermission();
            shell = new FsShell(TestDFSShell.dfs.getConf());
            // -p preserves sticky bit and doesn't preserve ACL
            Path target1 = new Path(hdfsTestDir, "targetfile1");
            String[] argv = new String[]{ "-cp", "-p", src.toUri().toString(), target1.toUri().toString() };
            int ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp is not working", SUCCESS, ret);
            FileStatus targetStatus = TestDFSShell.dfs.getFileStatus(target1);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            FsPermission targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            List<AclEntry> acls = TestDFSShell.dfs.getAclStatus(target1).getEntries();
            Assert.assertTrue(acls.isEmpty());
            Assert.assertFalse(targetStatus.hasAcl());
            // -ptopa preserves both sticky bit and ACL
            Path target2 = new Path(hdfsTestDir, "targetfile2");
            argv = new String[]{ "-cp", "-ptopa", src.toUri().toString(), target2.toUri().toString() };
            ret = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -ptopa is not working", SUCCESS, ret);
            targetStatus = TestDFSShell.dfs.getFileStatus(target2);
            Assert.assertEquals(mtime, targetStatus.getModificationTime());
            Assert.assertEquals(atime, targetStatus.getAccessTime());
            Assert.assertEquals(owner, targetStatus.getOwner());
            Assert.assertEquals(group, targetStatus.getGroup());
            targetPerm = targetStatus.getPermission();
            Assert.assertTrue(perm.equals(targetPerm));
            acls = TestDFSShell.dfs.getAclStatus(target2).getEntries();
            Assert.assertFalse(acls.isEmpty());
            Assert.assertTrue(targetStatus.hasAcl());
            Assert.assertEquals(TestDFSShell.dfs.getAclStatus(src), TestDFSShell.dfs.getAclStatus(target2));
        } finally {
            if (null != shell) {
                shell.close();
            }
        }
    }

    // force Copy Option is -f
    @Test(timeout = 30000)
    public void testCopyCommandsWithForceOption() throws Exception {
        FsShell shell = null;
        final File localFile = new File(TestDFSShell.TEST_ROOT_DIR, "testFileForPut");
        final String localfilepath = new Path(localFile.getAbsolutePath()).toUri().toString();
        final String testdir = "/tmp/TestDFSShell-testCopyCommandsWithForceOption-" + (TestDFSShell.counter.getAndIncrement());
        final Path hdfsTestDir = new Path(testdir);
        try {
            TestDFSShell.dfs.mkdirs(hdfsTestDir);
            localFile.createNewFile();
            TestDFSShell.writeFile(TestDFSShell.dfs, new Path(testdir, "testFileForPut"));
            shell = new FsShell();
            // Tests for put
            String[] argv = new String[]{ "-put", "-f", localfilepath, testdir };
            int res = ToolRunner.run(shell, argv);
            Assert.assertEquals("put -f is not working", SUCCESS, res);
            argv = new String[]{ "-put", localfilepath, testdir };
            res = ToolRunner.run(shell, argv);
            Assert.assertEquals("put command itself is able to overwrite the file", ERROR, res);
            // Tests for copyFromLocal
            argv = new String[]{ "-copyFromLocal", "-f", localfilepath, testdir };
            res = ToolRunner.run(shell, argv);
            Assert.assertEquals("copyFromLocal -f is not working", SUCCESS, res);
            argv = new String[]{ "-copyFromLocal", localfilepath, testdir };
            res = ToolRunner.run(shell, argv);
            Assert.assertEquals("copyFromLocal command itself is able to overwrite the file", ERROR, res);
            // Tests for cp
            argv = new String[]{ "-cp", "-f", localfilepath, testdir };
            res = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp -f is not working", SUCCESS, res);
            argv = new String[]{ "-cp", localfilepath, testdir };
            res = ToolRunner.run(shell, argv);
            Assert.assertEquals("cp command itself is able to overwrite the file", ERROR, res);
        } finally {
            if (null != shell)
                shell.close();

            if (localFile.exists())
                localFile.delete();

        }
    }

    /* [refs HDFS-5033]

    return a "Permission Denied" message instead of "No such file or Directory"
    when trying to put/copyFromLocal a file that doesn't have read access
     */
    @Test(timeout = 30000)
    public void testCopyFromLocalWithPermissionDenied() throws Exception {
        FsShell shell = null;
        PrintStream bak = null;
        final File localFile = new File(TestDFSShell.TEST_ROOT_DIR, "testFileWithNoReadPermissions");
        final String localfilepath = new Path(localFile.getAbsolutePath()).toUri().toString();
        final String testdir = "/tmp/TestDFSShell-CopyFromLocalWithPermissionDenied-" + (TestDFSShell.counter.getAndIncrement());
        final Path hdfsTestDir = new Path(testdir);
        try {
            TestDFSShell.dfs.mkdirs(hdfsTestDir);
            localFile.createNewFile();
            localFile.setReadable(false);
            TestDFSShell.writeFile(TestDFSShell.dfs, new Path(testdir, "testFileForPut"));
            shell = new FsShell();
            // capture system error messages, snarfed from testErrOutPut()
            bak = System.err;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream tmp = new PrintStream(out);
            System.setErr(tmp);
            // Tests for put
            String[] argv = new String[]{ "-put", localfilepath, testdir };
            int res = ToolRunner.run(shell, argv);
            Assert.assertEquals("put is working", ERROR, res);
            String returned = out.toString();
            Assert.assertTrue(" outputs Permission denied error message", ((returned.lastIndexOf("Permission denied")) != (-1)));
            // Tests for copyFromLocal
            argv = new String[]{ "-copyFromLocal", localfilepath, testdir };
            res = ToolRunner.run(shell, argv);
            Assert.assertEquals("copyFromLocal -f is working", ERROR, res);
            returned = out.toString();
            Assert.assertTrue(" outputs Permission denied error message", ((returned.lastIndexOf("Permission denied")) != (-1)));
        } finally {
            if (bak != null) {
                System.setErr(bak);
            }
            if (null != shell)
                shell.close();

            if (localFile.exists())
                localFile.delete();

            TestDFSShell.dfs.delete(hdfsTestDir, true);
        }
    }

    /**
     * Test -setrep with a replication factor that is too low.  We have to test
     * this here because the mini-miniCluster used with testHDFSConf.xml uses a
     * replication factor of 1 (for good reason).
     */
    @Test(timeout = 30000)
    public void testSetrepLow() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(DFS_NAMENODE_REPLICATION_MIN_KEY, 2);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf);
        MiniDFSCluster cluster = builder.numDataNodes(2).format(true).build();
        FsShell shell = new FsShell(conf);
        cluster.waitActive();
        final String testdir = "/tmp/TestDFSShell-testSetrepLow";
        final Path hdfsFile = new Path(testdir, "testFileForSetrepLow");
        final PrintStream origOut = System.out;
        final PrintStream origErr = System.err;
        try {
            final FileSystem fs = cluster.getFileSystem();
            Assert.assertTrue("Unable to create test directory", fs.mkdirs(new Path(testdir)));
            fs.create(hdfsFile, true).close();
            // Capture the command output so we can examine it
            final ByteArrayOutputStream bao = new ByteArrayOutputStream();
            final PrintStream capture = new PrintStream(bao);
            System.setOut(capture);
            System.setErr(capture);
            final String[] argv = new String[]{ "-setrep", "1", hdfsFile.toString() };
            try {
                Assert.assertEquals("Command did not return the expected exit code", 1, shell.run(argv));
            } finally {
                System.setOut(origOut);
                System.setErr(origErr);
            }
            Assert.assertTrue(("Error message is not the expected error message" + (bao.toString())), bao.toString().startsWith(("setrep: Requested replication factor of 1 is less than " + ("the required minimum of 2 for /tmp/TestDFSShell-" + "testSetrepLow/testFileForSetrepLow"))));
        } finally {
            shell.close();
            cluster.shutdown();
        }
    }

    // setrep for file and directory.
    @Test(timeout = 30000)
    public void testSetrep() throws Exception {
        FsShell shell = null;
        final String testdir1 = "/tmp/TestDFSShell-testSetrep-" + (TestDFSShell.counter.getAndIncrement());
        final String testdir2 = testdir1 + "/nestedDir";
        final Path hdfsFile1 = new Path(testdir1, "testFileForSetrep");
        final Path hdfsFile2 = new Path(testdir2, "testFileForSetrep");
        final Short oldRepFactor = new Short(((short) (2)));
        final Short newRepFactor = new Short(((short) (3)));
        try {
            String[] argv;
            Assert.assertThat(TestDFSShell.dfs.mkdirs(new Path(testdir2)), CoreMatchers.is(true));
            shell = new FsShell(TestDFSShell.dfs.getConf());
            TestDFSShell.dfs.create(hdfsFile1, true).close();
            TestDFSShell.dfs.create(hdfsFile2, true).close();
            // Tests for setrep on a file.
            argv = new String[]{ "-setrep", newRepFactor.toString(), hdfsFile1.toString() };
            Assert.assertThat(shell.run(argv), CoreMatchers.is(SUCCESS));
            Assert.assertThat(TestDFSShell.dfs.getFileStatus(hdfsFile1).getReplication(), CoreMatchers.is(newRepFactor));
            Assert.assertThat(TestDFSShell.dfs.getFileStatus(hdfsFile2).getReplication(), CoreMatchers.is(oldRepFactor));
            // Tests for setrep
            // Tests for setrep on a directory and make sure it is applied recursively.
            argv = new String[]{ "-setrep", newRepFactor.toString(), testdir1 };
            Assert.assertThat(shell.run(argv), CoreMatchers.is(SUCCESS));
            Assert.assertThat(TestDFSShell.dfs.getFileStatus(hdfsFile1).getReplication(), CoreMatchers.is(newRepFactor));
            Assert.assertThat(TestDFSShell.dfs.getFileStatus(hdfsFile2).getReplication(), CoreMatchers.is(newRepFactor));
        } finally {
            if (shell != null) {
                shell.close();
            }
        }
    }

    @Test(timeout = 300000)
    public void testAppendToFile() throws Exception {
        final int inputFileLength = 1024 * 1024;
        File testRoot = new File(TestDFSShell.TEST_ROOT_DIR, "testAppendtoFileDir");
        testRoot.mkdirs();
        File file1 = new File(testRoot, "file1");
        File file2 = new File(testRoot, "file2");
        TestDFSShell.createLocalFileWithRandomData(inputFileLength, file1);
        TestDFSShell.createLocalFileWithRandomData(inputFileLength, file2);
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        cluster.waitActive();
        try {
            FileSystem dfs = cluster.getFileSystem();
            Assert.assertTrue(("Not a HDFS: " + (getUri())), (dfs instanceof DistributedFileSystem));
            // Run appendToFile once, make sure that the target file is
            // created and is of the right size.
            Path remoteFile = new Path("/remoteFile");
            FsShell shell = new FsShell();
            shell.setConf(conf);
            String[] argv = new String[]{ "-appendToFile", file1.toString(), file2.toString(), remoteFile.toString() };
            int res = ToolRunner.run(shell, argv);
            Assert.assertThat(res, CoreMatchers.is(0));
            Assert.assertThat(dfs.getFileStatus(remoteFile).getLen(), CoreMatchers.is((((long) (inputFileLength)) * 2)));
            // Run the command once again and make sure that the target file
            // size has been doubled.
            res = ToolRunner.run(shell, argv);
            Assert.assertThat(res, CoreMatchers.is(0));
            Assert.assertThat(dfs.getFileStatus(remoteFile).getLen(), CoreMatchers.is((((long) (inputFileLength)) * 4)));
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testAppendToFileBadArgs() throws Exception {
        final int inputFileLength = 1024 * 1024;
        File testRoot = new File(TestDFSShell.TEST_ROOT_DIR, "testAppendToFileBadArgsDir");
        testRoot.mkdirs();
        File file1 = new File(testRoot, "file1");
        TestDFSShell.createLocalFileWithRandomData(inputFileLength, file1);
        // Run appendToFile with insufficient arguments.
        FsShell shell = new FsShell();
        shell.setConf(TestDFSShell.dfs.getConf());
        String[] argv = new String[]{ "-appendToFile", file1.toString() };
        int res = ToolRunner.run(shell, argv);
        Assert.assertThat(res, CoreMatchers.not(0));
        // Mix stdin with other input files. Must fail.
        Path remoteFile = new Path("/remoteFile");
        argv = new String[]{ "-appendToFile", file1.toString(), "-", remoteFile.toString() };
        res = ToolRunner.run(shell, argv);
        Assert.assertThat(res, CoreMatchers.not(0));
    }

    @Test(timeout = 30000)
    public void testSetXAttrPermission() throws Exception {
        UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        PrintStream bak = null;
        try {
            Path p = new Path("/foo");
            TestDFSShell.dfs.mkdirs(p);
            bak = System.err;
            final FsShell fshell = new FsShell(TestDFSShell.dfs.getConf());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setErr(new PrintStream(out));
            // No permission to write xattr
            TestDFSShell.dfs.setPermission(p, new FsPermission(((short) (448))));
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "user.a1", "-v", "1234", "/foo" });
                    Assert.assertEquals("Returned should be 1", 1, ret);
                    String str = out.toString();
                    Assert.assertTrue("Permission denied printed", ((str.indexOf("Permission denied")) != (-1)));
                    out.reset();
                    return null;
                }
            });
            int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "user.a1", "-v", "1234", "/foo" });
            Assert.assertEquals("Returned should be 0", 0, ret);
            out.reset();
            // No permission to read and remove
            TestDFSShell.dfs.setPermission(p, new FsPermission(((short) (488))));
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // Read
                    int ret = ToolRunner.run(fshell, new String[]{ "-getfattr", "-n", "user.a1", "/foo" });
                    Assert.assertEquals("Returned should be 1", 1, ret);
                    String str = out.toString();
                    Assert.assertTrue("Permission denied printed", ((str.indexOf("Permission denied")) != (-1)));
                    out.reset();
                    // Remove
                    ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-x", "user.a1", "/foo" });
                    Assert.assertEquals("Returned should be 1", 1, ret);
                    str = out.toString();
                    Assert.assertTrue("Permission denied printed", ((str.indexOf("Permission denied")) != (-1)));
                    out.reset();
                    return null;
                }
            });
        } finally {
            if (bak != null) {
                System.setErr(bak);
            }
        }
    }

    /* HDFS-6413 xattr names erroneously handled as case-insensitive */
    @Test(timeout = 30000)
    public void testSetXAttrCaseSensitivity() throws Exception {
        PrintStream bak = null;
        try {
            Path p = new Path("/mydir");
            TestDFSShell.dfs.mkdirs(p);
            bak = System.err;
            final FsShell fshell = new FsShell(TestDFSShell.dfs.getConf());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-n", "User.Foo", "/mydir" }, new String[]{ "-getfattr", "-d", "/mydir" }, new String[]{ "user.Foo" }, new String[]{  });
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-n", "user.FOO", "/mydir" }, new String[]{ "-getfattr", "-d", "/mydir" }, new String[]{ "user.Foo", "user.FOO" }, new String[]{  });
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-n", "USER.foo", "/mydir" }, new String[]{ "-getfattr", "-d", "/mydir" }, new String[]{ "user.Foo", "user.FOO", "user.foo" }, new String[]{  });
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-n", "USER.fOo", "-v", "myval", "/mydir" }, new String[]{ "-getfattr", "-d", "/mydir" }, new String[]{ "user.Foo", "user.FOO", "user.foo", "user.fOo=\"myval\"" }, new String[]{ "user.Foo=", "user.FOO=", "user.foo=" });
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-x", "useR.foo", "/mydir" }, new String[]{ "-getfattr", "-d", "/mydir" }, new String[]{ "user.Foo", "user.FOO" }, new String[]{ "foo" });
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-x", "USER.FOO", "/mydir" }, new String[]{ "-getfattr", "-d", "/mydir" }, new String[]{ "user.Foo" }, new String[]{ "FOO" });
            doSetXattr(out, fshell, new String[]{ "-setfattr", "-x", "useR.Foo", "/mydir" }, new String[]{ "-getfattr", "-n", "User.Foo", "/mydir" }, new String[]{  }, new String[]{ "Foo" });
        } finally {
            if (bak != null) {
                System.setOut(bak);
            }
        }
    }

    /**
     * Test to make sure that user namespace xattrs can be set only if path has
     * access and for sticky directorries, only owner/privileged user can write.
     * Trusted namespace xattrs can be set only with privileged users.
     *
     * As user1: Create a directory (/foo) as user1, chown it to user1 (and
     * user1's group), grant rwx to "other".
     *
     * As user2: Set an xattr (should pass with path access).
     *
     * As user1: Set an xattr (should pass).
     *
     * As user2: Read the xattr (should pass). Remove the xattr (should pass with
     * path access).
     *
     * As user1: Read the xattr (should pass). Remove the xattr (should pass).
     *
     * As user1: Change permissions only to owner
     *
     * As User2: Set an Xattr (Should fail set with no path access) Remove an
     * Xattr (Should fail with no path access)
     *
     * As SuperUser: Set an Xattr with Trusted (Should pass)
     */
    @Test(timeout = 30000)
    public void testSetXAttrPermissionAsDifferentOwner() throws Exception {
        final String root = "/testSetXAttrPermissionAsDifferentOwner";
        final String USER1 = "user1";
        final String GROUP1 = "supergroup";
        final UserGroupInformation user1 = UserGroupInformation.createUserForTesting(USER1, new String[]{ GROUP1 });
        final UserGroupInformation user2 = UserGroupInformation.createUserForTesting("user2", new String[]{ "mygroup2" });
        final UserGroupInformation SUPERUSER = UserGroupInformation.getCurrentUser();
        PrintStream bak = null;
        try {
            TestDFSShell.dfs.mkdirs(new Path(root));
            TestDFSShell.dfs.setOwner(new Path(root), USER1, GROUP1);
            bak = System.err;
            final FsShell fshell = new FsShell(TestDFSShell.dfs.getConf());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setErr(new PrintStream(out));
            // Test 1.  Let user1 be owner for /foo
            user1.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final int ret = ToolRunner.run(fshell, new String[]{ "-mkdir", root + "/foo" });
                    Assert.assertEquals("Return should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
            // Test 2. Give access to others
            user1.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // Give access to "other"
                    final int ret = ToolRunner.run(fshell, new String[]{ "-chmod", "707", root + "/foo" });
                    Assert.assertEquals("Return should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
            // Test 3. Should be allowed to write xattr if there is a path access to
            // user (user2).
            user2.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "user.a1", "-v", "1234", root + "/foo" });
                    Assert.assertEquals("Returned should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
            // Test 4. There should be permission to write xattr for
            // the owning user with write permissions.
            user1.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "user.a1", "-v", "1234", root + "/foo" });
                    Assert.assertEquals("Returned should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
            // Test 5. There should be permission to read non-owning user (user2) if
            // there is path access to that user and also can remove.
            user2.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // Read
                    int ret = ToolRunner.run(fshell, new String[]{ "-getfattr", "-n", "user.a1", root + "/foo" });
                    Assert.assertEquals("Returned should be 0", 0, ret);
                    out.reset();
                    // Remove
                    ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-x", "user.a1", root + "/foo" });
                    Assert.assertEquals("Returned should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
            // Test 6. There should be permission to read/remove for
            // the owning user with path access.
            user1.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return null;
                }
            });
            // Test 7. Change permission to have path access only to owner(user1)
            user1.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // Give access to "other"
                    final int ret = ToolRunner.run(fshell, new String[]{ "-chmod", "700", root + "/foo" });
                    Assert.assertEquals("Return should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
            // Test 8. There should be no permissions to set for
            // the non-owning user with no path access.
            user2.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // set
                    int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "user.a2", root + "/foo" });
                    Assert.assertEquals("Returned should be 1", 1, ret);
                    final String str = out.toString();
                    Assert.assertTrue("Permission denied printed", ((str.indexOf("Permission denied")) != (-1)));
                    out.reset();
                    return null;
                }
            });
            // Test 9. There should be no permissions to remove for
            // the non-owning user with no path access.
            user2.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // set
                    int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-x", "user.a2", root + "/foo" });
                    Assert.assertEquals("Returned should be 1", 1, ret);
                    final String str = out.toString();
                    Assert.assertTrue("Permission denied printed", ((str.indexOf("Permission denied")) != (-1)));
                    out.reset();
                    return null;
                }
            });
            // Test 10. Superuser should be allowed to set with trusted namespace
            SUPERUSER.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    // set
                    int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "trusted.a3", root + "/foo" });
                    Assert.assertEquals("Returned should be 0", 0, ret);
                    out.reset();
                    return null;
                }
            });
        } finally {
            if (bak != null) {
                System.setErr(bak);
            }
        }
    }

    /* 1. Test that CLI throws an exception and returns non-0 when user does
    not have permission to read an xattr.
    2. Test that CLI throws an exception and returns non-0 when a non-existent
    xattr is requested.
     */
    @Test(timeout = 120000)
    public void testGetFAttrErrors() throws Exception {
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        PrintStream bakErr = null;
        try {
            final Path p = new Path("/testGetFAttrErrors");
            TestDFSShell.dfs.mkdirs(p);
            bakErr = System.err;
            final FsShell fshell = new FsShell(TestDFSShell.dfs.getConf());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setErr(new PrintStream(out));
            // No permission for "other".
            TestDFSShell.dfs.setPermission(p, new FsPermission(((short) (448))));
            {
                final int ret = ToolRunner.run(fshell, new String[]{ "-setfattr", "-n", "user.a1", "-v", "1234", p.toString() });
                Assert.assertEquals("Returned should be 0", 0, ret);
                out.reset();
            }
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    int ret = ToolRunner.run(fshell, new String[]{ "-getfattr", "-n", "user.a1", p.toString() });
                    String str = out.toString();
                    Assert.assertTrue("xattr value was incorrectly returned", ((str.indexOf("1234")) == (-1)));
                    out.reset();
                    return null;
                }
            });
            {
                final int ret = ToolRunner.run(fshell, new String[]{ "-getfattr", "-n", "user.nonexistent", p.toString() });
                String str = out.toString();
                Assert.assertTrue("xattr value was incorrectly returned", ((str.indexOf("getfattr: At least one of the attributes provided was not found")) >= 0));
                out.reset();
            }
        } finally {
            if (bakErr != null) {
                System.setErr(bakErr);
            }
        }
    }

    /**
     * Test that the server trash configuration is respected when
     * the client configuration is not set.
     */
    @Test(timeout = 30000)
    public void testServerConfigRespected() throws Exception {
        deleteFileUsingTrash(true, false);
    }

    /**
     * Test that server trash configuration is respected even when the
     * client configuration is set.
     */
    @Test(timeout = 30000)
    public void testServerConfigRespectedWithClient() throws Exception {
        deleteFileUsingTrash(true, true);
    }

    /**
     * Test that the client trash configuration is respected when
     * the server configuration is not set.
     */
    @Test(timeout = 30000)
    public void testClientConfigRespected() throws Exception {
        deleteFileUsingTrash(false, true);
    }

    /**
     * Test that trash is disabled by default.
     */
    @Test(timeout = 30000)
    public void testNoTrashConfig() throws Exception {
        deleteFileUsingTrash(false, false);
    }

    @Test(timeout = 30000)
    public void testListReserved() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        FileSystem fs = cluster.getFileSystem();
        FsShell shell = new FsShell();
        shell.setConf(conf);
        FileStatus test = fs.getFileStatus(new Path("/.reserved"));
        Assert.assertEquals(DOT_RESERVED_STRING, test.getPath().getName());
        // Listing /.reserved/ should show 2 items: raw and .inodes
        FileStatus[] stats = fs.listStatus(new Path("/.reserved"));
        Assert.assertEquals(2, stats.length);
        Assert.assertEquals(DOT_INODES_STRING, stats[0].getPath().getName());
        Assert.assertEquals(conf.get(DFS_PERMISSIONS_SUPERUSERGROUP_KEY), stats[0].getGroup());
        Assert.assertEquals("raw", stats[1].getPath().getName());
        Assert.assertEquals(conf.get(DFS_PERMISSIONS_SUPERUSERGROUP_KEY), stats[1].getGroup());
        // Listing / should not show /.reserved
        stats = fs.listStatus(new Path("/"));
        Assert.assertEquals(0, stats.length);
        // runCmd prints error into System.err, thus verify from there.
        PrintStream syserr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);
        try {
            TestDFSShell.runCmd(shell, "-ls", "/.reserved");
            Assert.assertEquals(0, baos.toString().length());
            TestDFSShell.runCmd(shell, "-ls", "/.reserved/raw/.reserved");
            Assert.assertTrue(baos.toString().contains("No such file or directory"));
        } finally {
            System.setErr(syserr);
            cluster.shutdown();
        }
    }

    @Test(timeout = 30000)
    public void testMkdirReserved() throws IOException {
        try {
            TestDFSShell.dfs.mkdirs(new Path("/.reserved"));
            Assert.fail("Can't mkdir /.reserved");
        } catch (Exception e) {
            // Expected, HadoopIllegalArgumentException thrown from remote
            Assert.assertTrue(e.getMessage().contains("\".reserved\" is reserved"));
        }
    }

    @Test(timeout = 30000)
    public void testRmReserved() throws IOException {
        try {
            TestDFSShell.dfs.delete(new Path("/.reserved"), true);
            Assert.fail("Can't delete /.reserved");
        } catch (Exception e) {
            // Expected, InvalidPathException thrown from remote
            Assert.assertTrue(e.getMessage().contains("Invalid path name /.reserved"));
        }
    }

    // (timeout = 30000)
    @Test
    public void testCopyReserved() throws IOException {
        final File localFile = new File(TestDFSShell.TEST_ROOT_DIR, "testFileForPut");
        localFile.createNewFile();
        final String localfilepath = new Path(localFile.getAbsolutePath()).toUri().toString();
        try {
            TestDFSShell.dfs.copyFromLocalFile(new Path(localfilepath), new Path("/.reserved"));
            Assert.fail("Can't copyFromLocal to /.reserved");
        } catch (Exception e) {
            // Expected, InvalidPathException thrown from remote
            Assert.assertTrue(e.getMessage().contains("Invalid path name /.reserved"));
        }
        final String testdir = GenericTestUtils.getTempPath("TestDFSShell-testCopyReserved");
        final Path hdfsTestDir = new Path(testdir);
        TestDFSShell.writeFile(TestDFSShell.dfs, new Path(testdir, "testFileForPut"));
        final Path src = new Path(hdfsTestDir, "srcfile");
        TestDFSShell.dfs.create(src).close();
        Assert.assertTrue(TestDFSShell.dfs.exists(src));
        // runCmd prints error into System.err, thus verify from there.
        PrintStream syserr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);
        try {
            FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
            TestDFSShell.runCmd(shell, "-cp", src.toString(), "/.reserved");
            Assert.assertTrue(baos.toString().contains("Invalid path name /.reserved"));
        } finally {
            System.setErr(syserr);
        }
    }

    @Test(timeout = 30000)
    public void testChmodReserved() throws IOException {
        // runCmd prints error into System.err, thus verify from there.
        PrintStream syserr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);
        try {
            FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
            TestDFSShell.runCmd(shell, "-chmod", "777", "/.reserved");
            Assert.assertTrue(baos.toString().contains("Invalid path name /.reserved"));
        } finally {
            System.setErr(syserr);
        }
    }

    @Test(timeout = 30000)
    public void testChownReserved() throws IOException {
        // runCmd prints error into System.err, thus verify from there.
        PrintStream syserr = System.err;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setErr(ps);
        try {
            FsShell shell = new FsShell(TestDFSShell.dfs.getConf());
            TestDFSShell.runCmd(shell, "-chown", "user1", "/.reserved");
            Assert.assertTrue(baos.toString().contains("Invalid path name /.reserved"));
        } finally {
            System.setErr(syserr);
        }
    }

    @Test(timeout = 30000)
    public void testSymLinkReserved() throws IOException {
        try {
            TestDFSShell.dfs.createSymlink(new Path("/.reserved"), new Path("/rl1"), false);
            Assert.fail("Can't create symlink to /.reserved");
        } catch (Exception e) {
            // Expected, InvalidPathException thrown from remote
            Assert.assertTrue(e.getMessage().contains("Invalid target name: /.reserved"));
        }
    }

    @Test(timeout = 30000)
    public void testSnapshotReserved() throws IOException {
        final Path reserved = new Path("/.reserved");
        try {
            TestDFSShell.dfs.allowSnapshot(reserved);
            Assert.fail("Can't allow snapshot on /.reserved");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("Directory does not exist"));
        }
        try {
            TestDFSShell.dfs.createSnapshot(reserved, "snap");
            Assert.fail("Can't create snapshot on /.reserved");
        } catch (FileNotFoundException e) {
            Assert.assertTrue(e.getMessage().contains("Directory/File does not exist"));
        }
    }
}

