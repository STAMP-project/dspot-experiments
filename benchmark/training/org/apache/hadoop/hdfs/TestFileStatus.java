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


import FSNamesystem.LOG;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * This class tests the FileStatus API.
 */
public class TestFileStatus {
    {
        GenericTestUtils.setLogLevel(LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(FileSystem.LOG, Level.TRACE);
    }

    static final long seed = 3735928559L;

    static final int blockSize = 8192;

    static final int fileSize = 16384;

    private static Configuration conf;

    private static MiniDFSCluster cluster;

    private static FileSystem fs;

    private static FileContext fc;

    private static DFSClient dfsClient;

    private static Path file1;

    /**
     * Test calling getFileInfo directly on the client
     */
    @Test
    public void testGetFileInfo() throws IOException {
        // Check that / exists
        Path path = new Path("/");
        Assert.assertTrue("/ should be a directory", TestFileStatus.fs.getFileStatus(path).isDirectory());
        ContractTestUtils.assertNotErasureCoded(TestFileStatus.fs, path);
        // Make sure getFileInfo returns null for files which do not exist
        HdfsFileStatus fileInfo = TestFileStatus.dfsClient.getFileInfo("/noSuchFile");
        Assert.assertEquals("Non-existant file should result in null", null, fileInfo);
        Path path1 = new Path("/name1");
        Path path2 = new Path("/name1/name2");
        Assert.assertTrue(TestFileStatus.fs.mkdirs(path1));
        FSDataOutputStream out = TestFileStatus.fs.create(path2, false);
        out.close();
        fileInfo = TestFileStatus.dfsClient.getFileInfo(path1.toString());
        Assert.assertEquals(1, fileInfo.getChildrenNum());
        fileInfo = TestFileStatus.dfsClient.getFileInfo(path2.toString());
        Assert.assertEquals(0, fileInfo.getChildrenNum());
        // Test getFileInfo throws the right exception given a non-absolute path.
        try {
            TestFileStatus.dfsClient.getFileInfo("non-absolute");
            Assert.fail("getFileInfo for a non-absolute path did not throw IOException");
        } catch (RemoteException re) {
            Assert.assertTrue(("Wrong exception for invalid file name: " + re), re.toString().contains("Absolute path required"));
        }
    }

    /**
     * Test the FileStatus obtained calling getFileStatus on a file
     */
    @Test
    public void testGetFileStatusOnFile() throws Exception {
        checkFile(TestFileStatus.fs, TestFileStatus.file1, 1);
        // test getFileStatus on a file
        FileStatus status = TestFileStatus.fs.getFileStatus(TestFileStatus.file1);
        Assert.assertFalse(((TestFileStatus.file1) + " should be a file"), status.isDirectory());
        Assert.assertEquals(TestFileStatus.blockSize, status.getBlockSize());
        Assert.assertEquals(1, status.getReplication());
        Assert.assertEquals(TestFileStatus.fileSize, status.getLen());
        ContractTestUtils.assertNotErasureCoded(TestFileStatus.fs, TestFileStatus.file1);
        Assert.assertEquals(TestFileStatus.file1.makeQualified(TestFileStatus.fs.getUri(), TestFileStatus.fs.getWorkingDirectory()).toString(), status.getPath().toString());
        Assert.assertTrue(((((TestFileStatus.file1) + " should have erasure coding unset in ") + "FileStatus#toString(): ") + status), status.toString().contains("isErasureCoded=false"));
    }

    /**
     * Test the FileStatus obtained calling listStatus on a file
     */
    @Test
    public void testListStatusOnFile() throws IOException {
        FileStatus[] stats = TestFileStatus.fs.listStatus(TestFileStatus.file1);
        Assert.assertEquals(1, stats.length);
        FileStatus status = stats[0];
        Assert.assertFalse(((TestFileStatus.file1) + " should be a file"), status.isDirectory());
        Assert.assertEquals(TestFileStatus.blockSize, status.getBlockSize());
        Assert.assertEquals(1, status.getReplication());
        Assert.assertEquals(TestFileStatus.fileSize, status.getLen());
        ContractTestUtils.assertNotErasureCoded(TestFileStatus.fs, TestFileStatus.file1);
        Assert.assertEquals(TestFileStatus.file1.makeQualified(TestFileStatus.fs.getUri(), TestFileStatus.fs.getWorkingDirectory()).toString(), status.getPath().toString());
        RemoteIterator<FileStatus> itor = TestFileStatus.fc.listStatus(TestFileStatus.file1);
        status = itor.next();
        Assert.assertEquals(stats[0], status);
        Assert.assertFalse(((TestFileStatus.file1) + " should be a file"), status.isDirectory());
    }

