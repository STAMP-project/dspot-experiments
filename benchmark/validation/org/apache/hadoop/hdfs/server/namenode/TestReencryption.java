/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_KEY_PROVIDER_PATH;
import CreateEncryptionZoneFlag.NO_TRASH;
import ReencryptAction.CANCEL;
import ReencryptAction.START;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import ZoneReencryptionStatus.State.Completed;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContextTestWrapper;
import org.apache.hadoop.fs.FileEncryptionInfo;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.protocol.ReencryptionStatus;
import org.apache.hadoop.hdfs.protocol.SnapshotAccessControlException;
import org.apache.hadoop.hdfs.protocol.ZoneReencryptionStatus;
import org.apache.hadoop.hdfs.server.namenode.ReencryptionUpdater.ZoneSubmissionTracker;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.RetriableException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static EncryptionFaultInjector.instance;


/**
 * Test class for re-encryption.
 */
public class TestReencryption {
    protected static final Logger LOG = LoggerFactory.getLogger(TestReencryption.class);

    private Configuration conf;

    private FileSystemTestHelper fsHelper;

    private MiniDFSCluster cluster;

    protected HdfsAdmin dfsAdmin;

    private DistributedFileSystem fs;

    private FSNamesystem fsn;

    private File testRootDir;

    private static final String TEST_KEY = "test_key";

    private FileSystemTestWrapper fsWrapper;

    private FileContextTestWrapper fcWrapper;

    private static final EnumSet<CreateEncryptionZoneFlag> NO_TRASH = EnumSet.of(CreateEncryptionZoneFlag.NO_TRASH);

    @Rule
    public Timeout globalTimeout = new Timeout((180 * 1000));

