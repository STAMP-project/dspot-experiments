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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.QuotaExceededException;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static FSDirectory.DOT_RESERVED_STRING;


public class TestHDFSConcat {
    public static final Logger LOG = LoggerFactory.getLogger(TestHDFSConcat.class);

    private static final short REPL_FACTOR = 2;

    private MiniDFSCluster cluster;

    private NamenodeProtocols nn;

    private DistributedFileSystem dfs;

    private static final long blockSize = 512;

    private static final Configuration conf;

    static {
        conf = new Configuration();
        TestHDFSConcat.conf.setLong(DFS_BLOCK_SIZE_KEY, TestHDFSConcat.blockSize);
    }

    /**
     * Concatenates 10 files into one
     * Verifies the final size, deletion of the file, number of blocks
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testConcat() throws IOException, InterruptedException {
        final int numFiles = 10;
        long fileLen = (TestHDFSConcat.blockSize) * 3;
        HdfsFileStatus fStatus;
        FSDataInputStream stm;
        String trg = "/trg";
        Path trgPath = new Path(trg);
        DFSTestUtil.createFile(dfs, trgPath, fileLen, TestHDFSConcat.REPL_FACTOR, 1);
        fStatus = nn.getFileInfo(trg);
        long trgLen = fStatus.getLen();
        long trgBlocks = nn.getBlockLocations(trg, 0, trgLen).locatedBlockCount();
        Path[] files = new Path[numFiles];
        byte[][] bytes = new byte[numFiles + 1][((int) (fileLen))];
        LocatedBlocks[] lblocks = new LocatedBlocks[numFiles];
        long[] lens = new long[numFiles];
        stm = dfs.open(trgPath);
        stm.readFully(0, bytes[0]);
        stm.close();
        int i;
        for (i = 0; i < (files.length); i++) {
            files[i] = new Path(("/file" + i));
            Path path = files[i];
            System.out.println(("Creating file " + path));
            // make files with different content
            DFSTestUtil.createFile(dfs, path, fileLen, TestHDFSConcat.REPL_FACTOR, i);
            fStatus = nn.getFileInfo(path.toUri().getPath());
            lens[i] = fStatus.getLen();
            Assert.assertEquals(trgLen, lens[i]);// file of the same length.

            lblocks[i] = nn.getBlockLocations(path.toUri().getPath(), 0, lens[i]);
            // read the file
            stm = dfs.open(path);
            stm.readFully(0, bytes[(i + 1)]);
            // bytes[i][10] = 10;
            stm.close();
        }
        // check permissions -try the operation with the "wrong" user
        final UserGroupInformation user1 = UserGroupInformation.createUserForTesting("theDoctor", new String[]{ "tardis" });
        DistributedFileSystem hdfs = ((DistributedFileSystem) (DFSTestUtil.getFileSystemAs(user1, TestHDFSConcat.conf)));
        try {
            hdfs.concat(trgPath, files);
            Assert.fail("Permission exception expected");
        } catch (IOException ie) {
            System.out.println(("Got expected exception for permissions:" + (ie.getLocalizedMessage())));
            // expected
        }
        // check count update
        ContentSummary cBefore = dfs.getContentSummary(trgPath.getParent());
        // resort file array, make INode id not sorted.
        for (int j = 0; j < ((files.length) / 2); j++) {
            Path tempPath = files[j];
            files[j] = files[(((files.length) - 1) - j)];
            files[(((files.length) - 1) - j)] = tempPath;
            byte[] tempBytes = bytes[(1 + j)];
            bytes[(1 + j)] = bytes[((((files.length) - 1) - j) + 1)];
            bytes[((((files.length) - 1) - j) + 1)] = tempBytes;
        }
        // now concatenate
        dfs.concat(trgPath, files);
        // verify  count
        ContentSummary cAfter = dfs.getContentSummary(trgPath.getParent());
        Assert.assertEquals(cBefore.getFileCount(), ((cAfter.getFileCount()) + (files.length)));
        // verify other stuff
        long totalLen = trgLen;
        long totalBlocks = trgBlocks;
        for (i = 0; i < (files.length); i++) {
            totalLen += lens[i];
            totalBlocks += lblocks[i].locatedBlockCount();
        }
        System.out.println(((("total len=" + totalLen) + "; totalBlocks=") + totalBlocks));
        fStatus = nn.getFileInfo(trg);
        trgLen = fStatus.getLen();// new length

        // read the resulting file
        stm = dfs.open(trgPath);
        byte[] byteFileConcat = new byte[((int) (trgLen))];
        stm.readFully(0, byteFileConcat);
        stm.close();
        trgBlocks = nn.getBlockLocations(trg, 0, trgLen).locatedBlockCount();
        // verifications
        // 1. number of blocks
        Assert.assertEquals(trgBlocks, totalBlocks);
        // 2. file lengths
        Assert.assertEquals(trgLen, totalLen);
        // 3. removal of the src file
        for (Path p : files) {
            fStatus = nn.getFileInfo(p.toUri().getPath());
            Assert.assertNull((("File " + p) + " still exists"), fStatus);// file shouldn't exist

            // try to create fie with the same name
            DFSTestUtil.createFile(dfs, p, fileLen, TestHDFSConcat.REPL_FACTOR, 1);
        }
        // 4. content
        checkFileContent(byteFileConcat, bytes);
        // add a small file (less then a block)
        Path smallFile = new Path("/sfile");
        int sFileLen = 10;
        DFSTestUtil.createFile(dfs, smallFile, sFileLen, TestHDFSConcat.REPL_FACTOR, 1);
        dfs.concat(trgPath, new Path[]{ smallFile });
        fStatus = nn.getFileInfo(trg);
        trgLen = fStatus.getLen();// new length

        // check number of blocks
        trgBlocks = nn.getBlockLocations(trg, 0, trgLen).locatedBlockCount();
        Assert.assertEquals(trgBlocks, (totalBlocks + 1));
        // and length
        Assert.assertEquals(trgLen, (totalLen + sFileLen));
    }

    /**
     * Test that the concat operation is properly persisted in the
     * edit log, and properly replayed on restart.
     */
    @Test
    public void testConcatInEditLog() throws Exception {
        final Path TEST_DIR = new Path("/testConcatInEditLog");
        final long FILE_LEN = TestHDFSConcat.blockSize;
        // 1. Concat some files
        Path[] srcFiles = new Path[3];
        for (int i = 0; i < (srcFiles.length); i++) {
            Path path = new Path(TEST_DIR, ("src-" + i));
            DFSTestUtil.createFile(dfs, path, FILE_LEN, TestHDFSConcat.REPL_FACTOR, 1);
            srcFiles[i] = path;
        }
        Path targetFile = new Path(TEST_DIR, "target");
        DFSTestUtil.createFile(dfs, targetFile, FILE_LEN, TestHDFSConcat.REPL_FACTOR, 1);
        dfs.concat(targetFile, srcFiles);
        // 2. Verify the concat operation basically worked, and record
        // file status.
        Assert.assertTrue(dfs.exists(targetFile));
        FileStatus origStatus = dfs.getFileStatus(targetFile);
        // 3. Restart NN to force replay from edit log
        cluster.restartNameNode(true);
        // 4. Verify concat operation was replayed correctly and file status
        // did not change.
        Assert.assertTrue(dfs.exists(targetFile));
        Assert.assertFalse(dfs.exists(srcFiles[0]));
        FileStatus statusAfterRestart = dfs.getFileStatus(targetFile);
        Assert.assertEquals(origStatus.getModificationTime(), statusAfterRestart.getModificationTime());
    }

