/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.sftp;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.sshd.server.SshServer;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class TestSFTPFileSystem {
    private static final String TEST_SFTP_DIR = "testsftp";

    private static final String TEST_ROOT_DIR = GenericTestUtils.getTestDir().getAbsolutePath();

    @Rule
    public TestName name = new TestName();

    private static final String connection = "sftp://user:password@localhost";

    private static Path localDir = null;

    private static FileSystem localFs = null;

    private FileSystem sftpFs = null;

    private static SshServer sshd = null;

    private static Configuration conf = null;

    private static int port;

    /**
     * Creates a file and deletes it.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateFile() throws Exception {
        Path file = TestSFTPFileSystem.touch(sftpFs, name.getMethodName().toLowerCase());
        Assert.assertTrue(TestSFTPFileSystem.localFs.exists(file));
        Assert.assertTrue(sftpFs.delete(file, false));
        Assert.assertFalse(TestSFTPFileSystem.localFs.exists(file));
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Checks if a new created file exists.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFileExists() throws Exception {
        Path file = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, name.getMethodName().toLowerCase());
        Assert.assertTrue(sftpFs.exists(file));
        Assert.assertTrue(TestSFTPFileSystem.localFs.exists(file));
        Assert.assertTrue(sftpFs.delete(file, false));
        Assert.assertFalse(sftpFs.exists(file));
        Assert.assertFalse(TestSFTPFileSystem.localFs.exists(file));
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Test writing to a file and reading its value.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadFile() throws Exception {
        byte[] data = "yaks".getBytes();
        Path file = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, name.getMethodName().toLowerCase(), data);
        FSDataInputStream is = null;
        try {
            is = sftpFs.open(file);
            byte[] b = new byte[data.length];
            is.read(b);
            Assert.assertArrayEquals(data, b);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        Assert.assertTrue(sftpFs.delete(file, false));
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Test getting the status of a file.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStatFile() throws Exception {
        byte[] data = "yaks".getBytes();
        Path file = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, name.getMethodName().toLowerCase(), data);
        FileStatus lstat = TestSFTPFileSystem.localFs.getFileStatus(file);
        FileStatus sstat = sftpFs.getFileStatus(file);
        Assert.assertNotNull(sstat);
        Assert.assertEquals(lstat.getPath().toUri().getPath(), sstat.getPath().toUri().getPath());
        Assert.assertEquals(data.length, sstat.getLen());
        Assert.assertEquals(lstat.getLen(), sstat.getLen());
        Assert.assertTrue(sftpFs.delete(file, false));
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Test deleting a non empty directory.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = IOException.class)
    public void testDeleteNonEmptyDir() throws Exception {
        Path file = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, name.getMethodName().toLowerCase());
        sftpFs.delete(TestSFTPFileSystem.localDir, false);
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Test deleting a file that does not exist.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeleteNonExistFile() throws Exception {
        Path file = new Path(TestSFTPFileSystem.localDir, name.getMethodName().toLowerCase());
        Assert.assertFalse(sftpFs.delete(file, false));
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Test renaming a file.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRenameFile() throws Exception {
        byte[] data = "dingos".getBytes();
        Path file1 = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, ((name.getMethodName().toLowerCase()) + "1"));
        Path file2 = new Path(TestSFTPFileSystem.localDir, ((name.getMethodName().toLowerCase()) + "2"));
        Assert.assertTrue(sftpFs.rename(file1, file2));
        Assert.assertTrue(sftpFs.exists(file2));
        Assert.assertFalse(sftpFs.exists(file1));
        Assert.assertTrue(TestSFTPFileSystem.localFs.exists(file2));
        Assert.assertFalse(TestSFTPFileSystem.localFs.exists(file1));
        Assert.assertTrue(sftpFs.delete(file2, false));
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    /**
     * Test renaming a file that does not exist.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = IOException.class)
    public void testRenameNonExistFile() throws Exception {
        Path file1 = new Path(TestSFTPFileSystem.localDir, ((name.getMethodName().toLowerCase()) + "1"));
        Path file2 = new Path(TestSFTPFileSystem.localDir, ((name.getMethodName().toLowerCase()) + "2"));
        sftpFs.rename(file1, file2);
    }

    /**
     * Test renaming a file onto an existing file.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = IOException.class)
    public void testRenamingFileOntoExistingFile() throws Exception {
        Path file1 = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, ((name.getMethodName().toLowerCase()) + "1"));
        Path file2 = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, ((name.getMethodName().toLowerCase()) + "2"));
        sftpFs.rename(file1, file2);
    }

    @Test
    public void testGetAccessTime() throws IOException {
        Path file = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, name.getMethodName().toLowerCase());
        LocalFileSystem local = ((LocalFileSystem) (TestSFTPFileSystem.localFs));
        java.nio.file.Path path = local.pathToFile(file).toPath();
        long accessTime1 = Files.readAttributes(path, BasicFileAttributes.class).lastAccessTime().toMillis();
        // SFTPFileSystem doesn't have milliseconds. Excluding it.
        accessTime1 = (accessTime1 / 1000) * 1000;
        long accessTime2 = sftpFs.getFileStatus(file).getAccessTime();
        Assert.assertEquals(accessTime1, accessTime2);
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    @Test
    public void testGetModifyTime() throws IOException {
        Path file = TestSFTPFileSystem.touch(TestSFTPFileSystem.localFs, ((name.getMethodName().toLowerCase()) + "1"));
        File localFile = ((LocalFileSystem) (TestSFTPFileSystem.localFs)).pathToFile(file);
        long modifyTime1 = localFile.lastModified();
        // SFTPFileSystem doesn't have milliseconds. Excluding it.
        modifyTime1 = (modifyTime1 / 1000) * 1000;
        long modifyTime2 = sftpFs.getFileStatus(file).getModificationTime();
        Assert.assertEquals(modifyTime1, modifyTime2);
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }

    @Test
    public void testMkDirs() throws IOException {
        Path path = new Path(TestSFTPFileSystem.localDir.toUri().getPath(), new Path(name.getMethodName(), "subdirectory"));
        sftpFs.mkdirs(path);
        Assert.assertTrue(TestSFTPFileSystem.localFs.exists(path));
        Assert.assertTrue(TestSFTPFileSystem.localFs.getFileStatus(path).isDirectory());
        Assert.assertThat(getConnectionPool().getLiveConnCount(), Is.is(1));
    }
}

