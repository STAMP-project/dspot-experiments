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


import PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT;
import alluxio.hadoop.FileSystem;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.underfs.UnderFileSystem;
import alluxio.util.io.PathUtils;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Integration tests for {@link FileSystem#rename(Path, Path)}.
 */
// TODO(jiri): Test persisting rename operations to UFS.
public final class FileSystemRenameIntegrationTest extends BaseIntegrationTest {
    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_FILE_WRITE_TYPE_DEFAULT, "CACHE_THROUGH").build();

    private static String sUfsRoot;

    private static UnderFileSystem sUfs;

    private static FileSystem sTFS;

    @Test
    public void basicRenameTest1() throws Exception {
        // Rename /fileA to /fileB
        Path fileA = new Path("/fileA");
        Path fileB = new Path("/fileB");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.rename(fileA, fileB));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(fileB));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileB")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileB));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileB")));
    }

    @Test
    public void basicRenameTest2() throws Exception {
        // Rename /fileA to /dirA/fileA
        Path fileA = new Path("/fileA");
        Path dirA = new Path("/dirA");
        Path finalDst = new Path("/dirA/fileA");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        FileSystemRenameIntegrationTest.sTFS.mkdirs(dirA);
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.rename(fileA, finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA", "fileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
    }

    @Test
    public void basicRenameTest3() throws Exception {
        // Rename /fileA to /dirA/fileA without specifying the full path
        Path fileA = new Path("/fileA");
        Path dirA = new Path("/dirA");
        Path finalDst = new Path("/dirA/fileA");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        FileSystemRenameIntegrationTest.sTFS.mkdirs(dirA);
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.rename(fileA, dirA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA", "fileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
    }

    @Test
    public void basicRenameTest4() throws Exception {
        // Rename /fileA to /fileA
        Path fileA = new Path("/fileA");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.rename(fileA, fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
    }

    @Test
    public void basicRenameTest5() throws Exception {
        // Rename /fileA to /fileAfileA
        Path fileA = new Path("/fileA");
        Path finalDst = new Path("/fileAfileA");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.rename(fileA, finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileAfileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileAfileA")));
    }

    @Test
    public void basicRenameTest6() throws Exception {
        // Rename /dirA to /dirB, /dirA/fileA should become /dirB/fileA
        Path dirA = new Path("/dirA");
        Path dirB = new Path("/dirB");
        Path fileA = new Path("/dirA/fileA");
        Path finalDst = new Path("/dirB/fileA");
        FileSystemRenameIntegrationTest.sTFS.mkdirs(dirA);
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.rename(dirA, dirB));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(dirB));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA", "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirB")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirB", "fileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(dirB));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirB")));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirB", "fileA")));
    }

    @Test
    public void errorRenameTest1() throws Exception {
        // Rename /dirA to /dirA/dirB should fail
        Path dirA = new Path("/dirA");
        Path finalDst = new Path("/dirA/dirB");
        FileSystemRenameIntegrationTest.sTFS.mkdirs(dirA);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.rename(dirA, finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA", "dirB")));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirB")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirB")));
    }

    @Test
    public void errorRenameTest2() throws Exception {
        // Rename /fileA to /fileB should fail if /fileB exists
        Path fileA = new Path("/fileA");
        Path fileB = new Path("/fileB");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileB);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.rename(fileA, fileB));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(fileB));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileB")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileB));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileB")));
    }

    @Test
    public void errorRenameTest3() throws Exception {
        // Rename /fileA to /dirA/fileA should fail if /dirA/fileA exists
        Path fileA = new Path("/fileA");
        Path dirA = new Path("/dirA");
        Path finalDst = new Path("/dirA/fileA");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, finalDst);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.rename(fileA, dirA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA", "fileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(dirA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(finalDst));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isDirectory(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA")));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "dirA", "fileA")));
    }

    @Test
    public void errorRenameTest4() throws Exception {
        // Rename /fileA to an nonexistent path should fail
        Path fileA = new Path("/fileA");
        Path nonexistentPath = new Path("/doesNotExist/fileA");
        FileSystemRenameIntegrationTest.create(FileSystemRenameIntegrationTest.sTFS, fileA);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.rename(fileA, nonexistentPath));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertTrue(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
        FileSystemRenameIntegrationTest.cleanup(FileSystemRenameIntegrationTest.sTFS);
        Assert.assertFalse(FileSystemRenameIntegrationTest.sTFS.exists(fileA));
        Assert.assertFalse(FileSystemRenameIntegrationTest.sUfs.isFile(PathUtils.concatPath(FileSystemRenameIntegrationTest.sUfsRoot, "fileA")));
    }
}