    /**
     * Test getting a FileStatus object using a non-existant path
     */
    @Test
    public void testGetFileStatusOnNonExistantFileDir() throws IOException {
        Path dir = new Path("/test/mkdirs");
        try {
            TestFileStatus.fs.listStatus(dir);
            Assert.fail("listStatus of non-existent path should fail");
        } catch (FileNotFoundException fe) {
            Assert.assertEquals((("File " + dir) + " does not exist."), fe.getMessage());
        }
        try {
            TestFileStatus.fc.listStatus(dir);
            Assert.fail("listStatus of non-existent path should fail");
        } catch (FileNotFoundException fe) {
            Assert.assertEquals((("File " + dir) + " does not exist."), fe.getMessage());
        }
        try {
            TestFileStatus.fs.getFileStatus(dir);
            Assert.fail("getFileStatus of non-existent path should fail");
        } catch (FileNotFoundException fe) {
            Assert.assertTrue("Exception doesn't indicate non-existant path", fe.getMessage().startsWith("File does not exist"));
        }
    }

    /**
     * Test FileStatus objects obtained from a directory
     */
    @Test
    public void testGetFileStatusOnDir() throws Exception {
        // Create the directory
        Path dir = new Path("/test/mkdirs");
        Assert.assertTrue("mkdir failed", TestFileStatus.fs.mkdirs(dir));
        Assert.assertTrue("mkdir failed", TestFileStatus.fs.exists(dir));
        // test getFileStatus on an empty directory
        FileStatus status = TestFileStatus.fs.getFileStatus(dir);
        Assert.assertTrue((dir + " should be a directory"), status.isDirectory());
        Assert.assertTrue((dir + " should be zero size "), ((status.getLen()) == 0));
        ContractTestUtils.assertNotErasureCoded(TestFileStatus.fs, dir);
        Assert.assertEquals(dir.makeQualified(TestFileStatus.fs.getUri(), TestFileStatus.fs.getWorkingDirectory()).toString(), status.getPath().toString());
        // test listStatus on an empty directory
        FileStatus[] stats = TestFileStatus.fs.listStatus(dir);
        Assert.assertEquals((dir + " should be empty"), 0, stats.length);
        Assert.assertEquals((dir + " should be zero size "), 0, TestFileStatus.fs.getContentSummary(dir).getLength());
        RemoteIterator<FileStatus> itor = TestFileStatus.fc.listStatus(dir);
        Assert.assertFalse((dir + " should be empty"), itor.hasNext());
        itor = TestFileStatus.fs.listStatusIterator(dir);
        Assert.assertFalse((dir + " should be empty"), itor.hasNext());
        // create another file that is smaller than a block.
        Path file2 = new Path(dir, "filestatus2.dat");
        DFSTestUtil.createFile(TestFileStatus.fs, file2, ((TestFileStatus.blockSize) / 4), ((TestFileStatus.blockSize) / 4), TestFileStatus.blockSize, ((short) (1)), TestFileStatus.seed);
        checkFile(TestFileStatus.fs, file2, 1);
        // verify file attributes
        status = TestFileStatus.fs.getFileStatus(file2);
        Assert.assertEquals(TestFileStatus.blockSize, status.getBlockSize());
        Assert.assertEquals(1, status.getReplication());
        file2 = TestFileStatus.fs.makeQualified(file2);
        Assert.assertEquals(file2.toString(), status.getPath().toString());
        // Create another file in the same directory
        Path file3 = new Path(dir, "filestatus3.dat");
        DFSTestUtil.createFile(TestFileStatus.fs, file3, ((TestFileStatus.blockSize) / 4), ((TestFileStatus.blockSize) / 4), TestFileStatus.blockSize, ((short) (1)), TestFileStatus.seed);
        checkFile(TestFileStatus.fs, file3, 1);
        file3 = TestFileStatus.fs.makeQualified(file3);
        // Verify that the size of the directory increased by the size
        // of the two files
        final int expected = (TestFileStatus.blockSize) / 2;
        Assert.assertEquals(((dir + " size should be ") + expected), expected, TestFileStatus.fs.getContentSummary(dir).getLength());
        // Test listStatus on a non-empty directory
        stats = TestFileStatus.fs.listStatus(dir);
        Assert.assertEquals((dir + " should have two entries"), 2, stats.length);
        Assert.assertEquals(file2.toString(), stats[0].getPath().toString());
        Assert.assertEquals(file3.toString(), stats[1].getPath().toString());
        itor = TestFileStatus.fc.listStatus(dir);
        Assert.assertEquals(file2.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file3.toString(), itor.next().getPath().toString());
        Assert.assertFalse("Unexpected addtional file", itor.hasNext());
        itor = TestFileStatus.fs.listStatusIterator(dir);
        Assert.assertEquals(file2.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file3.toString(), itor.next().getPath().toString());
        Assert.assertFalse("Unexpected addtional file", itor.hasNext());
        // Test iterative listing. Now dir has 2 entries, create one more.
        Path dir3 = TestFileStatus.fs.makeQualified(new Path(dir, "dir3"));
        TestFileStatus.fs.mkdirs(dir3);
        dir3 = TestFileStatus.fs.makeQualified(dir3);
        stats = TestFileStatus.fs.listStatus(dir);
        Assert.assertEquals((dir + " should have three entries"), 3, stats.length);
        Assert.assertEquals(dir3.toString(), stats[0].getPath().toString());
        Assert.assertEquals(file2.toString(), stats[1].getPath().toString());
        Assert.assertEquals(file3.toString(), stats[2].getPath().toString());
        itor = TestFileStatus.fc.listStatus(dir);
        Assert.assertEquals(dir3.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file2.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file3.toString(), itor.next().getPath().toString());
        Assert.assertFalse("Unexpected addtional file", itor.hasNext());
        itor = TestFileStatus.fs.listStatusIterator(dir);
        Assert.assertEquals(dir3.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file2.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file3.toString(), itor.next().getPath().toString());
        Assert.assertFalse("Unexpected addtional file", itor.hasNext());
        // Now dir has 3 entries, create two more
        Path dir4 = TestFileStatus.fs.makeQualified(new Path(dir, "dir4"));
        TestFileStatus.fs.mkdirs(dir4);
        dir4 = TestFileStatus.fs.makeQualified(dir4);
        Path dir5 = TestFileStatus.fs.makeQualified(new Path(dir, "dir5"));
        TestFileStatus.fs.mkdirs(dir5);
        dir5 = TestFileStatus.fs.makeQualified(dir5);
        stats = TestFileStatus.fs.listStatus(dir);
        Assert.assertEquals((dir + " should have five entries"), 5, stats.length);
        Assert.assertEquals(dir3.toString(), stats[0].getPath().toString());
        Assert.assertEquals(dir4.toString(), stats[1].getPath().toString());
        Assert.assertEquals(dir5.toString(), stats[2].getPath().toString());
        Assert.assertEquals(file2.toString(), stats[3].getPath().toString());
        Assert.assertEquals(file3.toString(), stats[4].getPath().toString());
        itor = TestFileStatus.fc.listStatus(dir);
        Assert.assertEquals(dir3.toString(), itor.next().getPath().toString());
        Assert.assertEquals(dir4.toString(), itor.next().getPath().toString());
        Assert.assertEquals(dir5.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file2.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file3.toString(), itor.next().getPath().toString());
        Assert.assertFalse(itor.hasNext());
        itor = TestFileStatus.fs.listStatusIterator(dir);
        Assert.assertEquals(dir3.toString(), itor.next().getPath().toString());
        Assert.assertEquals(dir4.toString(), itor.next().getPath().toString());
        Assert.assertEquals(dir5.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file2.toString(), itor.next().getPath().toString());
        Assert.assertEquals(file3.toString(), itor.next().getPath().toString());
        Assert.assertFalse(itor.hasNext());
        itor = TestFileStatus.fs.listStatusIterator(dir);
        Assert.assertEquals(dir3.toString(), itor.next().getPath().toString());
        Assert.assertEquals(dir4.toString(), itor.next().getPath().toString());
        TestFileStatus.fs.delete(dir.getParent(), true);
        try {
            itor.hasNext();
            Assert.fail("FileNotFoundException expected");
        } catch (FileNotFoundException fnfe) {
        }
        TestFileStatus.fs.mkdirs(file2);
        TestFileStatus.fs.mkdirs(dir3);
        TestFileStatus.fs.mkdirs(dir4);
        TestFileStatus.fs.mkdirs(dir5);
        itor = TestFileStatus.fs.listStatusIterator(dir);
        int count = 0;
        try {
            TestFileStatus.fs.delete(dir.getParent(), true);
            while ((itor.next()) != null) {
                count++;
            } 
            Assert.fail("FileNotFoundException expected");
        } catch (FileNotFoundException fnfe) {
        }
        Assert.assertEquals(2, count);
    }
}