    // test case when final block is not of a full length
    @Test
    public void testConcatNotCompleteBlock() throws IOException {
        long trgFileLen = (TestHDFSConcat.blockSize) * 3;
        long srcFileLen = ((TestHDFSConcat.blockSize) * 3) + 20;// block at the end - not full

        // create first file
        String name1 = "/trg";
        String name2 = "/src";
        Path filePath1 = new Path(name1);
        DFSTestUtil.createFile(dfs, filePath1, trgFileLen, TestHDFSConcat.REPL_FACTOR, 1);
        HdfsFileStatus fStatus = nn.getFileInfo(name1);
        long fileLen = fStatus.getLen();
        Assert.assertEquals(fileLen, trgFileLen);
        // read the file
        FSDataInputStream stm = dfs.open(filePath1);
        byte[] byteFile1 = new byte[((int) (trgFileLen))];
        stm.readFully(0, byteFile1);
        stm.close();
        LocatedBlocks lb1 = nn.getBlockLocations(name1, 0, trgFileLen);
        Path filePath2 = new Path(name2);
        DFSTestUtil.createFile(dfs, filePath2, srcFileLen, TestHDFSConcat.REPL_FACTOR, 1);
        fStatus = nn.getFileInfo(name2);
        fileLen = fStatus.getLen();
        Assert.assertEquals(srcFileLen, fileLen);
        // read the file
        stm = dfs.open(filePath2);
        byte[] byteFile2 = new byte[((int) (srcFileLen))];
        stm.readFully(0, byteFile2);
        stm.close();
        LocatedBlocks lb2 = nn.getBlockLocations(name2, 0, srcFileLen);
        System.out.println(((("trg len=" + trgFileLen) + "; src len=") + srcFileLen));
        // move the blocks
        dfs.concat(filePath1, new Path[]{ filePath2 });
        long totalLen = trgFileLen + srcFileLen;
        fStatus = nn.getFileInfo(name1);
        fileLen = fStatus.getLen();
        // read the resulting file
        stm = dfs.open(filePath1);
        byte[] byteFileConcat = new byte[((int) (fileLen))];
        stm.readFully(0, byteFileConcat);
        stm.close();
        LocatedBlocks lbConcat = nn.getBlockLocations(name1, 0, fileLen);
        // verifications
        // 1. number of blocks
        Assert.assertEquals(lbConcat.locatedBlockCount(), ((lb1.locatedBlockCount()) + (lb2.locatedBlockCount())));
        // 2. file lengths
        System.out.println(((("file1 len=" + fileLen) + "; total len=") + totalLen));
        Assert.assertEquals(fileLen, totalLen);
        // 3. removal of the src file
        fStatus = nn.getFileInfo(name2);
        Assert.assertNull((("File " + name2) + "still exists"), fStatus);// file shouldn't exist

        // 4. content
        checkFileContent(byteFileConcat, new byte[][]{ byteFile1, byteFile2 });
    }