    @Test
    public void testReencryptionBasic() throws Exception {
        /* Setup test dir:
        /zones/zone/[0-9]
        /dir/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path("/dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        // test re-encrypt without keyroll
        final Path encFile1 = new Path(zone, "0");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile1);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        assertKeyVersionEquals(encFile1, fei0);
        // key not rolled, so no edeks need to be updated.
        verifyZoneStatus(zone, null, 0);
        // test re-encrypt after keyroll
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(2);
        FileEncryptionInfo fei1 = getFileEncryptionInfo(encFile1);
        assertKeyVersionChanged(encFile1, fei0);
        // test listReencryptionStatus
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        verifyZoneCompletionTime(zs);
        Assert.assertNotEquals(fei0.getEzKeyVersionName(), zs.getEzKeyVersionName());
        Assert.assertEquals(fei1.getEzKeyVersionName(), zs.getEzKeyVersionName());
        Assert.assertEquals(10, zs.getFilesReencrypted());
        // test re-encrypt on same zone again
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(3);
        assertKeyVersionEquals(encFile1, fei1);
        // test non-EZ submission
        try {
            dfsAdmin.reencryptEncryptionZone(subdir, START);
            Assert.fail("Re-encrypting non-EZ should fail");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            GenericTestUtils.assertExceptionContains("not the root of an encryption zone", expected);
        }
        // test non-existing dir
        try {
            dfsAdmin.reencryptEncryptionZone(new Path(zone, "notexist"), START);
            Assert.fail("Re-encrypting non-existing dir should fail");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            Assert.assertTrue(((expected.unwrapRemoteException()) instanceof FileNotFoundException));
        }
        // test directly on a EZ file
        try {
            dfsAdmin.reencryptEncryptionZone(encFile1, START);
            Assert.fail("Re-encrypting on a file should fail");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            GenericTestUtils.assertExceptionContains("not the root of an encryption zone", expected);
        }
        // test same command resubmission
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        try {
            dfsAdmin.reencryptEncryptionZone(zone, START);
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            GenericTestUtils.assertExceptionContains("already submitted", expected);
        }
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedZones(4);
        // test empty EZ
        final Path emptyZone = new Path("/emptyZone");
        fsWrapper.mkdir(emptyZone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(emptyZone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        dfsAdmin.reencryptEncryptionZone(emptyZone, START);
        waitForReencryptedZones(5);
        dfsAdmin.reencryptEncryptionZone(emptyZone, START);
        waitForReencryptedZones(6);
        // test rename ez and listReencryptionStatus
        final Path renamedZone = new Path("/renamedZone");
        fsWrapper.rename(zone, renamedZone);
        it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        zs = it.next();
        Assert.assertEquals(renamedZone.toString(), zs.getZoneName());
    }

    @Test
    public void testReencryptOrdering() throws Exception {
        /* Setup dir as follows:
        /zones/zone/[0-3]
        /zones/zone/dir/f
        /zones/zone/f[0-4]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        Path subdir = new Path(zone, "dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        for (int i = 0; i < 4; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        for (int i = 0; i < 5; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("f" + (Integer.toString(i)))), len, ((short) (1)), 65261);
        }
        // /zones/zone/f[0-4] should be re-encrypted after /zones/zone/dir/f
        final Path lastReencryptedFile = new Path(subdir, "f");
        final Path notReencrypted = new Path(zone, "f0");
        final FileEncryptionInfo fei = getFileEncryptionInfo(lastReencryptedFile);
        final FileEncryptionInfo feiLast = getFileEncryptionInfo(notReencrypted);
        rollKey(TestReencryption.TEST_KEY);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthSubmission(1);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedFiles(zone.toString(), 5);
        assertKeyVersionChanged(lastReencryptedFile, fei);
        assertKeyVersionEquals(notReencrypted, feiLast);
    }

    @Test
    public void testDeleteDuringReencrypt() throws Exception {
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        // test zone deleted during re-encrypt
        getEzManager().pauseReencryptForTesting();
        getEzManager().resetMetricsForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        fs.delete(zone, true);
        getEzManager().resumeReencryptForTesting();
        waitForTotalZones(0);
        Assert.assertNull(getZoneStatus(zone.toString()));
    }

    @Test
    public void testZoneDeleteDuringReencrypt() throws Exception {
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // test zone deleted during re-encrypt's checkpointing
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resetMetricsForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedFiles(zone.toString(), 5);
        fs.delete(zoneParent, true);
        getEzManager().resumeReencryptForTesting();
        waitForTotalZones(0);
        Assert.assertNull(getEzManager().getZoneStatus(zone.toString()));
        // verify zone is cleared
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testRestartAfterReencrypt() throws Exception {
        /* Setup dir as follows:
        /zones
        /zones/zone
        /zones/zone/[0-9]
        /zones/zone/dir
        /zones/zone/dir/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        final Path encFile0 = new Path(zone, "0");
        final Path encFile9 = new Path(zone, "9");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9 = getFileEncryptionInfo(encFile9);
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        assertKeyVersionChanged(encFile0, fei0);
        assertKeyVersionChanged(encFile9, fei9);
        final FileEncryptionInfo fei0new = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9new = getFileEncryptionInfo(encFile9);
        restartClusterDisableReencrypt();
        assertKeyVersionEquals(encFile0, fei0new);
        assertKeyVersionEquals(encFile9, fei9new);
        Assert.assertNull("Re-encrypt queue should be empty after restart", getReencryptionStatus().getNextUnprocessedZone());
    }

    @Test
    public void testRestartWithRenames() throws Exception {
        /* Setup dir as follows:
        /zones
        /zones/zone
        /zones/zone/f --> renamed to f1
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        DFSTestUtil.createFile(fs, new Path(zone, "f"), len, ((short) (1)), 65261);
        fsWrapper.rename(new Path(zone, "f"), new Path(zone, "f1"));
        // re-encrypt
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        // make sure NN can successfully restart (rename can load ok with
        // re-encrypt since they're in correct order)
        cluster.restartNameNodes();
        cluster.waitActive();
        waitForReencryptedZones(1);
    }

    @Test
    public void testRestartDuringReencrypt() throws Exception {
        /* Setup dir as follows:
        /zones
        /zones/zone
        /zones/zone/dir_empty
        /zones/zone/dir1/[0-9]
        /zones/zone/dir1/dir_empty1
        /zones/zone/dir2
        /zones/zone/dir2/dir_empty2
        /zones/zone/dir2/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        fsWrapper.mkdir(new Path(zone, "dir_empty"), FsPermission.getDirDefault(), true);
        Path subdir = new Path(zone, "dir2");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        fsWrapper.mkdir(new Path(subdir, "dir_empty2"), FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        subdir = new Path(zone, "dir1");
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        fsWrapper.mkdir(new Path(subdir, "dir_empty1"), FsPermission.getDirDefault(), true);
        final Path encFile0 = new Path(subdir, "0");
        final Path encFile9 = new Path(subdir, "9");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9 = getFileEncryptionInfo(encFile9);
        rollKey(TestReencryption.TEST_KEY);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthSubmission(1);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedFiles(zone.toString(), 5);
        restartClusterDisableReencrypt();
        final Long zoneId = fsn.getFSDirectory().getINode(zone.toString()).getId();
        Assert.assertEquals("Re-encrypt should restore to the last checkpoint zone", zoneId, getReencryptionStatus().getNextUnprocessedZone());
        Assert.assertEquals("Re-encrypt should restore to the last checkpoint file", new Path(subdir, "4").toString(), getEzManager().getZoneStatus(zone.toString()).getLastCheckpointFile());
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedZones(1);
        assertKeyVersionChanged(encFile0, fei0);
        assertKeyVersionChanged(encFile9, fei9);
        Assert.assertNull("Re-encrypt queue should be empty after restart", getReencryptionStatus().getNextUnprocessedZone());
        Assert.assertEquals(11, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRestartAfterReencryptAndCheckpoint() throws Exception {
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        final Path encFile0 = new Path(zone, "0");
        final Path encFile9 = new Path(zone, "9");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9 = getFileEncryptionInfo(encFile9);
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        assertKeyVersionChanged(encFile0, fei0);
        assertKeyVersionChanged(encFile9, fei9);
        final FileEncryptionInfo fei0new = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9new = getFileEncryptionInfo(encFile9);
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        restartClusterDisableReencrypt();
        assertKeyVersionEquals(encFile0, fei0new);
        assertKeyVersionEquals(encFile9, fei9new);
        Assert.assertNull("Re-encrypt queue should be empty after restart", getReencryptionStatus().getNextUnprocessedZone());
    }

    @Test
    public void testReencryptLoadedFromEdits() throws Exception {
        /* /zones/zone/[0-9]
        /zones/zone/dir/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        final Path encFile0 = new Path(zone, "0");
        final Path encFile9 = new Path(zone, "9");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9 = getFileEncryptionInfo(encFile9);
        rollKey(TestReencryption.TEST_KEY);
        // disable re-encrypt for testing, and issue a command
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        // verify after restart the command is loaded
        restartClusterDisableReencrypt();
        waitForQueuedZones(1);
        // Let the re-encrypt to start running.
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedZones(1);
        assertKeyVersionChanged(encFile0, fei0);
        assertKeyVersionChanged(encFile9, fei9);
        // verify status
        verifyZoneStatus(zone, fei0, 11);
    }

    @Test
    public void testReencryptLoadedFromFsimage() throws Exception {
        /* /zones/zone/[0-9]
        /zones/zone/dir/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        final Path encFile0 = new Path(zone, "0");
        final Path encFile9 = new Path(zone, "9");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile0);
        final FileEncryptionInfo fei9 = getFileEncryptionInfo(encFile9);
        rollKey(TestReencryption.TEST_KEY);
        // disable re-encrypt for testing, and issue a command
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        // verify after loading from fsimage the command is loaded
        restartClusterDisableReencrypt();
        waitForQueuedZones(1);
        // Let the re-encrypt to start running.
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedZones(1);
        assertKeyVersionChanged(encFile0, fei0);
        assertKeyVersionChanged(encFile9, fei9);
        // verify status
        verifyZoneStatus(zone, fei0, 11);
    }

    @Test
    public void testReencryptCommandsQueuedOrdering() throws Exception {
        final Path zoneParent = new Path("/zones");
        final String zoneBaseName = (zoneParent.toString()) + "/zone";
        final int numZones = 10;
        for (int i = 0; i < numZones; ++i) {
            final Path zone = new Path((zoneBaseName + i));
            fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
            dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        }
        // Disable re-encrypt for testing, and issue commands
        getEzManager().pauseReencryptForTesting();
        for (int i = 0; i < numZones; ++i) {
            dfsAdmin.reencryptEncryptionZone(new Path((zoneBaseName + i)), START);
        }
        waitForQueuedZones(numZones);
        // Verify commands are queued in the same order submitted
        ReencryptionStatus rzs = new ReencryptionStatus(getReencryptionStatus());
        for (int i = 0; i < numZones; ++i) {
            Long zoneId = fsn.getFSDirectory().getINode((zoneBaseName + i)).getId();
            Assert.assertEquals(zoneId, rzs.getNextUnprocessedZone());
            rzs.removeZone(zoneId);
        }
        // Cancel some zones
        Set<Integer> cancelled = new HashSet<>(Arrays.asList(0, 3, 4));
        for (int cancel : cancelled) {
            dfsAdmin.reencryptEncryptionZone(new Path((zoneBaseName + cancel)), CANCEL);
        }
        restartClusterDisableReencrypt();
        waitForQueuedZones((numZones - (cancelled.size())));
        rzs = new ReencryptionStatus(getReencryptionStatus());
        for (int i = 0; i < numZones; ++i) {
            if (cancelled.contains(i)) {
                continue;
            }
            Long zoneId = fsn.getFSDirectory().getINode((zoneBaseName + i)).getId();
            Assert.assertEquals(zoneId, rzs.getNextUnprocessedZone());
            rzs.removeZone(zoneId);
        }
        // Verify the same is true after loading from FSImage
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        restartClusterDisableReencrypt();
        waitForQueuedZones((numZones - (cancelled.size())));
        rzs = new ReencryptionStatus(getReencryptionStatus());
        for (int i = 0; i < 10; ++i) {
            if (cancelled.contains(i)) {
                continue;
            }
            Long zoneId = fsn.getFSDirectory().getINode((zoneBaseName + i)).getId();
            Assert.assertEquals(zoneId, rzs.getNextUnprocessedZone());
            rzs.removeZone(zoneId);
        }
    }

    @Test
    public void testReencryptNestedZones() throws Exception {
        /* Setup dir as follows:
        / <- EZ
        /file
        /dir/dfile
        /level1  <- nested EZ
        /level1/fileL1-[0~2]
        /level1/level2/ <- nested EZ
        /level1/level2/fileL2-[0~3]
         */
        final int len = 8196;
        final Path zoneRoot = new Path("/");
        final Path zoneL1 = new Path(zoneRoot, "level1");
        final Path zoneL2 = new Path(zoneL1, "level2");
        final Path nonzoneDir = new Path(zoneRoot, "dir");
        dfsAdmin.createEncryptionZone(zoneRoot, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        DFSTestUtil.createFile(fs, new Path(zoneRoot, "file"), len, ((short) (1)), 65261);
        DFSTestUtil.createFile(fs, new Path(nonzoneDir, "dfile"), len, ((short) (1)), 65261);
        fsWrapper.mkdir(zoneL1, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zoneL1, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 3; ++i) {
            DFSTestUtil.createFile(fs, new Path(zoneL1, ("fileL1-" + i)), len, ((short) (1)), 65261);
        }
        fsWrapper.mkdir(zoneL2, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zoneL2, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 4; ++i) {
            DFSTestUtil.createFile(fs, new Path(zoneL2, ("fileL2-" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Disable re-encrypt, send re-encrypt on '/', verify queue
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zoneRoot, START);
        waitForQueuedZones(1);
        ReencryptionStatus rzs = getReencryptionStatus();
        Assert.assertEquals(((Long) (fsn.getFSDirectory().getINode(zoneRoot.toString()).getId())), rzs.getNextUnprocessedZone());
        // Resume re-encrypt, verify files re-encrypted
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zoneRoot.toString());
        Assert.assertEquals(2, getZoneStatus(zoneRoot.toString()).getFilesReencrypted());
        // Same tests on a child EZ.
        getEzManager().resetMetricsForTesting();
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zoneL1, START);
        waitForQueuedZones(1);
        rzs = getReencryptionStatus();
        Assert.assertEquals(((Long) (fsn.getFSDirectory().getINode(zoneL1.toString()).getId())), rzs.getNextUnprocessedZone());
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zoneL1.toString());
        Assert.assertEquals(3, getZoneStatus(zoneL1.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceCreateHandler() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~9]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        int expected = 10;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthSubmission(1);
        // Resume the re-encrypt thread
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        /* creates the following:
        /dir/file8[0~5]
        /dir/dirsub/file[10-14]
        /dir/sub/file[15-19]
         */
        for (int i = 0; i < 6; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file8" + i)), len, ((short) (1)), 65261);
        }
        // we don't care newly created files since they should already use new edek.
        // so naturally processes the listing from last checkpoint
        final Path subdir = new Path(zone, "dirsub");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        for (int i = 10; i < 15; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, ("file" + i)), len, ((short) (1)), 65261);
        }
        // the above are created before checkpoint position, so not re-encrypted.
        final Path sub = new Path(zone, "sub");
        fsWrapper.mkdir(sub, FsPermission.getDirDefault(), true);
        for (int i = 15; i < 20; ++i) {
            DFSTestUtil.createFile(fs, new Path(sub, ("file" + i)), len, ((short) (1)), 65261);
        }
        // resume re-encrypt thread which was paused after first checkpoint
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceDeleteHandler() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~9]
        /dir/subdir/file[10-14]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        int expected = 15;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "subdir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        for (int i = 10; i < 15; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // proceed to first checkpoint (5 files), delete files/subdir, then resume
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        fsWrapper.delete(new Path(zone, "file5"), true);
        fsWrapper.delete(new Path(zone, "file8"), true);
        expected -= 2;
        fsWrapper.delete(subdir, true);
        expected -= 5;
        // resume re-encrypt thread which was paused after first checkpoint
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceDeleteUpdater() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~9]
        /dir/subdir/file[10-14]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        int expected = 15;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "subdir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        for (int i = 10; i < 15; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // proceed to first checkpoint (5 files), delete files/subdir, then resume
        getEzManager().pauseForTestingAfterNthCheckpoint(zone.toString(), 1);
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        getEzManager().resumeReencryptForTesting();
        // give handler thread some time to process the files before deletion.
        Thread.sleep(3000);
        fsWrapper.delete(new Path(zone, "file5"), true);
        fsWrapper.delete(new Path(zone, "file8"), true);
        expected -= 2;
        fsWrapper.delete(subdir, true);
        expected -= 5;
        // resume updater thread which was paused after first checkpoint, verify
        // deleted files are skipped.
        getEzManager().resumeReencryptUpdaterForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceDeleteCurrentDirHandler() throws Exception {
        /* Setup dir as follows:
        /dir/subdir/file[0~9]
        /dir/subdir2/file[10-14]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        final Path subdir = new Path(zone, "subdir");
        int expected = 15;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, ("file" + i)), len, ((short) (1)), 65261);
        }
        final Path subdir2 = new Path(zone, "subdir2");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        for (int i = 10; i < 15; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir2, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // proceed to first checkpoint (5 files), delete subdir, then resume
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        fsWrapper.delete(subdir, true);
        expected -= 5;
        // resume re-encrypt thread which was paused after first checkpoint
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceDeleteCurrentDirUpdater() throws Exception {
        /* Setup dir as follows:
        /dir/subdir/file[0~9]
        /dir/subdir2/file[10-14]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        final Path subdir = new Path(zone, "subdir");
        int expected = 15;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, ("file" + i)), len, ((short) (1)), 65261);
        }
        final Path subdir2 = new Path(zone, "subdir2");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        for (int i = 10; i < 15; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir2, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // proceed to first checkpoint (5 files), delete subdir, then resume
        getEzManager().pauseForTestingAfterNthCheckpoint(zone.toString(), 1);
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        getEzManager().resumeReencryptForTesting();
        // give handler thread some time to process the files before deletion.
        Thread.sleep(3000);
        fsWrapper.delete(subdir, true);
        expected -= 5;
        // resume updater thread which was paused after first checkpoint, verify
        // deleted files are skipped.
        getEzManager().resumeReencryptUpdaterForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceDeleteZoneHandler() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~10]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 11; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // let both handler and updater pause, then delete zone.
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().pauseForTestingAfterNthCheckpoint(zone.toString(), 1);
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        Thread.sleep(3000);
        Map<Long, ZoneSubmissionTracker> tasks = ((Map<Long, ZoneSubmissionTracker>) (Whitebox.getInternalState(getHandler(), "submissions")));
        List<Future> futures = new LinkedList<>();
        for (ZoneSubmissionTracker zst : tasks.values()) {
            for (Future f : zst.getTasks()) {
                futures.add(f);
            }
        }
        fsWrapper.delete(zone, true);
        getEzManager().resumeReencryptForTesting();
        // verify no running tasks
        for (Future f : futures) {
            Assert.assertTrue(f.isDone());
        }
        waitForTotalZones(0);
    }

    @Test
    public void testRaceDeleteCreateHandler() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~9]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        int expected = 10;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthSubmission(1);
        // Resume the re-encrypt thread
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        final Path recreated = new Path(zone, "file9");
        fsWrapper.delete(recreated, true);
        DFSTestUtil.createFile(fs, recreated, len, ((short) (2)), 65261);
        expected -= 1;// newly created files use new edek, no need to re-encrypt

        // resume re-encrypt thread which was paused after first checkpoint
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testRaceDeleteCreateUpdater() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~9]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        int expected = 10;
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthCheckpoint(zone.toString(), 1);
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        getEzManager().resumeReencryptForTesting();
        // give handler thread some time to process the files before deletion.
        Thread.sleep(3000);
        final Path recreated = new Path(zone, "file9");
        final FileEncryptionInfo feiOrig = getFileEncryptionInfo(recreated);
        final String contentOrig = DFSTestUtil.readFile(fs, recreated);
        fsWrapper.delete(recreated, true);
        DFSTestUtil.createFile(fs, recreated, len, ((short) (2)), 65261);
        expected -= 1;
        // resume updater thread which was paused after first checkpoint
        getEzManager().resumeReencryptUpdaterForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(expected, getZoneStatus(zone.toString()).getFilesReencrypted());
        // verify new file is using it's own edeks, with new keyversions,
        // and can be decrypted correctly.
        assertKeyVersionChanged(recreated, feiOrig);
        final String content = DFSTestUtil.readFile(fs, recreated);
        Assert.assertEquals(contentOrig, content);
    }

    // TODO: update test once HDFS-11203 is implemented.
    @Test
    public void testReencryptRaceRename() throws Exception {
        /* Setup dir as follows:
        /dir/file[0~9]
        /dir/subdir/file[10-14]
         */
        final int len = 8196;
        final Path zone = new Path("/dir");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, ("file" + i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path(zone, "subdir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        for (int i = 10; i < 15; ++i) {
            DFSTestUtil.createFile(fs, new Path(subdir, ("file" + i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // Issue the command re-encrypt and pause it
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthSubmission(1);
        // Resume the re-encrypt thread
        getEzManager().resumeReencryptForTesting();
        waitForReencryptedFiles(zone.toString(), 5);
        try {
            fsWrapper.rename(new Path(zone, "file8"), new Path(zone, "file08"));
            Assert.fail("rename a file in an EZ should be disabled");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("under re-encryption", e);
        }
        // resume handler and pause updater, test again.
        getEzManager().pauseReencryptUpdaterForTesting();
        getEzManager().resumeReencryptForTesting();
        try {
            fsWrapper.rename(new Path(zone, "file8"), new Path(zone, "file08"));
            Assert.fail("rename a file in an EZ should be disabled");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("under re-encryption", e);
        }
    }

    @Test
    public void testReencryptSnapshots() throws Exception {
        /* Setup test dir:
        /zones/zone/[0-9]
        /dir/f

        /zones/zone is snapshottable, and rename file 5 to 5new,
        6 to 6new then delete (so the file is only referred from a snapshot).
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.allowSnapshot(zone);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path("/dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        // create a snapshot and rename a file, so INodeReference is created.
        final Path zoneSnap = fs.createSnapshot(zone);
        fsWrapper.rename(new Path(zone, "5"), new Path(zone, "5new"));
        fsWrapper.rename(new Path(zone, "6"), new Path(zone, "6new"));
        fsWrapper.delete(new Path(zone, "6new"), true);
        // test re-encrypt on snapshot dir
        final Path encFile1 = new Path(zone, "0");
        final FileEncryptionInfo fei0 = getFileEncryptionInfo(encFile1);
        rollKey(TestReencryption.TEST_KEY);
        try {
            dfsAdmin.reencryptEncryptionZone(zoneSnap, START);
            Assert.fail("Reencrypt command on snapshot path should fail.");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception", expected);
            Assert.assertTrue(((expected.unwrapRemoteException()) instanceof SnapshotAccessControlException));
        }
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        waitForReencryptedFiles(zone.toString(), 9);
        assertKeyVersionChanged(encFile1, fei0);
    }

    @Test
    public void testReencryptCancel() throws Exception {
        /* Setup test dir:
        /zones/zone/[0-9]
        /dir/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path("/dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        rollKey(TestReencryption.TEST_KEY);
        // disable, test basic
        getEzManager().pauseReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForQueuedZones(1);
        dfsAdmin.reencryptEncryptionZone(zone, CANCEL);
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(0, getZoneStatus(zone.toString()).getFilesReencrypted());
        // test same command resubmission
        try {
            dfsAdmin.reencryptEncryptionZone(zone, CANCEL);
        } catch (RemoteException expected) {
            GenericTestUtils.assertExceptionContains("not under re-encryption", expected);
        }
        rollKey(TestReencryption.TEST_KEY);
        // test cancelling half-way
        getEzManager().pauseForTestingAfterNthSubmission(1);
        getEzManager().resumeReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedFiles(zone.toString(), 5);
        dfsAdmin.reencryptEncryptionZone(zone, CANCEL);
        getEzManager().resumeReencryptForTesting();
        waitForZoneCompletes(zone.toString());
        Assert.assertEquals(5, getZoneStatus(zone.toString()).getFilesReencrypted());
        Assert.assertNull(getEzManager().getZoneStatus(zone.toString()).getLastCheckpointFile());
        Assert.assertNull(getReencryptionStatus().getNextUnprocessedZone());
        // test cancelling non-EZ dir
        try {
            dfsAdmin.reencryptEncryptionZone(subdir, CANCEL);
            Assert.fail("Re-encrypting non-EZ should fail");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            GenericTestUtils.assertExceptionContains("not the root of an encryption zone", expected);
        }
        // test cancelling non-existing dir
        try {
            dfsAdmin.reencryptEncryptionZone(new Path(zone, "notexist"), CANCEL);
            Assert.fail("Re-encrypting non-existing dir should fail");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            Assert.assertTrue(((expected.unwrapRemoteException()) instanceof FileNotFoundException));
        }
        // test cancelling directly on a EZ file
        final Path encFile = new Path(zone, "0");
        try {
            dfsAdmin.reencryptEncryptionZone(encFile, CANCEL);
            Assert.fail("Re-encrypting on a file should fail");
        } catch (RemoteException expected) {
            TestReencryption.LOG.info("Expected exception caught.", expected);
            GenericTestUtils.assertExceptionContains("not the root of an encryption zone", expected);
        }
        // final check - should only had 5 files re-encrypted overall.
        Assert.assertEquals(5, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testCancelFutureThenReencrypt() throws Exception {
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        cancelFutureDuringReencryption(zone);
        // make sure new re-encryption after cancellation works.
        getEzManager().resumeReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForZoneCompletes(zone.toString());
        final RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        final ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        Assert.assertFalse(zs.isCanceled());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(10, zs.getFilesReencrypted());
    }

    @Test
    public void testCancelFutureThenRestart() throws Exception {
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        cancelFutureDuringReencryption(zone);
        // restart, and check status.
        restartClusterDisableReencrypt();
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        Assert.assertTrue(zs.isCanceled());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(0, zs.getFilesReencrypted());
        // verify re-encryption works after restart.
        getEzManager().resumeReencryptForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForZoneCompletes(zone.toString());
        it = dfsAdmin.listReencryptionStatus();
        zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        Assert.assertFalse(zs.isCanceled());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(10, zs.getFilesReencrypted());
    }

    @Test
    public void testReencryptCancelForUpdater() throws Exception {
        /* Setup test dir:
        /zones/zone/[0-9]
        /dir/f
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        final Path subdir = new Path("/dir");
        fsWrapper.mkdir(subdir, FsPermission.getDirDefault(), true);
        DFSTestUtil.createFile(fs, new Path(subdir, "f"), len, ((short) (1)), 65261);
        rollKey(TestReencryption.TEST_KEY);
        // disable, test basic
        getEzManager().pauseReencryptUpdaterForTesting();
        dfsAdmin.reencryptEncryptionZone(zone, START);
        Thread.sleep(3000);
        dfsAdmin.reencryptEncryptionZone(zone, CANCEL);
        getEzManager().resumeReencryptUpdaterForTesting();
        waitForZoneCompletes(zone.toString());
        Thread.sleep(3000);
        Assert.assertEquals(0, getZoneStatus(zone.toString()).getFilesReencrypted());
    }

    @Test
    public void testReencryptionWithoutProvider() throws Exception {
        /* Setup test dir:
        /zones/zone/[0-9]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        // re-encrypt the zone
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        // start NN without providers
        cluster.getConfiguration(0).unset(HADOOP_SECURITY_KEY_PROVIDER_PATH);
        cluster.restartNameNodes();
        cluster.waitClusterUp();
        // test re-encrypt should fail
        try {
            dfsAdmin.reencryptEncryptionZone(zone, START);
            Assert.fail("should not be able to re-encrypt");
        } catch (RemoteException expected) {
            GenericTestUtils.assertExceptionContains("rejected", expected.unwrapRemoteException());
        }
        try {
            dfsAdmin.reencryptEncryptionZone(zone, CANCEL);
            Assert.fail("should not be able to cancel re-encrypt");
        } catch (RemoteException expected) {
            GenericTestUtils.assertExceptionContains("rejected", expected.unwrapRemoteException());
        }
        // test listReencryptionStatus should still work
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(10, zs.getFilesReencrypted());
    }

    @Test
    public void testReencryptionNNSafeMode() throws Exception {
        /* Setup test dir:
        /zones/zone/[0-9]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        rollKey(TestReencryption.TEST_KEY);
        // mark pause after first checkpoint (5 files)
        getEzManager().pauseForTestingAfterNthSubmission(1);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedFiles(zone.toString(), 5);
        fs.setSafeMode(SAFEMODE_ENTER);
        getEzManager().resumeReencryptForTesting();
        for (int i = 0; i < 3; ++i) {
            Thread.sleep(1000);
            RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
            Assert.assertTrue(it.hasNext());
            ZoneReencryptionStatus zs = it.next();
            Assert.assertEquals(zone.toString(), zs.getZoneName());
            Assert.assertEquals(0, zs.getCompletionTime());
            Assert.assertEquals(5, zs.getFilesReencrypted());
        }
        fs.setSafeMode(SAFEMODE_LEAVE);
        // trigger the background thread to run, without having to
        // wait for DFS_NAMENODE_REENCRYPT_SLEEP_INTERVAL_KEY
        getHandler().notifyNewSubmission();
        waitForReencryptedFiles(zone.toString(), 10);
    }

    @Test
    public void testReencryptionKMSDown() throws Exception {
        class MyInjector extends EncryptionFaultInjector {
            private volatile int exceptionCount = 0;

            MyInjector(int numFailures) {
                exceptionCount = numFailures;
            }

            @Override
            public synchronized void reencryptEncryptedKeys() throws IOException {
                if ((exceptionCount) > 0) {
                    --(exceptionCount);
                    throw new IOException("Injected KMS failure");
                }
            }
        }
        final MyInjector injector = new MyInjector(1);
        instance = injector;
        /* Setup test dir:
        /zones/zone/[0-9]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        // re-encrypt the zone
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        Assert.assertEquals(0, injector.exceptionCount);
        // test listReencryptionStatus should still work
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(5, zs.getFilesReencrypted());
        Assert.assertEquals(5, zs.getNumReencryptionFailures());
    }

    @Test
    public void testReencryptionUpdaterFaultOneTask() throws Exception {
        class MyInjector extends EncryptionFaultInjector {
            private volatile int exceptionCount = 0;

            MyInjector(int numFailures) {
                exceptionCount = numFailures;
            }

            @Override
            public synchronized void reencryptUpdaterProcessOneTask() throws IOException {
                if ((exceptionCount) > 0) {
                    --(exceptionCount);
                    throw new IOException("Injected process task failure");
                }
            }
        }
        final MyInjector injector = new MyInjector(1);
        instance = injector;
        /* Setup test dir:
        /zones/zone/[0-9]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        // re-encrypt the zone
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        Assert.assertEquals(0, injector.exceptionCount);
        // test listReencryptionStatus should still work
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(5, zs.getFilesReencrypted());
        Assert.assertEquals(1, zs.getNumReencryptionFailures());
    }

    @Test
    public void testReencryptionUpdaterFaultCkpt() throws Exception {
        class MyInjector extends EncryptionFaultInjector {
            private volatile int exceptionCount = 0;

            MyInjector(int numFailures) {
                exceptionCount = numFailures;
            }

            @Override
            public synchronized void reencryptUpdaterProcessCheckpoint() throws IOException {
                if ((exceptionCount) > 0) {
                    --(exceptionCount);
                    throw new IOException("Injected process checkpoint failure");
                }
            }
        }
        final MyInjector injector = new MyInjector(1);
        instance = injector;
        /* Setup test dir:
        /zones/zone/[0-9]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        // re-encrypt the zone
        rollKey(TestReencryption.TEST_KEY);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        Assert.assertEquals(0, injector.exceptionCount);
        // test listReencryptionStatus should still work
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(10, zs.getFilesReencrypted());
        Assert.assertEquals(1, zs.getNumReencryptionFailures());
    }

    @Test
    public void testReencryptionUpdaterFaultRecover() throws Exception {
        class MyInjector extends EncryptionFaultInjector {
            private volatile int exceptionCount = 0;

            MyInjector(int oneTask) {
                exceptionCount = oneTask;
            }

            @Override
            public synchronized void reencryptUpdaterProcessOneTask() throws IOException {
                if ((exceptionCount) > 0) {
                    --(exceptionCount);
                    throw new RetriableException("Injected process task failure");
                }
            }
        }
        final MyInjector injector = new MyInjector(10);
        instance = injector;
        /* Setup test dir:
        /zones/zone/[0-9]
         */
        final int len = 8196;
        final Path zoneParent = new Path("/zones");
        final Path zone = new Path(zoneParent, "zone");
        fsWrapper.mkdir(zone, FsPermission.getDirDefault(), true);
        dfsAdmin.createEncryptionZone(zone, TestReencryption.TEST_KEY, TestReencryption.NO_TRASH);
        for (int i = 0; i < 10; ++i) {
            DFSTestUtil.createFile(fs, new Path(zone, Integer.toString(i)), len, ((short) (1)), 65261);
        }
        // re-encrypt the zone
        rollKey(TestReencryption.TEST_KEY);
        Whitebox.setInternalState(getUpdater(), "faultRetryInterval", 50);
        dfsAdmin.reencryptEncryptionZone(zone, START);
        waitForReencryptedZones(1);
        Assert.assertEquals(0, injector.exceptionCount);
        // test listReencryptionStatus should still work
        RemoteIterator<ZoneReencryptionStatus> it = dfsAdmin.listReencryptionStatus();
        Assert.assertTrue(it.hasNext());
        ZoneReencryptionStatus zs = it.next();
        Assert.assertEquals(zone.toString(), zs.getZoneName());
        Assert.assertEquals(Completed, zs.getState());
        verifyZoneCompletionTime(zs);
        Assert.assertEquals(10, zs.getFilesReencrypted());
        Assert.assertEquals(0, zs.getNumReencryptionFailures());
    }
}

