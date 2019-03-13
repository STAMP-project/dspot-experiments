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
package org.apache.hadoop.fs.viewfs;


import CreateEncryptionZoneFlag.PROVISION_TRASH;
import NflyFSystem.NflyKey.readMostRecent;
import NflyFSystem.NflyKey.repairOnRead;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.CreateEncryptionZoneFlag;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestViewFileSystemHdfs extends ViewFileSystemBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestViewFileSystemHdfs.class);

    private static MiniDFSCluster cluster;

    private static Path defaultWorkingDirectory;

    private static Path defaultWorkingDirectory2;

    private static final Configuration CONF = new Configuration();

    private static FileSystem fHdfs;

    private static FileSystem fHdfs2;

    private FileSystem fsTarget2;

    Path targetTestRoot2;

    @Test
    public void testTrashRootsAfterEncryptionZoneDeletion() throws Exception {
        final Path zone = new Path("/EZ");
        fsTarget.mkdirs(zone);
        final Path zone1 = new Path("/EZ/zone1");
        fsTarget.mkdirs(zone1);
        DFSTestUtil.createKey("test_key", TestViewFileSystemHdfs.cluster, TestViewFileSystemHdfs.CONF);
        HdfsAdmin hdfsAdmin = new HdfsAdmin(TestViewFileSystemHdfs.cluster.getURI(0), TestViewFileSystemHdfs.CONF);
        final EnumSet<CreateEncryptionZoneFlag> provisionTrash = EnumSet.of(PROVISION_TRASH);
        hdfsAdmin.createEncryptionZone(zone1, "test_key", provisionTrash);
        final Path encFile = new Path(zone1, "encFile");
        DFSTestUtil.createFile(fsTarget, encFile, 10240, ((short) (1)), 65261);
        Configuration clientConf = new Configuration(TestViewFileSystemHdfs.CONF);
        clientConf.setLong(CommonConfigurationKeysPublic.FS_TRASH_INTERVAL_KEY, 1);
        clientConf.set("fs.default.name", fsTarget.getUri().toString());
        FsShell shell = new FsShell(clientConf);
        // Verify file deletion within EZ
        DFSTestUtil.verifyDelete(shell, fsTarget, encFile, true);
        Assert.assertTrue("ViewFileSystem trash roots should include EZ file trash", ((fsView.getTrashRoots(true).size()) == 1));
        // Verify deletion of EZ
        DFSTestUtil.verifyDelete(shell, fsTarget, zone, true);
        Assert.assertTrue("ViewFileSystem trash roots should include EZ zone trash", ((fsView.getTrashRoots(true).size()) == 2));
    }

    @Test
    public void testDf() throws Exception {
        Configuration newConf = new Configuration(conf);
        // Verify if DF on non viewfs produces output as before, that is
        // without "Mounted On" header.
        DFSTestUtil.FsShellRun("-df", 0, ("Use%" + (System.lineSeparator())), newConf);
        // Setting the default Fs to viewfs
        newConf.set("fs.default.name", "viewfs:///");
        // Verify if DF on viewfs produces a new header "Mounted on"
        DFSTestUtil.FsShellRun("-df /user", 0, "Mounted on", newConf);
        DFSTestUtil.FsShellRun("-df viewfs:///user", 0, "/user", newConf);
        DFSTestUtil.FsShellRun("-df /user3", 1, "/user3", newConf);
        DFSTestUtil.FsShellRun("-df /user2/abc", 1, "No such file or directory", newConf);
        DFSTestUtil.FsShellRun("-df /user2/", 0, "/user2", newConf);
        DFSTestUtil.FsShellRun("-df /internalDir", 0, "/internalDir", newConf);
        DFSTestUtil.FsShellRun("-df /", 0, null, newConf);
        DFSTestUtil.FsShellRun("-df", 0, null, newConf);
    }

    @Test
    public void testFileChecksum() throws IOException {
        ViewFileSystem viewFs = ((ViewFileSystem) (fsView));
        Path mountDataRootPath = new Path("/data");
        String fsTargetFileName = "debug.log";
        Path fsTargetFilePath = new Path(targetTestRoot, "data/debug.log");
        Path mountDataFilePath = new Path(mountDataRootPath, fsTargetFileName);
        fileSystemTestHelper.createFile(fsTarget, fsTargetFilePath);
        FileStatus fileStatus = viewFs.getFileStatus(mountDataFilePath);
        long fileLength = fileStatus.getLen();
        FileChecksum fileChecksumViaViewFs = viewFs.getFileChecksum(mountDataFilePath);
        FileChecksum fileChecksumViaTargetFs = fsTarget.getFileChecksum(fsTargetFilePath);
        Assert.assertTrue("File checksum not matching!", fileChecksumViaViewFs.equals(fileChecksumViaTargetFs));
        fileChecksumViaViewFs = viewFs.getFileChecksum(mountDataFilePath, (fileLength / 2));
        fileChecksumViaTargetFs = fsTarget.getFileChecksum(fsTargetFilePath, (fileLength / 2));
        Assert.assertTrue("File checksum not matching!", fileChecksumViaViewFs.equals(fileChecksumViaTargetFs));
    }

    // Rename should fail on across different fileSystems
    @Test
    public void testRenameAccorssFilesystem() throws IOException {
        // data is mountpoint in nn1
        Path mountDataRootPath = new Path("/data");
        // mountOnNn2 is nn2 mountpoint
        Path fsTargetFilePath = new Path("/mountOnNn2");
        Path filePath = new Path((mountDataRootPath + "/ttest"));
        Path hdfFilepath = new Path((fsTargetFilePath + "/ttest2"));
        fsView.create(filePath);
        try {
            fsView.rename(filePath, hdfFilepath);
            ContractTestUtils.fail("Should thrown IOE on Renames across filesytems");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Renames across Mount points not supported", e);
        }
    }

    @Test
    public void testNflyClosestRepair() throws Exception {
        testNflyRepair(repairOnRead);
    }

    @Test
    public void testNflyMostRecentRepair() throws Exception {
        testNflyRepair(readMostRecent);
    }
}

