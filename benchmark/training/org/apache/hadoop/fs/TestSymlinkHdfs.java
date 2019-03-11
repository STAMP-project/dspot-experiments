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


import FileContext.DEFAULT_PERM;
import HdfsConstants.QUOTA_DONT_SET;
import NameNode.stateChangeLog;
import java.io.IOException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.QuotaExceededException;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.web.WebHdfsFileSystem;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test symbolic links in Hdfs.
 */
public abstract class TestSymlinkHdfs extends SymlinkBaseTest {
    {
        GenericTestUtils.setLogLevel(stateChangeLog, Level.ALL);
    }

    protected static MiniDFSCluster cluster;

    protected static WebHdfsFileSystem webhdfs;

    protected static DistributedFileSystem dfs;

    /**
     * Access a file using a link that spans Hdfs to LocalFs
     */
    @Test(timeout = 10000)
    public void testLinkAcrossFileSystems() throws IOException {
        Path localDir = new Path((("file://" + (wrapper.getAbsoluteTestRootDir())) + "/test"));
        Path localFile = new Path((("file://" + (wrapper.getAbsoluteTestRootDir())) + "/test/file"));
        Path link = new Path(testBaseDir1(), "linkToFile");
        FSTestWrapper localWrapper = wrapper.getLocalFSWrapper();
        localWrapper.delete(localDir, true);
        localWrapper.mkdir(localDir, DEFAULT_PERM, true);
        localWrapper.setWorkingDirectory(localDir);
        Assert.assertEquals(localDir, localWrapper.getWorkingDirectory());
        createAndWriteFile(localWrapper, localFile);
        wrapper.createSymlink(localFile, link, false);
        readFile(link);
        Assert.assertEquals(fileSize, wrapper.getFileStatus(link).getLen());
    }