    /**
     * test illegal args cases
     */
    @Test
    public void testIllegalArg() throws IOException {
        long fileLen = (TestHDFSConcat.blockSize) * 3;
        Path parentDir = new Path("/parentTrg");
        Assert.assertTrue(dfs.mkdirs(parentDir));
        Path trg = new Path(parentDir, "trg");
        DFSTestUtil.createFile(dfs, trg, fileLen, TestHDFSConcat.REPL_FACTOR, 1);
        // must be in the same dir
        {
            // create first file
            Path dir1 = new Path("/dir1");
            Assert.assertTrue(dfs.mkdirs(dir1));
            Path src = new Path(dir1, "src");
            DFSTestUtil.createFile(dfs, src, fileLen, TestHDFSConcat.REPL_FACTOR, 1);
            try {
                dfs.concat(trg, new Path[]{ src });
                Assert.fail("didn't fail for src and trg in different directories");
            } catch (Exception e) {
                // expected
            }
        }
        // non existing file
        try {
            dfs.concat(trg, new Path[]{ new Path("test1/a") });// non existing file

            Assert.fail("didn't fail with invalid arguments");
        } catch (Exception e) {
            // expected
        }
        // empty arg list
        try {
            dfs.concat(trg, new Path[]{  });// empty array

            Assert.fail("didn't fail with invalid arguments");
        } catch (Exception e) {
            // exspected
        }
        // the source file's preferred block size cannot be greater than the target
        {
            final Path src1 = new Path(parentDir, "src1");
            DFSTestUtil.createFile(dfs, src1, fileLen, TestHDFSConcat.REPL_FACTOR, 0L);
            final Path src2 = new Path(parentDir, "src2");
            // create a file whose preferred block size is greater than the target
            DFSTestUtil.createFile(dfs, src2, 1024, fileLen, ((dfs.getDefaultBlockSize(trg)) * 2), TestHDFSConcat.REPL_FACTOR, 0L);
            try {
                dfs.concat(trg, new Path[]{ src1, src2 });
                Assert.fail("didn't fail for src with greater preferred block size");
            } catch (Exception e) {
                GenericTestUtils.assertExceptionContains("preferred block size", e);
            }
        }
    }

