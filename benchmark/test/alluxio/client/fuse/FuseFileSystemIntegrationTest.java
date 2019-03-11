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
package alluxio.client.fuse;


import PropertyKey.FUSE_USER_GROUP_TRANSLATION_ENABLED;
import PropertyKey.USER_BLOCK_SIZE_BYTES_DEFAULT;
import ReadPType.NO_CACHE;
import WritePType.MUST_CACHE;
import alluxio.AlluxioURI;
import alluxio.Constants;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileOutStream;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemTestUtils;
import alluxio.fuse.AlluxioFuseFileSystem;
import alluxio.fuse.AlluxioFuseUtils;
import alluxio.grpc.OpenFilePOptions;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.OSUtils;
import alluxio.util.ShellUtils;
import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Integration tests for {@link AlluxioFuseFileSystem}.
 */
public class FuseFileSystemIntegrationTest {
    private static final int WAIT_TIMEOUT_MS = 60 * (Constants.SECOND_MS);

    private static final int BLOCK_SIZE = 4 * (Constants.KB);

    // Fuse user group translation needs to be enabled to support chown/chgrp/ls commands
    // to show accurate information
    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(FUSE_USER_GROUP_TRANSLATION_ENABLED, true).setProperty(USER_BLOCK_SIZE_BYTES_DEFAULT, FuseFileSystemIntegrationTest.BLOCK_SIZE).build();

    private static String sAlluxioRoot;

    private static boolean sFuseInstalled;

    private static FileSystem sFileSystem;

    private static AlluxioFuseFileSystem sFuseFileSystem;

    private static Thread sFuseThread;

    private static String sMountPoint;

