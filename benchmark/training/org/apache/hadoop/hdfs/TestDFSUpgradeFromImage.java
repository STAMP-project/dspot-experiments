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


import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import Event.CreateEvent;
import Event.EventType;
import Event.RenameEvent;
import FSImage.LOG;
import GenericTestUtils.DEFAULT_TEST_DATA_DIR;
import GenericTestUtils.SYSPROP_TEST_DATA_DIR;
import StartupOption.REGULAR;
import StartupOption.UPGRADE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.inotify.Event;
import org.apache.hadoop.hdfs.inotify.EventBatch;
import org.apache.hadoop.hdfs.server.namenode.FSImageFormat;
import org.apache.hadoop.hdfs.server.namenode.FSImageTestUtil;
import org.apache.hadoop.hdfs.server.namenode.IllegalReservedPathException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getRootLogger;


/**
 * This tests data transfer protocol handling in the Datanode. It sends
 * various forms of wrong data and verifies that Datanode handles it well.
 *
 * This test uses the following items from src/test/.../dfs directory :
 *   1) hadoop-22-dfs-dir.tgz and other tarred pre-upgrade NN / DN
 *      directory images
 *   2) hadoop-dfs-dir.txt : checksums that are compared in this test.
 * Please read hadoop-dfs-dir.txt for more information.
 */