    /**
     * make sure we update the quota correctly after concat
     */
    @Test
    public void testConcatWithQuotaDecrease() throws IOException {
        final short srcRepl = 3;// note this is different with REPL_FACTOR

        final int srcNum = 10;
        final Path foo = new Path("/foo");
        final Path[] srcs = new Path[srcNum];
        final Path target = new Path(foo, "target");
        DFSTestUtil.createFile(dfs, target, TestHDFSConcat.blockSize, TestHDFSConcat.REPL_FACTOR, 0L);
        dfs.setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        for (int i = 0; i < srcNum; i++) {
            srcs[i] = new Path(foo, ("src" + i));
            DFSTestUtil.createFile(dfs, srcs[i], ((TestHDFSConcat.blockSize) * 2), srcRepl, 0L);
        }
        ContentSummary summary = dfs.getContentSummary(foo);
        Assert.assertEquals(11, summary.getFileCount());
        Assert.assertEquals((((TestHDFSConcat.blockSize) * (TestHDFSConcat.REPL_FACTOR)) + ((((TestHDFSConcat.blockSize) * 2) * srcRepl) * srcNum)), summary.getSpaceConsumed());
        dfs.concat(target, srcs);
        summary = dfs.getContentSummary(foo);
        Assert.assertEquals(1, summary.getFileCount());
        Assert.assertEquals((((TestHDFSConcat.blockSize) * (TestHDFSConcat.REPL_FACTOR)) + ((((TestHDFSConcat.blockSize) * 2) * (TestHDFSConcat.REPL_FACTOR)) * srcNum)), summary.getSpaceConsumed());
    }

    @Test
    public void testConcatWithQuotaIncrease() throws IOException {
        final short repl = 3;
        final int srcNum = 10;
        final Path foo = new Path("/foo");
        final Path bar = new Path(foo, "bar");
        final Path[] srcs = new Path[srcNum];
        final Path target = new Path(bar, "target");
        DFSTestUtil.createFile(dfs, target, TestHDFSConcat.blockSize, repl, 0L);
        final long dsQuota = ((TestHDFSConcat.blockSize) * repl) + (((TestHDFSConcat.blockSize) * srcNum) * (TestHDFSConcat.REPL_FACTOR));
        dfs.setQuota(foo, ((Long.MAX_VALUE) - 1), dsQuota);
        for (int i = 0; i < srcNum; i++) {
            srcs[i] = new Path(bar, ("src" + i));
            DFSTestUtil.createFile(dfs, srcs[i], TestHDFSConcat.blockSize, TestHDFSConcat.REPL_FACTOR, 0L);
        }
        ContentSummary summary = dfs.getContentSummary(bar);
        Assert.assertEquals(11, summary.getFileCount());
        Assert.assertEquals(dsQuota, summary.getSpaceConsumed());
        try {
            dfs.concat(target, srcs);
            Assert.fail("QuotaExceededException expected");
        } catch (RemoteException e) {
            Assert.assertTrue(((e.unwrapRemoteException()) instanceof QuotaExceededException));
        }
        dfs.setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        dfs.concat(target, srcs);
        summary = dfs.getContentSummary(bar);
        Assert.assertEquals(1, summary.getFileCount());
        Assert.assertEquals((((TestHDFSConcat.blockSize) * repl) * (srcNum + 1)), summary.getSpaceConsumed());
    }

    @Test
    public void testConcatRelativeTargetPath() throws IOException {
        Path dir = new Path("/dir");
        Path trg = new Path("trg");
        Path src = new Path(dir, "src");
        dfs.setWorkingDirectory(dir);
        DFSTestUtil.createFile(dfs, trg, TestHDFSConcat.blockSize, TestHDFSConcat.REPL_FACTOR, 1);
        DFSTestUtil.createFile(dfs, src, TestHDFSConcat.blockSize, TestHDFSConcat.REPL_FACTOR, 1);
        dfs.concat(trg, new Path[]{ src });
        Assert.assertEquals(((TestHDFSConcat.blockSize) * 2), dfs.getFileStatus(trg).getLen());
        Assert.assertFalse(dfs.exists(src));
    }

    @Test(timeout = 30000)
    public void testConcatReservedRelativePaths() throws IOException {
        String testPathDir = "/.reserved/raw/ezone";
        Path dir = new Path(testPathDir);
        dfs.mkdirs(dir);
        Path trg = new Path(testPathDir, "trg");
        Path src = new Path(testPathDir, "src");
        DFSTestUtil.createFile(dfs, trg, TestHDFSConcat.blockSize, TestHDFSConcat.REPL_FACTOR, 1);
        DFSTestUtil.createFile(dfs, src, TestHDFSConcat.blockSize, TestHDFSConcat.REPL_FACTOR, 1);
        try {
            dfs.concat(trg, new Path[]{ src });
            Assert.fail("Must throw Exception!");
        } catch (IOException e) {
            String errMsg = (("Concat operation doesn't support " + (DOT_RESERVED_STRING)) + " relative path : ") + trg;
            GenericTestUtils.assertExceptionContains(errMsg, e);
        }
    }
}

