/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.hadoop;


import AuthType.SIMPLE;
import Constants.DEFAULT_FILE_SYSTEM_MODE;
import PropertyKey.SECURITY_AUTHENTICATION_TYPE;
import PropertyKey.SECURITY_AUTHORIZATION_PERMISSION_ENABLED;
import PropertyKey.UNDERFS_GCS_OWNER_ID_TO_USERNAME_MAPPING;
import PropertyKey.UNDERFS_S3_OWNER_ID_TO_USERNAME_MAPPING;
import PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT;
import alluxio.conf.ServerConfiguration;
import alluxio.hadoop.FileSystem;
import alluxio.hadoop.HadoopClientTestUtils;
import alluxio.security.authorization.Mode;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.underfs.UfsStatus;
import alluxio.underfs.UnderFileSystem;
import alluxio.underfs.options.CreateOptions;
import alluxio.underfs.options.MkdirsOptions;
import alluxio.util.UnderFileSystemUtils;
import alluxio.util.io.PathUtils;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration tests for {@link FileSystem#setOwner(Path, String, String)} and
 * {@link FileSystem#setPermission(Path, org.apache.hadoop.fs.permission.FsPermission)}.
 */
public final class FileSystemAclIntegrationTest extends BaseIntegrationTest {
    /**
     * The exception expected to be thrown.
     */
    @Rule
    public final ExpectedException mThrown = ExpectedException.none();

    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(SECURITY_AUTHENTICATION_TYPE, SIMPLE.getAuthName()).setProperty(SECURITY_AUTHORIZATION_PERMISSION_ENABLED, "true").setProperty(USER_FILE_WRITE_TYPE_DEFAULT, "CACHE_THROUGH").build();

    private static String sUfsRoot;

    private static UnderFileSystem sUfs;

    private static FileSystem sTFS;

    @Test
    public void createFileWithPermission() throws Exception {
        List<Integer> permissionValues = Lists.newArrayList(73, 146, 219, 292, 365, 438, 511, 493, 475, 420, 347, 329);
        for (int value : permissionValues) {
            Path file = new Path(("/createfile" + value));
            FsPermission permission = FsPermission.createImmutable(((short) (value)));
            FSDataOutputStream o = /* ignored */
            /* ignored */
            /* ignored */
            /* ignored */
            /* ignored */
            FileSystemAclIntegrationTest.sTFS.create(file, permission, false, 10, ((short) (1)), 512, null);
            o.writeBytes("Test Bytes");
            o.close();
            FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(file);
            Assert.assertEquals(permission, fs.getPermission());
        }
    }

    @Test
    public void mkdirsWithPermission() throws Exception {
        List<Integer> permissionValues = Lists.newArrayList(73, 146, 219, 292, 365, 438, 511, 493, 475, 420, 347, 329);
        for (int value : permissionValues) {
            Path dir = new Path(("/createDir" + value));
            FsPermission permission = FsPermission.createImmutable(((short) (value)));
            FileSystemAclIntegrationTest.sTFS.mkdirs(dir, permission);
            FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(dir);
            Assert.assertEquals(permission, fs.getPermission());
        }
    }

    /**
     * Test for {@link FileSystem#setPermission(Path, org.apache.hadoop.fs.permission.FsPermission)}.
     * It will test changing the permission of file using TFS.
     */
    @Test
    public void chmod() throws Exception {
        Path fileA = new Path("/chmodfileA");
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileA);
        Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)));
        if ((UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs)) && (HadoopClientTestUtils.isHadoop1x())) {
            // If the UFS is hadoop 1.0, the org.apache.hadoop.fs.FileSystem.create uses default
            // permission option 0777.
            Assert.assertEquals(((short) (511)), fs.getPermission().toShort());
        } else {
            // Default permission should be 0644.
            Assert.assertEquals(((short) (420)), fs.getPermission().toShort());
        }
        FileSystemAclIntegrationTest.sTFS.setPermission(fileA, FsPermission.createImmutable(((short) (493))));
        Assert.assertEquals(((short) (493)), FileSystemAclIntegrationTest.sTFS.getFileStatus(fileA).getPermission().toShort());
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)} with local UFS. It will test only
     * changing the owner of file using TFS and propagate the change to UFS. Since the arbitrary
     * owner does not exist in the local UFS, the operation would fail.
     */
    @Test
    public void changeNonexistentOwnerForLocal() throws Exception {
        // Skip non-local UFSs.
        Assume.assumeTrue(UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs));
        Path fileA = new Path("/chownfileA-local");
        final String nonexistentOwner = "nonexistent-user1";
        final String nonexistentGroup = "nonexistent-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileA);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        Assert.assertEquals(defaultOwner, FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)).getOwner());
        // Group can different because local FS user to group mapping can be different from that
        // in Alluxio.
        Assert.assertNotEquals(defaultOwner, nonexistentOwner);
        Assert.assertNotEquals(defaultGroup, nonexistentGroup);
        // Expect a IOException for not able to setOwner for UFS with invalid owner name.
        mThrown.expect(IOException.class);
        mThrown.expectMessage("Could not setOwner for UFS file");
        FileSystemAclIntegrationTest.sTFS.setOwner(fileA, nonexistentOwner, null);
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)} with local UFS. It will test only
     * changing the group of file using TFS and propagate the change to UFS. Since the arbitrary
     * group does not exist in the local UFS, the operation would fail.
     */
    @Test
    public void changeNonexistentGroupForLocal() throws Exception {
        // Skip non-local UFSs.
        Assume.assumeTrue(UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs));
        Path fileB = new Path("/chownfileB-local");
        final String nonexistentOwner = "nonexistent-user1";
        final String nonexistentGroup = "nonexistent-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileB);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileB);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        Assert.assertEquals(defaultOwner, FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileB)).getOwner());
        // Group can different because local FS user to group mapping can be different from that
        // in Alluxio.
        Assert.assertNotEquals(defaultOwner, nonexistentOwner);
        Assert.assertNotEquals(defaultGroup, nonexistentGroup);
        // Expect a IOException for not able to setOwner for UFS with invalid group name.
        mThrown.expect(IOException.class);
        mThrown.expectMessage("Could not setOwner for UFS file");
        FileSystemAclIntegrationTest.sTFS.setOwner(fileB, null, nonexistentGroup);
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)} with local UFS. It will test
     * changing both owner and group of file using TFS and propagate the change to UFS. Since the
     * arbitrary owner and group do not exist in the local UFS, the operation would fail.
     */
    @Test
    public void changeNonexistentOwnerAndGroupForLocal() throws Exception {
        // Skip non-local UFSs.
        Assume.assumeTrue(UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs));
        Path fileC = new Path("/chownfileC-local");
        final String nonexistentOwner = "nonexistent-user1";
        final String nonexistentGroup = "nonexistent-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileC);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileC);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        Assert.assertEquals(defaultOwner, FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileC)).getOwner());
        // Group can different because local FS user to group mapping can be different from that
        // in Alluxio.
        Assert.assertNotEquals(defaultOwner, nonexistentOwner);
        Assert.assertNotEquals(defaultGroup, nonexistentGroup);
        mThrown.expect(IOException.class);
        mThrown.expectMessage("Could not update owner");
        FileSystemAclIntegrationTest.sTFS.setOwner(fileC, nonexistentOwner, nonexistentGroup);
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)} with HDFS UFS. It will test only
     * changing the owner of file using TFS and propagate the change to UFS.
     */
    @Test
    public void changeNonexistentOwnerForHdfs() throws Exception {
        // Skip non-HDFS UFSs.
        Assume.assumeTrue(UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs));
        Path fileA = new Path("/chownfileA-hdfs");
        final String testOwner = "test-user1";
        final String testGroup = "test-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileA);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        Assert.assertEquals(defaultOwner, FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)).getOwner());
        // Group can different because HDFS user to group mapping can be different from that in Alluxio.
        Assert.assertNotEquals(defaultOwner, testOwner);
        Assert.assertNotEquals(defaultGroup, testGroup);
        // Expect a IOException for not able to setOwner for UFS with invalid owner name.
        FileSystemAclIntegrationTest.sTFS.setOwner(fileA, testOwner, null);
        fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileA);
        Assert.assertEquals(testOwner, fs.getOwner());
        Assert.assertEquals(defaultGroup, fs.getGroup());
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA));
        Assert.assertEquals(testOwner, ufsStatus.getOwner());
        Assert.assertEquals(defaultGroup, ufsStatus.getGroup());
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)} with HDFS UFS. It will test only
     * changing the group of file using TFS and propagate the change to UFS.
     */
    @Test
    public void changeNonexistentGroupForHdfs() throws Exception {
        // Skip non-HDFS UFSs.
        Assume.assumeTrue(UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs));
        Path fileB = new Path("/chownfileB-hdfs");
        final String testOwner = "test-user1";
        final String testGroup = "test-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileB);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileB);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        Assert.assertEquals(defaultOwner, FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileB)).getOwner());
        // Group can different because HDFS user to group mapping can be different from that in Alluxio.
        Assert.assertNotEquals(defaultOwner, testOwner);
        Assert.assertNotEquals(defaultGroup, testGroup);
        FileSystemAclIntegrationTest.sTFS.setOwner(fileB, null, testGroup);
        fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileB);
        Assert.assertEquals(defaultOwner, fs.getOwner());
        Assert.assertEquals(testGroup, fs.getGroup());
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileB));
        Assert.assertEquals(defaultOwner, ufsStatus.getOwner());
        Assert.assertEquals(testGroup, ufsStatus.getGroup());
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)} with HDFS UFS. It will test
     * changing both owner and group of file using TFS and propagate the change to UFS.
     */
    @Test
    public void changeNonexistentOwnerAndGroupForHdfs() throws Exception {
        // Skip non-HDFS UFSs.
        Assume.assumeTrue(UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs));
        Path fileC = new Path("/chownfileC-hdfs");
        final String testOwner = "test-user1";
        final String testGroup = "test-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileC);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileC);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        Assert.assertEquals(defaultOwner, FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileC)).getOwner());
        // Group can different because HDFS user to group mapping can be different from that in Alluxio.
        Assert.assertNotEquals(defaultOwner, testOwner);
        Assert.assertNotEquals(defaultGroup, testGroup);
        FileSystemAclIntegrationTest.sTFS.setOwner(fileC, testOwner, testGroup);
        fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileC);
        Assert.assertEquals(testOwner, fs.getOwner());
        Assert.assertEquals(testGroup, fs.getGroup());
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileC));
        Assert.assertEquals(testOwner, ufsStatus.getOwner());
        Assert.assertEquals(testGroup, ufsStatus.getGroup());
    }

    /**
     * Test for {@link FileSystem#setOwner(Path, String, String)}. It will test both owner and group
     * are null.
     */
    @Test
    public void checkNullOwnerAndGroup() throws Exception {
        Path fileD = new Path("/chownfileD");
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileD);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileD);
        String defaultOwner = fs.getOwner();
        String defaultGroup = fs.getGroup();
        FileSystemAclIntegrationTest.sTFS.setOwner(fileD, null, null);
        fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(fileD);
        Assert.assertEquals(defaultOwner, fs.getOwner());
        Assert.assertEquals(defaultGroup, fs.getGroup());
    }

    /**
     * Tests the directory permission propagation to UFS.
     */
    @Test
    public void directoryPermissionForUfs() throws IOException {
        // Skip non-local and non-HDFS UFSs.
        Assume.assumeTrue(((UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs)) || (UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs))));
        Path dir = new Path("/root/directoryPermissionForUfsDir");
        FileSystemAclIntegrationTest.sTFS.mkdirs(dir);
        FileStatus fs = FileSystemAclIntegrationTest.sTFS.getFileStatus(dir);
        String defaultOwner = fs.getOwner();
        Short dirMode = fs.getPermission().toShort();
        FileStatus parentFs = FileSystemAclIntegrationTest.sTFS.getFileStatus(dir.getParent());
        Short parentMode = parentFs.getPermission().toShort();
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getDirectoryStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, dir));
        Assert.assertEquals(defaultOwner, ufsStatus.getOwner());
        Assert.assertEquals(((int) (dirMode)), ((int) (ufsStatus.getMode())));
        Assert.assertEquals(((int) (parentMode)), ((int) (FileSystemAclIntegrationTest.sUfs.getDirectoryStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, dir.getParent())).getMode())));
        short newMode = ((short) (493));
        FsPermission newPermission = new FsPermission(newMode);
        FileSystemAclIntegrationTest.sTFS.setPermission(dir, newPermission);
        Assert.assertEquals(((int) (newMode)), ((int) (FileSystemAclIntegrationTest.sUfs.getDirectoryStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, dir)).getMode())));
    }

    /**
     * Tests the parent directory permission when mkdirs recursively.
     */
    @Test
    public void parentDirectoryPermissionForUfs() throws IOException {
        // Skip non-local and non-HDFS UFSs.
        Assume.assumeTrue(((UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs)) || (UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs))));
        String path = "/root/parentDirectoryPermissionForUfsDir/parentDirectoryPermissionForUfsFile";
        Path fileA = new Path(path);
        Path dirA = fileA.getParent();
        FileSystemAclIntegrationTest.sTFS.mkdirs(dirA);
        short parentMode = ((short) (448));
        FsPermission newPermission = new FsPermission(parentMode);
        FileSystemAclIntegrationTest.sTFS.setPermission(dirA, newPermission);
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        Assert.assertEquals(((int) (parentMode)), ((int) (FileSystemAclIntegrationTest.sUfs.getDirectoryStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, dirA)).getMode())));
        // Rename from dirA to dirB, file and its parent permission should be in sync with the source
        // dirA.
        Path fileB = new Path("/root/dirB/fileB");
        Path dirB = fileB.getParent();
        FileSystemAclIntegrationTest.sTFS.rename(dirA, dirB);
        Assert.assertEquals(((int) (parentMode)), ((int) (FileSystemAclIntegrationTest.sUfs.getDirectoryStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileB.getParent())).getMode())));
    }

    /**
     * Tests the loaded file metadata from UFS having the same mode as that in the UFS.
     */
    @Test
    public void loadFileMetadataMode() throws Exception {
        // Skip non-local and non-HDFS-2 UFSs.
        Assume.assumeTrue(((UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs)) || ((UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs)) && (HadoopClientTestUtils.isHadoop2x()))));
        List<Integer> permissionValues = Lists.newArrayList(73, 146, 219, 292, 365, 438, 511, 493, 475, 420, 347, 329);
        for (int value : permissionValues) {
            Path file = new Path(("/loadFileMetadataMode" + value));
            FileSystemAclIntegrationTest.sTFS.delete(file, false);
            // Create a file directly in UFS and set the corresponding mode.
            String ufsPath = PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, file);
            FileSystemAclIntegrationTest.sUfs.create(ufsPath, CreateOptions.defaults(ServerConfiguration.global()).setOwner("testuser").setGroup("testgroup").setMode(new Mode(((short) (value))))).close();
            Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, file)));
            // Check the mode is consistent in Alluxio namespace once it's loaded from UFS to Alluxio.
            Assert.assertEquals(new Mode(((short) (value))).toString(), new Mode(FileSystemAclIntegrationTest.sTFS.getFileStatus(file).getPermission().toShort()).toString());
        }
    }

    /**
     * Tests the loaded directory metadata from UFS having the same mode as that in the UFS.
     */
    @Test
    public void loadDirMetadataMode() throws Exception {
        // Skip non-local and non-HDFS UFSs.
        Assume.assumeTrue(((UnderFileSystemUtils.isLocal(FileSystemAclIntegrationTest.sUfs)) || (UnderFileSystemUtils.isHdfs(FileSystemAclIntegrationTest.sUfs))));
        List<Integer> permissionValues = Lists.newArrayList(73, 146, 219, 292, 365, 438, 511, 493, 475, 420, 347, 329);
        for (int value : permissionValues) {
            Path dir = new Path((("/loadDirMetadataMode" + value) + "/"));
            FileSystemAclIntegrationTest.sTFS.delete(dir, true);
            // Create a directory directly in UFS and set the corresponding mode.
            String ufsPath = PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, dir);
            FileSystemAclIntegrationTest.sUfs.mkdirs(ufsPath, MkdirsOptions.defaults(ServerConfiguration.global()).setCreateParent(false).setOwner("testuser").setGroup("testgroup").setMode(new Mode(((short) (value)))));
            Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, dir)));
            // Check the mode is consistent in Alluxio namespace once it's loaded from UFS to Alluxio.
            Assert.assertEquals(new Mode(((short) (value))).toString(), new Mode(FileSystemAclIntegrationTest.sTFS.getFileStatus(dir).getPermission().toShort()).toString());
        }
    }

    @Test
    public void s3GetPermission() throws Exception {
        Assume.assumeTrue(UnderFileSystemUtils.isS3(FileSystemAclIntegrationTest.sUfs));
        ServerConfiguration.unset(UNDERFS_S3_OWNER_ID_TO_USERNAME_MAPPING);
        Path fileA = new Path("/s3GetPermissionFile");
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)));
        // Without providing "alluxio.underfs.s3.canonical.owner.id.to.username.mapping", the default
        // display name of the S3 owner account is NOT empty.
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA));
        Assert.assertNotEquals("", ufsStatus.getOwner());
        Assert.assertNotEquals("", ufsStatus.getGroup());
        Assert.assertEquals(((short) (448)), ufsStatus.getMode());
    }

    @Test
    public void gcsGetPermission() throws Exception {
        Assume.assumeTrue(UnderFileSystemUtils.isGcs(FileSystemAclIntegrationTest.sUfs));
        ServerConfiguration.unset(UNDERFS_GCS_OWNER_ID_TO_USERNAME_MAPPING);
        Path fileA = new Path("/gcsGetPermissionFile");
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)));
        // Without providing "alluxio.underfs.gcs.owner.id.to.username.mapping", the default
        // display name of the GCS owner account is empty. The owner will be the GCS account id, which
        // is not empty.
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA));
        Assert.assertNotEquals("", ufsStatus.getOwner());
        Assert.assertNotEquals("", ufsStatus.getGroup());
        Assert.assertEquals(((short) (448)), ufsStatus.getMode());
    }

    @Test
    public void swiftGetPermission() throws Exception {
        Assume.assumeTrue(UnderFileSystemUtils.isSwift(FileSystemAclIntegrationTest.sUfs));
        Path fileA = new Path("/swiftGetPermissionFile");
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)));
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA));
        Assert.assertNotEquals("", ufsStatus.getOwner());
        Assert.assertNotEquals("", ufsStatus.getGroup());
        Assert.assertEquals(((short) (448)), ufsStatus.getMode());
    }

    @Test
    public void ossGetPermission() throws Exception {
        Assume.assumeTrue(UnderFileSystemUtils.isOss(FileSystemAclIntegrationTest.sUfs));
        Path fileA = new Path("/objectfileA");
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemAclIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA)));
        // Verify the owner, group and permission of OSS UFS is not supported and thus returns default
        // values.
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA));
        Assert.assertNotEquals("", ufsStatus.getOwner());
        Assert.assertNotEquals("", ufsStatus.getGroup());
        Assert.assertEquals(DEFAULT_FILE_SYSTEM_MODE, ufsStatus.getMode());
    }

    @Test
    public void objectStoreSetOwner() throws Exception {
        Assume.assumeTrue(FileSystemAclIntegrationTest.sUfs.isObjectStorage());
        Path fileA = new Path("/objectfileA");
        final String newOwner = "new-user1";
        final String newGroup = "new-group1";
        FileSystemAclIntegrationTest.create(FileSystemAclIntegrationTest.sTFS, fileA);
        // Set owner to Alluxio files that are persisted in UFS will NOT propagate to underlying object.
        FileSystemAclIntegrationTest.sTFS.setOwner(fileA, newOwner, newGroup);
        UfsStatus ufsStatus = FileSystemAclIntegrationTest.sUfs.getFileStatus(PathUtils.concatPath(FileSystemAclIntegrationTest.sUfsRoot, fileA));
        Assert.assertNotEquals("", ufsStatus.getOwner());
        Assert.assertNotEquals("", ufsStatus.getGroup());
    }
}