public class TestDFSUpgradeFromImage {
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSUpgradeFromImage.class);

    private static final File TEST_ROOT_DIR = new File(MiniDFSCluster.getBaseDirectory());

    private static final String HADOOP_DFS_DIR_TXT = "hadoop-dfs-dir.txt";

    private static final String HADOOP22_IMAGE = "hadoop-22-dfs-dir.tgz";

    private static final String HADOOP1_BBW_IMAGE = "hadoop1-bbw.tgz";

    private static final String HADOOP1_RESERVED_IMAGE = "hadoop-1-reserved.tgz";

    private static final String HADOOP023_RESERVED_IMAGE = "hadoop-0.23-reserved.tgz";

    private static final String HADOOP2_RESERVED_IMAGE = "hadoop-2-reserved.tgz";

    private static final String HADOOP252_IMAGE = "hadoop-252-dfs-dir.tgz";

    private static class ReferenceFileInfo {
        String path;

        long checksum;
    }

    static final Configuration upgradeConf;

    static {
        upgradeConf = new HdfsConfiguration();
        TestDFSUpgradeFromImage.upgradeConf.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));// block scanning off

        if ((System.getProperty(SYSPROP_TEST_DATA_DIR)) == null) {
            // to allow test to be run outside of Maven
            System.setProperty(SYSPROP_TEST_DATA_DIR, DEFAULT_TEST_DATA_DIR);
        }
    }

    public interface ClusterVerifier {
        public void verifyClusterPostUpgrade(final MiniDFSCluster cluster) throws IOException;
    }

    final LinkedList<TestDFSUpgradeFromImage.ReferenceFileInfo> refList = new LinkedList<TestDFSUpgradeFromImage.ReferenceFileInfo>();

    Iterator<TestDFSUpgradeFromImage.ReferenceFileInfo> refIter;

    boolean printChecksum = false;

    /**
     * Test that sets up a fake image from Hadoop 0.3.0 and tries to start a
     * NN, verifying that the correct error message is thrown.
     */
    @Test
    public void testFailOnPreUpgradeImage() throws IOException {
        Configuration conf = new HdfsConfiguration();
        File namenodeStorage = new File(TestDFSUpgradeFromImage.TEST_ROOT_DIR, "nnimage-0.3.0");
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, namenodeStorage.toString());
        // Set up a fake NN storage that looks like an ancient Hadoop dir circa 0.3.0
        FileUtil.fullyDelete(namenodeStorage);
        Assert.assertTrue(("Make " + namenodeStorage), namenodeStorage.mkdirs());
        File imageDir = new File(namenodeStorage, "image");
        Assert.assertTrue(("Make " + imageDir), imageDir.mkdirs());
        // Hex dump of a formatted image from Hadoop 0.3.0
        File imageFile = new File(imageDir, "fsimage");
        byte[] imageBytes = StringUtils.hexStringToByte("fffffffee17c0d2700000000");
        FileOutputStream fos = new FileOutputStream(imageFile);
        try {
            fos.write(imageBytes);
        } finally {
            fos.close();
        }
        // Now try to start an NN from it
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).startupOption(REGULAR).build();
            Assert.fail("Was able to start NN from 0.3.0 image");
        } catch (IOException ioe) {
            if (!(ioe.toString().contains("Old layout version is 'too old'"))) {
                throw ioe;
            }
        } finally {
            // We expect startup to fail, but just in case it didn't, shutdown now.
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test upgrade from 0.22 image
     */
    @Test
    public void testUpgradeFromRel22Image() throws IOException {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP22_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        upgradeAndVerify(new MiniDFSCluster.Builder(TestDFSUpgradeFromImage.upgradeConf).numDataNodes(4), null);
    }

    /**
     * Test upgrade from 0.22 image with corrupt md5, make sure it
     * fails to upgrade
     */
    @Test
    public void testUpgradeFromCorruptRel22Image() throws IOException {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP22_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        // Overwrite the md5 stored in the VERSION files
        File[] nnDirs = MiniDFSCluster.getNameNodeDirectory(MiniDFSCluster.getBaseDirectory(), 0, 0);
        FSImageTestUtil.corruptVersionFile(new File(nnDirs[0], "current/VERSION"), "imageMD5Digest", "22222222222222222222222222222222");
        FSImageTestUtil.corruptVersionFile(new File(nnDirs[1], "current/VERSION"), "imageMD5Digest", "22222222222222222222222222222222");
        // Attach our own log appender so we can verify output
        final LogVerificationAppender appender = new LogVerificationAppender();
        final org.apache.log4j.Logger logger = getRootLogger();
        logger.addAppender(appender);
        // Upgrade should now fail
        try {
            upgradeAndVerify(new MiniDFSCluster.Builder(TestDFSUpgradeFromImage.upgradeConf).numDataNodes(4), null);
            Assert.fail("Upgrade did not fail with bad MD5");
        } catch (IOException ioe) {
            String msg = StringUtils.stringifyException(ioe);
            if (!(msg.contains("Failed to load FSImage file"))) {
                throw ioe;
            }
            int md5failures = appender.countExceptionsWithMessage(" is corrupt with MD5 checksum of ");
            Assert.assertEquals("Upgrade did not fail with bad MD5", 1, md5failures);
        }
    }

    /**
     * Test upgrade from a branch-1.2 image with reserved paths
     */
    @Test
    public void testUpgradeFromRel1ReservedImage() throws Exception {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP1_RESERVED_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        MiniDFSCluster cluster = null;
        // Try it once without setting the upgrade flag to ensure it fails
        final Configuration conf = new Configuration();
        // Try it again with a custom rename string
        try {
            FSImageFormat.setRenameReservedPairs((".snapshot=.user-snapshot," + ".reserved=.my-reserved"));
            cluster = new MiniDFSCluster.Builder(conf).format(false).startupOption(UPGRADE).numDataNodes(0).build();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // Make sure the paths were renamed as expected
            // Also check that paths are present after a restart, checks that the
            // upgraded fsimage has the same state.
            final String[] expected = new String[]{ "/.my-reserved", "/.user-snapshot", "/.user-snapshot/.user-snapshot", "/.user-snapshot/open", "/dir1", "/dir1/.user-snapshot", "/dir2", "/dir2/.user-snapshot", "/user", "/user/andrew", "/user/andrew/.user-snapshot" };
            for (int i = 0; i < 2; i++) {
                // Restart the second time through this loop
                if (i == 1) {
                    cluster.finalizeCluster(conf);
                    cluster.restartNameNode(true);
                }
                ArrayList<Path> toList = new ArrayList<Path>();
                toList.add(new Path("/"));
                ArrayList<String> found = new ArrayList<String>();
                while (!(toList.isEmpty())) {
                    Path p = toList.remove(0);
                    FileStatus[] statuses = dfs.listStatus(p);
                    for (FileStatus status : statuses) {
                        final String path = status.getPath().toUri().getPath();
                        System.out.println(("Found path " + path));
                        found.add(path);
                        if (status.isDirectory()) {
                            toList.add(status.getPath());
                        }
                    }
                } 
                for (String s : expected) {
                    Assert.assertTrue(("Did not find expected path " + s), found.contains(s));
                }
                Assert.assertEquals("Found an unexpected path while listing filesystem", found.size(), expected.length);
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test upgrade from a 0.23.11 image with reserved paths
     */
    @Test
    public void testUpgradeFromRel023ReservedImage() throws Exception {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP023_RESERVED_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        MiniDFSCluster cluster = null;
        // Try it once without setting the upgrade flag to ensure it fails
        final Configuration conf = new Configuration();
        // Try it again with a custom rename string
        try {
            FSImageFormat.setRenameReservedPairs((".snapshot=.user-snapshot," + ".reserved=.my-reserved"));
            cluster = new MiniDFSCluster.Builder(conf).format(false).startupOption(UPGRADE).numDataNodes(0).build();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // Make sure the paths were renamed as expected
            // Also check that paths are present after a restart, checks that the
            // upgraded fsimage has the same state.
            final String[] expected = new String[]{ "/.user-snapshot", "/dir1", "/dir1/.user-snapshot", "/dir2", "/dir2/.user-snapshot" };
            for (int i = 0; i < 2; i++) {
                // Restart the second time through this loop
                if (i == 1) {
                    cluster.finalizeCluster(conf);
                    cluster.restartNameNode(true);
                }
                ArrayList<Path> toList = new ArrayList<Path>();
                toList.add(new Path("/"));
                ArrayList<String> found = new ArrayList<String>();
                while (!(toList.isEmpty())) {
                    Path p = toList.remove(0);
                    FileStatus[] statuses = dfs.listStatus(p);
                    for (FileStatus status : statuses) {
                        final String path = status.getPath().toUri().getPath();
                        System.out.println(("Found path " + path));
                        found.add(path);
                        if (status.isDirectory()) {
                            toList.add(status.getPath());
                        }
                    }
                } 
                for (String s : expected) {
                    Assert.assertTrue(("Did not find expected path " + s), found.contains(s));
                }
                Assert.assertEquals("Found an unexpected path while listing filesystem", found.size(), expected.length);
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test upgrade from 2.0 image with a variety of .snapshot and .reserved
     * paths to test renaming on upgrade
     */
    @Test
    public void testUpgradeFromRel2ReservedImage() throws Exception {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP2_RESERVED_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        MiniDFSCluster cluster = null;
        // Try it once without setting the upgrade flag to ensure it fails
        final Configuration conf = new Configuration();
        try {
            cluster = new MiniDFSCluster.Builder(conf).format(false).startupOption(UPGRADE).numDataNodes(0).build();
        } catch (IOException ioe) {
            Throwable cause = ioe.getCause();
            if ((cause != null) && (cause instanceof IllegalReservedPathException)) {
                GenericTestUtils.assertExceptionContains("reserved path component in this version", cause);
            } else {
                throw ioe;
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
        // Try it again with a custom rename string
        try {
            FSImageFormat.setRenameReservedPairs((".snapshot=.user-snapshot," + ".reserved=.my-reserved"));
            cluster = new MiniDFSCluster.Builder(conf).format(false).startupOption(UPGRADE).numDataNodes(0).build();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // Make sure the paths were renamed as expected
            // Also check that paths are present after a restart, checks that the
            // upgraded fsimage has the same state.
            final String[] expected = new String[]{ "/edits", "/edits/.reserved", "/edits/.user-snapshot", "/edits/.user-snapshot/editsdir", "/edits/.user-snapshot/editsdir/editscontents", "/edits/.user-snapshot/editsdir/editsdir2", "/image", "/image/.reserved", "/image/.user-snapshot", "/image/.user-snapshot/imagedir", "/image/.user-snapshot/imagedir/imagecontents", "/image/.user-snapshot/imagedir/imagedir2", "/.my-reserved", "/.my-reserved/edits-touch", "/.my-reserved/image-touch" };
            for (int i = 0; i < 2; i++) {
                // Restart the second time through this loop
                if (i == 1) {
                    cluster.finalizeCluster(conf);
                    cluster.restartNameNode(true);
                }
                ArrayList<Path> toList = new ArrayList<Path>();
                toList.add(new Path("/"));
                ArrayList<String> found = new ArrayList<String>();
                while (!(toList.isEmpty())) {
                    Path p = toList.remove(0);
                    FileStatus[] statuses = dfs.listStatus(p);
                    for (FileStatus status : statuses) {
                        final String path = status.getPath().toUri().getPath();
                        System.out.println(("Found path " + path));
                        found.add(path);
                        if (status.isDirectory()) {
                            toList.add(status.getPath());
                        }
                    }
                } 
                for (String s : expected) {
                    Assert.assertTrue(("Did not find expected path " + s), found.contains(s));
                }
                Assert.assertEquals("Found an unexpected path while listing filesystem", found.size(), expected.length);
            }
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test upgrade from a 1.x image with some blocksBeingWritten
     */
    @Test
    public void testUpgradeFromRel1BBWImage() throws IOException {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP1_BBW_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        Configuration conf = new Configuration(TestDFSUpgradeFromImage.upgradeConf);
        conf.set(DFS_DATANODE_DATA_DIR_KEY, GenericTestUtils.getTempPath((((("dfs" + (File.separator)) + "data") + (File.separator)) + "data1")));
        upgradeAndVerify(new MiniDFSCluster.Builder(conf).numDataNodes(1).enableManagedDfsDirsRedundancy(false).manageDataDfsDirs(false), null);
    }

    @Test
    public void testPreserveEditLogs() throws Exception {
        unpackStorage(TestDFSUpgradeFromImage.HADOOP252_IMAGE, TestDFSUpgradeFromImage.HADOOP_DFS_DIR_TXT);
        /**
         * The pre-created image has the following edits:
         * mkdir /input; mkdir /input/dir1~5
         * copyFromLocal randome_file_1 /input/dir1
         * copyFromLocal randome_file_2 /input/dir2
         * mv /input/dir1/randome_file_1 /input/dir3/randome_file_3
         * rmdir /input/dir1
         */
        Configuration conf = new HdfsConfiguration();
        conf = UpgradeUtilities.initializeStorageStateConf(1, conf);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).startupOption(UPGRADE).build();
        DFSInotifyEventInputStream ieis = cluster.getFileSystem().getInotifyEventStream(0);
        EventBatch batch;
        Event.CreateEvent ce;
        Event.RenameEvent re;
        // mkdir /input
        batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
        Assert.assertEquals(1, batch.getEvents().length);
        Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.CREATE)));
        ce = ((Event.CreateEvent) (batch.getEvents()[0]));
        Assert.assertEquals(ce.getPath(), "/input");
        // mkdir /input/dir1~5
        for (int i = 1; i <= 5; i++) {
            batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
            Assert.assertEquals(1, batch.getEvents().length);
            Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.CREATE)));
            ce = ((Event.CreateEvent) (batch.getEvents()[0]));
            Assert.assertEquals(ce.getPath(), ("/input/dir" + i));
        }
        // copyFromLocal randome_file_1~2 /input/dir1~2
        for (int i = 1; i <= 2; i++) {
            batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
            Assert.assertEquals(1, batch.getEvents().length);
            if ((batch.getEvents()[0].getEventType()) != (EventType.CREATE)) {
                FSImage.LOG.debug("");
            }
            Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.CREATE)));
            // copyFromLocal randome_file_1 /input/dir1, CLOSE
            batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
            Assert.assertEquals(1, batch.getEvents().length);
            Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.CLOSE)));
            // copyFromLocal randome_file_1 /input/dir1, CLOSE
            batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
            Assert.assertEquals(1, batch.getEvents().length);
            Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.RENAME)));
            re = ((Event.RenameEvent) (batch.getEvents()[0]));
            Assert.assertEquals(re.getDstPath(), ((("/input/dir" + i) + "/randome_file_") + i));
        }
        // mv /input/dir1/randome_file_1 /input/dir3/randome_file_3
        long txIDBeforeRename = batch.getTxid();
        batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
        Assert.assertEquals(1, batch.getEvents().length);
        Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.RENAME)));
        re = ((Event.RenameEvent) (batch.getEvents()[0]));
        Assert.assertEquals(re.getDstPath(), "/input/dir3/randome_file_3");
        // rmdir /input/dir1
        batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
        Assert.assertEquals(1, batch.getEvents().length);
        Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.UNLINK)));
        Assert.assertEquals(getPath(), "/input/dir1");
        long lastTxID = batch.getTxid();
        // Start inotify from the tx before rename /input/dir1/randome_file_1
        ieis = cluster.getFileSystem().getInotifyEventStream(txIDBeforeRename);
        batch = TestDFSInotifyEventInputStream.waitForNextEvents(ieis);
        Assert.assertEquals(1, batch.getEvents().length);
        Assert.assertTrue(((batch.getEvents()[0].getEventType()) == (EventType.RENAME)));
        re = ((Event.RenameEvent) (batch.getEvents()[0]));
        Assert.assertEquals(re.getDstPath(), "/input/dir3/randome_file_3");
        // Try to read beyond available edits
        ieis = cluster.getFileSystem().getInotifyEventStream((lastTxID + 1));
        Assert.assertNull(ieis.poll());
        cluster.shutdown();
    }
}