    @Test
    public void cat() throws Exception {
        String testFile = "/catTestFile";
        String content = "Alluxio Cat Test File Content";
        try (FileOutStream os = FuseFileSystemIntegrationTest.sFileSystem.createFile(new AlluxioURI(testFile))) {
            os.write(content.getBytes());
        }
        String result = ShellUtils.execCommand("cat", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals((content + "\n"), result);
    }

    @Test
    public void chgrp() throws Exception {
        String testFile = "/chgrpTestFile";
        String userName = System.getProperty("user.name");
        String groupName = AlluxioFuseUtils.getGroupName(userName);
        FileSystemTestUtils.createByteFile(FuseFileSystemIntegrationTest.sFileSystem, testFile, MUST_CACHE, 10);
        ShellUtils.execCommand("chgrp", groupName, ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals(groupName, FuseFileSystemIntegrationTest.sFileSystem.getStatus(new AlluxioURI(testFile)).getGroup());
    }

    @Test
    public void chmod() throws Exception {
        String testFile = "/chmodTestFile";
        FileSystemTestUtils.createByteFile(FuseFileSystemIntegrationTest.sFileSystem, testFile, MUST_CACHE, 10);
        ShellUtils.execCommand("chmod", "777", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals(((short) (511)), FuseFileSystemIntegrationTest.sFileSystem.getStatus(new AlluxioURI(testFile)).getMode());
    }

    @Test
    public void chown() throws Exception {
        String testFile = "/chownTestFile";
        FileSystemTestUtils.createByteFile(FuseFileSystemIntegrationTest.sFileSystem, testFile, MUST_CACHE, 10);
        String userName = System.getProperty("user.name");
        String groupName = AlluxioFuseUtils.getGroupName(userName);
        ShellUtils.execCommand("chown", ((userName + ":") + groupName), ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals(userName, FuseFileSystemIntegrationTest.sFileSystem.getStatus(new AlluxioURI(testFile)).getOwner());
        Assert.assertEquals(groupName, FuseFileSystemIntegrationTest.sFileSystem.getStatus(new AlluxioURI(testFile)).getGroup());
    }

    @Test
    public void cp() throws Exception {
        String testFile = "/cpTestFile";
        String content = "Alluxio Cp Test File Content";
        File localFile = generateFileContent("/TestFileOnLocalPath", content.getBytes());
        ShellUtils.execCommand("cp", localFile.getPath(), ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertTrue(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI(testFile)));
        // Fuse release() is async
        // Cp again to make sure the first cp is completed
        String testFolder = "/cpTestFolder";
        ShellUtils.execCommand("mkdir", ((FuseFileSystemIntegrationTest.sMountPoint) + testFolder));
        ShellUtils.execCommand("cp", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile), (((FuseFileSystemIntegrationTest.sMountPoint) + testFolder) + testFile));
        Assert.assertTrue(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI((testFolder + testFile))));
        byte[] read = new byte[content.length()];
        try (FileInStream is = FuseFileSystemIntegrationTest.sFileSystem.openFile(new AlluxioURI(testFile), OpenFilePOptions.newBuilder().setReadType(NO_CACHE).build())) {
            is.read(read);
        }
        Assert.assertEquals(content, new String(read, "UTF8"));
    }

    @Test
    public void ddDuAndRm() throws Exception {
        String testFile = "/ddTestFile";
        createFileInFuse(testFile);
        // Fuse release() is async
        // Open the file to make sure dd is completed
        ShellUtils.execCommand("head", "-c", "10", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertTrue(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI(testFile)));
        Assert.assertEquals((40 * (Constants.KB)), FuseFileSystemIntegrationTest.sFileSystem.getStatus(new AlluxioURI(testFile)).getLength());
        String output = ShellUtils.execCommand("du", "-k", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals("40", output.split("\\s+")[0]);
        ShellUtils.execCommand("rm", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertFalse(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI(testFile)));
    }

    @Test
    public void head() throws Exception {
        String testFile = "/headTestFile";
        String content = "Alluxio Head Test File Content";
        try (FileOutStream os = FuseFileSystemIntegrationTest.sFileSystem.createFile(new AlluxioURI(testFile))) {
            os.write(content.getBytes());
        }
        String result = ShellUtils.execCommand("head", "-c", "17", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals("Alluxio Head Test\n", result);
    }

    @Test
    public void ls() throws Exception {
        // ls -sh has different results in osx
        Assume.assumeTrue(OSUtils.isLinux());
        String testFile = "/lsTestFile";
        createFileInFuse(testFile);
        // Fuse getattr() will wait for file to be completed
        // when fuse release returns but does not finish
        String out = ShellUtils.execCommand("ls", "-sh", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertFalse(out.isEmpty());
        Assert.assertEquals("40K", out.split("\\s+")[0]);
        Assert.assertTrue(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI(testFile)));
        Assert.assertEquals((40 * (Constants.KB)), FuseFileSystemIntegrationTest.sFileSystem.getStatus(new AlluxioURI(testFile)).getLength());
    }

    @Test
    public void mkdirAndMv() throws Exception {
        String testFile = "/mvTestFile";
        String testFolder = "/mkdirTestFolder";
        FileSystemTestUtils.createByteFile(FuseFileSystemIntegrationTest.sFileSystem, testFile, MUST_CACHE, 10);
        ShellUtils.execCommand("mkdir", ((FuseFileSystemIntegrationTest.sMountPoint) + testFolder));
        ShellUtils.execCommand("mv", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile), (((FuseFileSystemIntegrationTest.sMountPoint) + testFolder) + testFile));
        Assert.assertFalse(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI(testFile)));
        Assert.assertTrue(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI((testFolder + testFile))));
    }

    @Test
    public void tail() throws Exception {
        String testFile = "/tailTestFile";
        String content = "Alluxio Tail Test File Content";
        try (FileOutStream os = FuseFileSystemIntegrationTest.sFileSystem.createFile(new AlluxioURI(testFile))) {
            os.write(content.getBytes());
        }
        String result = ShellUtils.execCommand("tail", "-c", "17", ((FuseFileSystemIntegrationTest.sMountPoint) + testFile));
        Assert.assertEquals("Test File Content\n", result);
    }

    @Test
    public void touchAndLs() throws Exception {
        FileSystemTestUtils.createByteFile(FuseFileSystemIntegrationTest.sFileSystem, "/lsTestFile", MUST_CACHE, 10);
        String touchTestFile = "/touchTestFile";
        ShellUtils.execCommand("touch", ((FuseFileSystemIntegrationTest.sMountPoint) + touchTestFile));
        String lsResult = ShellUtils.execCommand("ls", FuseFileSystemIntegrationTest.sMountPoint);
        Assert.assertTrue(lsResult.contains("lsTestFile"));
        Assert.assertTrue(lsResult.contains("touchTestFile"));
        Assert.assertTrue(FuseFileSystemIntegrationTest.sFileSystem.exists(new AlluxioURI(touchTestFile)));
    }
}