    /**
     * Test renaming a file across two file systems using a link
     */
    @Test(timeout = 10000)
    public void testRenameAcrossFileSystemsViaLink() throws IOException {
        Path localDir = new Path((("file://" + (wrapper.getAbsoluteTestRootDir())) + "/test"));
        Path hdfsFile = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "link");
        Path hdfsFileNew = new Path(testBaseDir1(), "fileNew");
        Path hdfsFileNewViaLink = new Path(link, "fileNew");
        FSTestWrapper localWrapper = wrapper.getLocalFSWrapper();
        localWrapper.delete(localDir, true);
        localWrapper.mkdir(localDir, DEFAULT_PERM, true);
        localWrapper.setWorkingDirectory(localDir);
        createAndWriteFile(hdfsFile);
        wrapper.createSymlink(localDir, link, false);
        // Rename hdfs://test1/file to hdfs://test1/link/fileNew
        // which renames to file://TEST_ROOT/test/fileNew which
        // spans AbstractFileSystems and therefore fails.
        try {
            wrapper.rename(hdfsFile, hdfsFileNewViaLink);
            Assert.fail("Renamed across file systems");
        } catch (InvalidPathException ipe) {
            // Expected from FileContext
        } catch (IllegalArgumentException e) {
            // Expected from Filesystem
            GenericTestUtils.assertExceptionContains("Wrong FS: ", e);
        }
        // Now rename hdfs://test1/link/fileNew to hdfs://test1/fileNew
        // which renames file://TEST_ROOT/test/fileNew to hdfs://test1/fileNew
        // which spans AbstractFileSystems and therefore fails.
        createAndWriteFile(hdfsFileNewViaLink);
        try {
            wrapper.rename(hdfsFileNewViaLink, hdfsFileNew);
            Assert.fail("Renamed across file systems");
        } catch (InvalidPathException ipe) {
            // Expected from FileContext
        } catch (IllegalArgumentException e) {
            // Expected from Filesystem
            GenericTestUtils.assertExceptionContains("Wrong FS: ", e);
        }
    }

    /**
     * Test create symlink to /
     */
    @Test(timeout = 10000)
    public void testCreateLinkToSlash() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToSlash");
        Path fileViaLink = new Path(((((testBaseDir1()) + "/linkToSlash") + (testBaseDir1())) + "/file"));
        createAndWriteFile(file);
        wrapper.setWorkingDirectory(dir);
        wrapper.createSymlink(new Path("/"), link, false);
        readFile(fileViaLink);
        Assert.assertEquals(fileSize, wrapper.getFileStatus(fileViaLink).getLen());
        // Ditto when using another file context since the file system
        // for the slash is resolved according to the link's parent.
        if ((wrapper) instanceof FileContextTestWrapper) {
            FSTestWrapper localWrapper = wrapper.getLocalFSWrapper();
            Path linkQual = new Path(TestSymlinkHdfs.cluster.getURI(0).toString(), fileViaLink);
            Assert.assertEquals(fileSize, localWrapper.getFileStatus(linkQual).getLen());
        }
    }

    /**
     * setPermission affects the target not the link
     */
    @Test(timeout = 10000)
    public void testSetPermissionAffectsTarget() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path dir = new Path(testBaseDir2());
        Path linkToFile = new Path(testBaseDir1(), "linkToFile");
        Path linkToDir = new Path(testBaseDir1(), "linkToDir");
        createAndWriteFile(file);
        wrapper.createSymlink(file, linkToFile, false);
        wrapper.createSymlink(dir, linkToDir, false);
        // Changing the permissions using the link does not modify
        // the permissions of the link..
        FsPermission perms = wrapper.getFileLinkStatus(linkToFile).getPermission();
        wrapper.setPermission(linkToFile, new FsPermission(((short) (436))));
        wrapper.setOwner(linkToFile, "user", "group");
        Assert.assertEquals(perms, wrapper.getFileLinkStatus(linkToFile).getPermission());
        // but the file's permissions were adjusted appropriately
        FileStatus stat = wrapper.getFileStatus(file);
        Assert.assertEquals(436, stat.getPermission().toShort());
        Assert.assertEquals("user", stat.getOwner());
        Assert.assertEquals("group", stat.getGroup());
        // Getting the file's permissions via the link is the same
        // as getting the permissions directly.
        Assert.assertEquals(stat.getPermission(), wrapper.getFileStatus(linkToFile).getPermission());
        // Ditto for a link to a directory
        perms = wrapper.getFileLinkStatus(linkToDir).getPermission();
        wrapper.setPermission(linkToDir, new FsPermission(((short) (436))));
        wrapper.setOwner(linkToDir, "user", "group");
        Assert.assertEquals(perms, wrapper.getFileLinkStatus(linkToDir).getPermission());
        stat = wrapper.getFileStatus(dir);
        Assert.assertEquals(436, stat.getPermission().toShort());
        Assert.assertEquals("user", stat.getOwner());
        Assert.assertEquals("group", stat.getGroup());
        Assert.assertEquals(stat.getPermission(), wrapper.getFileStatus(linkToDir).getPermission());
    }

    /**
     * Create a symlink using a path with scheme but no authority
     */
    @Test(timeout = 10000)
    public void testCreateWithPartQualPathFails() throws IOException {
        Path fileWoAuth = new Path("hdfs:///test/file");
        Path linkWoAuth = new Path("hdfs:///test/link");
        try {
            createAndWriteFile(fileWoAuth);
            Assert.fail("HDFS requires URIs with schemes have an authority");
        } catch (RuntimeException e) {
            // Expected
        }
        try {
            wrapper.createSymlink(new Path("foo"), linkWoAuth, false);
            Assert.fail("HDFS requires URIs with schemes have an authority");
        } catch (RuntimeException e) {
            // Expected
        }
    }

    /**
     * setReplication affects the target not the link
     */
    @Test(timeout = 10000)
    public void testSetReplication() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        createAndWriteFile(file);
        wrapper.createSymlink(file, link, false);
        wrapper.setReplication(link, ((short) (2)));
        Assert.assertEquals(0, wrapper.getFileLinkStatus(link).getReplication());
        Assert.assertEquals(2, wrapper.getFileStatus(link).getReplication());
        Assert.assertEquals(2, wrapper.getFileStatus(file).getReplication());
    }

    /**
     * Test create symlink with a max len name
     */
    @Test(timeout = 10000)
    public void testCreateLinkMaxPathLink() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        final int maxPathLen = HdfsServerConstants.MAX_PATH_LENGTH;
        final int dirLen = (dir.toString().length()) + 1;
        int len = maxPathLen - dirLen;
        // Build a MAX_PATH_LENGTH path
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < (len / 10); i++) {
            sb.append("0123456789");
        }
        for (int i = 0; i < (len % 10); i++) {
            sb.append("x");
        }
        Path link = new Path(sb.toString());
        Assert.assertEquals(maxPathLen, (dirLen + (link.toString().length())));
        // Check that it works
        createAndWriteFile(file);
        wrapper.setWorkingDirectory(dir);
        wrapper.createSymlink(file, link, false);
        readFile(link);
        // Now modify the path so it's too large
        link = new Path(((sb.toString()) + "x"));
        try {
            wrapper.createSymlink(file, link, false);
            Assert.fail("Path name should be too long");
        } catch (IOException x) {
            // Expected
        }
    }

    /**
     * Test symlink owner
     */
    @Test(timeout = 10000)
    public void testLinkOwner() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "symlinkToFile");
        createAndWriteFile(file);
        wrapper.createSymlink(file, link, false);
        FileStatus statFile = wrapper.getFileStatus(file);
        FileStatus statLink = wrapper.getFileStatus(link);
        Assert.assertEquals(statLink.getOwner(), statFile.getOwner());
    }

    /**
     * Test WebHdfsFileSystem.createSymlink(..).
     */
    @Test(timeout = 10000)
    public void testWebHDFS() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        createAndWriteFile(file);
        TestSymlinkHdfs.webhdfs.createSymlink(file, link, false);
        wrapper.setReplication(link, ((short) (2)));
        Assert.assertEquals(0, wrapper.getFileLinkStatus(link).getReplication());
        Assert.assertEquals(2, wrapper.getFileStatus(link).getReplication());
        Assert.assertEquals(2, wrapper.getFileStatus(file).getReplication());
    }

    /**
     * Test craeteSymlink(..) with quota.
     */
    @Test(timeout = 10000)
    public void testQuota() throws IOException {
        final Path dir = new Path(testBaseDir1());
        TestSymlinkHdfs.dfs.setQuota(dir, 3, QUOTA_DONT_SET);
        final Path file = new Path(dir, "file");
        createAndWriteFile(file);
        // creating the first link should succeed
        final Path link1 = new Path(dir, "link1");
        wrapper.createSymlink(file, link1, false);
        try {
            // creating the second link should fail with QuotaExceededException.
            final Path link2 = new Path(dir, "link2");
            wrapper.createSymlink(file, link2, false);
            Assert.fail("Created symlink despite quota violation");
        } catch (QuotaExceededException qee) {
            // expected
        }
    }
}

