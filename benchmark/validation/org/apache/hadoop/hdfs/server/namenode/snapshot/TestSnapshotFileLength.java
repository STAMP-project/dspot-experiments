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
package org.apache.hadoop.hdfs.server.namenode.snapshot;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.util.ToolRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSnapshotFileLength {
    private static final long SEED = 0;

    private static final short REPLICATION = 1;

    private static final int BLOCKSIZE = 1024;

    private static final Configuration conf = new Configuration();

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem hdfs;

    private final Path dir = new Path("/TestSnapshotFileLength");

    private final Path sub = new Path(dir, "sub1");

    private final String file1Name = "file1";

    private final String snapshot1 = "snapshot1";

    /**
     * Test that we cannot read a file beyond its snapshot length
     * when accessing it via a snapshot path.
     */
    @Test(timeout = 300000)
    public void testSnapshotfileLength() throws Exception {
        TestSnapshotFileLength.hdfs.mkdirs(sub);
        int bytesRead;
        byte[] buffer = new byte[(TestSnapshotFileLength.BLOCKSIZE) * 8];
        int origLen = (TestSnapshotFileLength.BLOCKSIZE) + 1;
        int toAppend = TestSnapshotFileLength.BLOCKSIZE;
        FSDataInputStream fis = null;
        FileStatus fileStatus = null;
        // Create and write a file.
        Path file1 = new Path(sub, file1Name);
        DFSTestUtil.createFile(TestSnapshotFileLength.hdfs, file1, TestSnapshotFileLength.BLOCKSIZE, 0, TestSnapshotFileLength.BLOCKSIZE, TestSnapshotFileLength.REPLICATION, TestSnapshotFileLength.SEED);
        DFSTestUtil.appendFile(TestSnapshotFileLength.hdfs, file1, origLen);
        // Create a snapshot on the parent directory.
        TestSnapshotFileLength.hdfs.allowSnapshot(sub);
        TestSnapshotFileLength.hdfs.createSnapshot(sub, snapshot1);
        Path file1snap1 = SnapshotTestHelper.getSnapshotPath(sub, snapshot1, file1Name);
        final FileChecksum snapChksum1 = TestSnapshotFileLength.hdfs.getFileChecksum(file1snap1);
        Assert.assertThat("file and snapshot file checksums are not equal", TestSnapshotFileLength.hdfs.getFileChecksum(file1), CoreMatchers.is(snapChksum1));
        // Append to the file.
        FSDataOutputStream out = TestSnapshotFileLength.hdfs.append(file1);
        // Nothing has been appended yet. All checksums should still be equal.
        // HDFS-8150:Fetching checksum for file under construction should fail
        try {
            TestSnapshotFileLength.hdfs.getFileChecksum(file1);
            Assert.fail(("getFileChecksum should fail for files " + "with blocks under construction"));
        } catch (IOException ie) {
            Assert.assertTrue(ie.getMessage().contains((("Fail to get checksum, since file " + file1) + " is under construction.")));
        }
        Assert.assertThat("snapshot checksum (post-open for append) has changed", TestSnapshotFileLength.hdfs.getFileChecksum(file1snap1), CoreMatchers.is(snapChksum1));
        try {
            AppendTestUtil.write(out, 0, toAppend);
            // Test reading from snapshot of file that is open for append
            byte[] dataFromSnapshot = DFSTestUtil.readFileBuffer(TestSnapshotFileLength.hdfs, file1snap1);
            Assert.assertThat("Wrong data size in snapshot.", dataFromSnapshot.length, CoreMatchers.is(origLen));
            // Verify that checksum didn't change
            Assert.assertThat("snapshot checksum (post-append) has changed", TestSnapshotFileLength.hdfs.getFileChecksum(file1snap1), CoreMatchers.is(snapChksum1));
        } finally {
            out.close();
        }
        Assert.assertThat("file and snapshot file checksums (post-close) are equal", TestSnapshotFileLength.hdfs.getFileChecksum(file1), CoreMatchers.not(snapChksum1));
        Assert.assertThat("snapshot file checksum (post-close) has changed", TestSnapshotFileLength.hdfs.getFileChecksum(file1snap1), CoreMatchers.is(snapChksum1));
        // Make sure we can read the entire file via its non-snapshot path.
        fileStatus = TestSnapshotFileLength.hdfs.getFileStatus(file1);
        Assert.assertThat(fileStatus.getLen(), CoreMatchers.is((((long) (origLen)) + toAppend)));
        fis = TestSnapshotFileLength.hdfs.open(file1);
        bytesRead = fis.read(0, buffer, 0, buffer.length);
        Assert.assertThat(bytesRead, CoreMatchers.is((origLen + toAppend)));
        fis.close();
        // Try to open the file via its snapshot path.
        fis = TestSnapshotFileLength.hdfs.open(file1snap1);
        fileStatus = TestSnapshotFileLength.hdfs.getFileStatus(file1snap1);
        Assert.assertThat(fileStatus.getLen(), CoreMatchers.is(((long) (origLen))));
        // Make sure we can only read up to the snapshot length.
        bytesRead = fis.read(0, buffer, 0, buffer.length);
        Assert.assertThat(bytesRead, CoreMatchers.is(origLen));
        fis.close();
        byte[] dataFromSnapshot = DFSTestUtil.readFileBuffer(TestSnapshotFileLength.hdfs, file1snap1);
        Assert.assertThat("Wrong data size in snapshot.", dataFromSnapshot.length, CoreMatchers.is(origLen));
    }

    /**
     * Adding as part of jira HDFS-5343
     * Test for checking the cat command on snapshot path it
     *  cannot read a file beyond snapshot file length
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 600000)
    public void testSnapshotFileLengthWithCatCommand() throws Exception {
        FSDataInputStream fis = null;
        FileStatus fileStatus = null;
        int bytesRead;
        byte[] buffer = new byte[(TestSnapshotFileLength.BLOCKSIZE) * 8];
        TestSnapshotFileLength.hdfs.mkdirs(sub);
        Path file1 = new Path(sub, file1Name);
        DFSTestUtil.createFile(TestSnapshotFileLength.hdfs, file1, TestSnapshotFileLength.BLOCKSIZE, TestSnapshotFileLength.REPLICATION, TestSnapshotFileLength.SEED);
        TestSnapshotFileLength.hdfs.allowSnapshot(sub);
        TestSnapshotFileLength.hdfs.createSnapshot(sub, snapshot1);
        DFSTestUtil.appendFile(TestSnapshotFileLength.hdfs, file1, TestSnapshotFileLength.BLOCKSIZE);
        // Make sure we can read the entire file via its non-snapshot path.
        fileStatus = TestSnapshotFileLength.hdfs.getFileStatus(file1);
        Assert.assertEquals("Unexpected file length", ((TestSnapshotFileLength.BLOCKSIZE) * 2), fileStatus.getLen());
        fis = TestSnapshotFileLength.hdfs.open(file1);
        bytesRead = fis.read(buffer, 0, buffer.length);
        Assert.assertEquals("Unexpected # bytes read", ((TestSnapshotFileLength.BLOCKSIZE) * 2), bytesRead);
        fis.close();
        Path file1snap1 = SnapshotTestHelper.getSnapshotPath(sub, snapshot1, file1Name);
        fis = TestSnapshotFileLength.hdfs.open(file1snap1);
        fileStatus = TestSnapshotFileLength.hdfs.getFileStatus(file1snap1);
        Assert.assertEquals(fileStatus.getLen(), TestSnapshotFileLength.BLOCKSIZE);
        // Make sure we can only read up to the snapshot length.
        bytesRead = fis.read(buffer, 0, buffer.length);
        Assert.assertEquals("Unexpected # bytes read", TestSnapshotFileLength.BLOCKSIZE, bytesRead);
        fis.close();
        PrintStream outBackup = System.out;
        PrintStream errBackup = System.err;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bao));
        System.setErr(new PrintStream(bao));
        // Make sure we can cat the file upto to snapshot length
        FsShell shell = new FsShell();
        try {
            ToolRunner.run(TestSnapshotFileLength.conf, shell, new String[]{ "-cat", "/TestSnapshotFileLength/sub1/.snapshot/snapshot1/file1" });
            Assert.assertEquals("Unexpected # bytes from -cat", TestSnapshotFileLength.BLOCKSIZE, bao.size());
        } finally {
            System.setOut(outBackup);
            System.setErr(errBackup);
        }
    }
}

