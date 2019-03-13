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


import CreateEncryptionZoneFlag.PROVISION_TRASH;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.kms.server.MiniKMS;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * This class tests Trash functionality in Encryption Zones with Kerberos
 * enabled.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTrashWithSecureEncryptionZones {
    private static HdfsConfiguration baseConf;

    private static File baseDir;

    private static final EnumSet<CreateEncryptionZoneFlag> PROVISION_TRASH = EnumSet.of(CreateEncryptionZoneFlag.PROVISION_TRASH);

    private static final String HDFS_USER_NAME = "hdfs";

    private static final String SPNEGO_USER_NAME = "HTTP";

    private static final String OOZIE_USER_NAME = "oozie";

    private static final String OOZIE_PROXIED_USER_NAME = "oozie_user";

    private static String hdfsPrincipal;

    private static String spnegoPrincipal;

    private static String keytab;

    // MiniKDC
    private static MiniKdc kdc;

    // MiniKMS
    private static MiniKMS miniKMS;

    private static final String TEST_KEY = "test_key";

    private static final Path CURRENT = new Path("Current");

    // MiniDFS
    private static MiniDFSCluster cluster;

    private static HdfsConfiguration conf;

    private static FileSystem fs;

    private static HdfsAdmin dfsAdmin;

    private static Configuration clientConf;

    private static FsShell shell;

    private static AtomicInteger zoneCounter = new AtomicInteger(1);

    private static AtomicInteger fileCounter = new AtomicInteger(1);

    private static final int LEN = 8192;

    @Test
    public void testTrashCheckpoint() throws Exception {
        final Path zone1 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone1);
        final Path zone2 = new Path(((zone1 + "/zone") + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone2);
        TestTrashWithSecureEncryptionZones.dfsAdmin.createEncryptionZone(zone2, TestTrashWithSecureEncryptionZones.TEST_KEY, TestTrashWithSecureEncryptionZones.PROVISION_TRASH);
        final Path encFile1 = new Path(zone2, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, encFile1, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        // Verify Trash checkpoint within Encryption Zone
        Path trashDir = new Path(zone2, (((((TestTrashWithSecureEncryptionZones.fs.TRASH_PREFIX) + "/") + (TestTrashWithSecureEncryptionZones.HDFS_USER_NAME)) + "/") + (TestTrashWithSecureEncryptionZones.CURRENT)));
        String trashPath = (trashDir.toString()) + (encFile1.toString());
        Path deletedFile = verifyTrashLocationWithShellDelete(encFile1);
        Assert.assertEquals(("Deleted file not at the expected trash location: " + trashPath), trashPath, deletedFile.toUri().getPath());
        // Verify Trash checkpoint outside the encryption zone when the whole
        // encryption zone is deleted and moved
        trashPath = (((((TestTrashWithSecureEncryptionZones.fs.getHomeDirectory().toUri().getPath()) + "/") + (TestTrashWithSecureEncryptionZones.fs.TRASH_PREFIX)) + "/") + (TestTrashWithSecureEncryptionZones.CURRENT)) + zone2;
        Path deletedDir = verifyTrashLocationWithShellDelete(zone2);
        Assert.assertEquals(("Deleted zone not at the expected trash location: " + trashPath), trashPath, deletedDir.toUri().getPath());
    }

    @Test
    public void testTrashExpunge() throws Exception {
        final Path zone1 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone1);
        final Path zone2 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone2);
        TestTrashWithSecureEncryptionZones.dfsAdmin.createEncryptionZone(zone1, TestTrashWithSecureEncryptionZones.TEST_KEY, TestTrashWithSecureEncryptionZones.PROVISION_TRASH);
        final Path file1 = new Path(zone1, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        final Path file2 = new Path(zone2, ("file" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, file1, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, file2, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        // Verify Trash expunge within the encryption zone
        List<Path> trashPaths = Lists.newArrayList();
        trashPaths.add(verifyTrashLocationWithShellDelete(file1));
        trashPaths.add(verifyTrashLocationWithShellDelete(file2));
        verifyTrashExpunge(trashPaths);
        // Verify Trash expunge when the whole encryption zone has been deleted
        final Path file3 = new Path(zone1, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, file3, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        Path trashPath = verifyTrashLocationWithShellDelete(file3);
        // Delete encryption zone
        DFSTestUtil.verifyDelete(TestTrashWithSecureEncryptionZones.shell, TestTrashWithSecureEncryptionZones.fs, zone1, true);
        verifyTrashExpunge(Lists.newArrayList(trashPath));
    }

    @Test
    public void testDeleteWithSkipTrash() throws Exception {
        final Path zone1 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone1);
        final Path encFile1 = new Path(zone1, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        final Path encFile2 = new Path(zone1, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, encFile1, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, encFile2, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        // Verify file deletion with skipTrash
        verifyDeleteWithSkipTrash(encFile1);
        // Verify file deletion without skipTrash
        DFSTestUtil.verifyDelete(TestTrashWithSecureEncryptionZones.shell, TestTrashWithSecureEncryptionZones.fs, encFile2, true);
    }

    @Test
    public void testDeleteEmptyDirectory() throws Exception {
        final Path zone1 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        final Path zone2 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone1);
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone2);
        final Path trashDir1 = new Path((((TestTrashWithSecureEncryptionZones.shell.getCurrentTrashDir(zone1)) + "/") + zone1));
        final Path trashDir2 = new Path((((TestTrashWithSecureEncryptionZones.shell.getCurrentTrashDir(zone1)) + "/") + zone2));
        // Delete empty directory with -r option
        String[] argv1 = new String[]{ "-rm", "-r", zone1.toString() };
        int res = ToolRunner.run(TestTrashWithSecureEncryptionZones.shell, argv1);
        Assert.assertEquals("rm failed", 0, res);
        Assert.assertTrue(("Empty directory not deleted even with -r : " + trashDir1), TestTrashWithSecureEncryptionZones.fs.exists(trashDir1));
        // Delete empty directory without -r option
        String[] argv2 = new String[]{ "-rm", zone2.toString() };
        res = ToolRunner.run(TestTrashWithSecureEncryptionZones.shell, argv2);
        Assert.assertEquals("rm on empty directory did not fail", 1, res);
        Assert.assertTrue(("Empty directory deleted without -r : " + trashDir2), (!(TestTrashWithSecureEncryptionZones.fs.exists(trashDir2))));
    }

    @Test
    public void testDeleteFromTrashWithinEZ() throws Exception {
        final Path zone1 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone1);
        TestTrashWithSecureEncryptionZones.dfsAdmin.createEncryptionZone(zone1, TestTrashWithSecureEncryptionZones.TEST_KEY, TestTrashWithSecureEncryptionZones.PROVISION_TRASH);
        final Path encFile1 = new Path(zone1, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, encFile1, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        final Path trashFile = new Path((((TestTrashWithSecureEncryptionZones.shell.getCurrentTrashDir(encFile1)) + "/") + encFile1));
        String[] argv = new String[]{ "-rm", "-r", encFile1.toString() };
        int res = ToolRunner.run(TestTrashWithSecureEncryptionZones.shell, argv);
        Assert.assertEquals("rm failed", 0, res);
        String[] argvDeleteTrash = new String[]{ "-rm", "-r", trashFile.toString() };
        int resDeleteTrash = ToolRunner.run(TestTrashWithSecureEncryptionZones.shell, argvDeleteTrash);
        Assert.assertEquals("rm failed", 0, resDeleteTrash);
        Assert.assertFalse(("File deleted from Trash : " + trashFile), TestTrashWithSecureEncryptionZones.fs.exists(trashFile));
    }

    @Test
    public void testTrashRetentionAfterNamenodeRestart() throws Exception {
        final Path zone1 = new Path(("/zone" + (TestTrashWithSecureEncryptionZones.zoneCounter.getAndIncrement())));
        TestTrashWithSecureEncryptionZones.fs.mkdirs(zone1);
        TestTrashWithSecureEncryptionZones.dfsAdmin.createEncryptionZone(zone1, TestTrashWithSecureEncryptionZones.TEST_KEY, TestTrashWithSecureEncryptionZones.PROVISION_TRASH);
        final Path encFile1 = new Path(zone1, ("encFile" + (TestTrashWithSecureEncryptionZones.fileCounter.getAndIncrement())));
        DFSTestUtil.createFile(TestTrashWithSecureEncryptionZones.fs, encFile1, TestTrashWithSecureEncryptionZones.LEN, ((short) (1)), 65261);
        final Path trashFile = new Path((((TestTrashWithSecureEncryptionZones.shell.getCurrentTrashDir(encFile1)) + "/") + encFile1));
        String[] argv = new String[]{ "-rm", "-r", encFile1.toString() };
        int res = ToolRunner.run(TestTrashWithSecureEncryptionZones.shell, argv);
        Assert.assertEquals("rm failed", 0, res);
        Assert.assertTrue(("File not in trash : " + trashFile), TestTrashWithSecureEncryptionZones.fs.exists(trashFile));
        TestTrashWithSecureEncryptionZones.cluster.restartNameNode(0);
        TestTrashWithSecureEncryptionZones.cluster.waitActive();
        TestTrashWithSecureEncryptionZones.fs = TestTrashWithSecureEncryptionZones.cluster.getFileSystem();
        Assert.assertTrue(("On Namenode restart, file deleted from trash : " + trashFile), TestTrashWithSecureEncryptionZones.fs.exists(trashFile));
    }
}

