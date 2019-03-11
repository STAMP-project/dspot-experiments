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


import LocalFileSystemConfigKeys.LOCAL_FS_BYTES_PER_CHECKSUM_KEY;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestChecksumFileSystem {
    static final String TEST_ROOT_DIR = GenericTestUtils.getTempPath("work-dir/localfs");

    static LocalFileSystem localFs;

    @Test
    public void testgetChecksumLength() throws Exception {
        Assert.assertEquals(8, ChecksumFileSystem.getChecksumLength(0L, 512));
        Assert.assertEquals(12, ChecksumFileSystem.getChecksumLength(1L, 512));
        Assert.assertEquals(12, ChecksumFileSystem.getChecksumLength(512L, 512));
        Assert.assertEquals(16, ChecksumFileSystem.getChecksumLength(513L, 512));
        Assert.assertEquals(16, ChecksumFileSystem.getChecksumLength(1023L, 512));
        Assert.assertEquals(16, ChecksumFileSystem.getChecksumLength(1024L, 512));
        Assert.assertEquals(408, ChecksumFileSystem.getChecksumLength(100L, 1));
        Assert.assertEquals(4000000000008L, ChecksumFileSystem.getChecksumLength(10000000000000L, 10));
    }

    @Test
    public void testVerifyChecksum() throws Exception {
        Path testPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testPath");
        Path testPath11 = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testPath11");
        FSDataOutputStream fout = TestChecksumFileSystem.localFs.create(testPath);
        fout.write("testing".getBytes());
        fout.close();
        fout = TestChecksumFileSystem.localFs.create(testPath11);
        fout.write("testing you".getBytes());
        fout.close();
        // Exercise some boundary cases - a divisor of the chunk size
        // the chunk size, 2x chunk size, and +/-1 around these.
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 128);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 511);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 512);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 513);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1023);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1025);
        TestChecksumFileSystem.localFs.delete(TestChecksumFileSystem.localFs.getChecksumFile(testPath), true);
        Assert.assertTrue("checksum deleted", (!(TestChecksumFileSystem.localFs.exists(TestChecksumFileSystem.localFs.getChecksumFile(testPath)))));
        // copying the wrong checksum file
        FileUtil.copy(TestChecksumFileSystem.localFs, TestChecksumFileSystem.localFs.getChecksumFile(testPath11), TestChecksumFileSystem.localFs, TestChecksumFileSystem.localFs.getChecksumFile(testPath), false, true, TestChecksumFileSystem.localFs.getConf());
        Assert.assertTrue("checksum exists", TestChecksumFileSystem.localFs.exists(TestChecksumFileSystem.localFs.getChecksumFile(testPath)));
        boolean errorRead = false;
        try {
            FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024);
        } catch (ChecksumException ie) {
            errorRead = true;
        }
        Assert.assertTrue("error reading", errorRead);
        // now setting verify false, the read should succeed
        TestChecksumFileSystem.localFs.setVerifyChecksum(false);
        String str = FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024).toString();
        Assert.assertTrue("read", "testing".equals(str));
    }

    @Test
    public void testMultiChunkFile() throws Exception {
        Path testPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testMultiChunk");
        FSDataOutputStream fout = TestChecksumFileSystem.localFs.create(testPath);
        for (int i = 0; i < 1000; i++) {
            fout.write(("testing" + i).getBytes());
        }
        fout.close();
        // Exercise some boundary cases - a divisor of the chunk size
        // the chunk size, 2x chunk size, and +/-1 around these.
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 128);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 511);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 512);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 513);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1023);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024);
        FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1025);
    }

    /**
     * Test to ensure that if the checksum file is truncated, a
     * ChecksumException is thrown
     */
    @Test
    public void testTruncatedChecksum() throws Exception {
        Path testPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testtruncatedcrc");
        FSDataOutputStream fout = TestChecksumFileSystem.localFs.create(testPath);
        fout.write("testing truncation".getBytes());
        fout.close();
        // Read in the checksum
        Path checksumFile = TestChecksumFileSystem.localFs.getChecksumFile(testPath);
        FileSystem rawFs = TestChecksumFileSystem.localFs.getRawFileSystem();
        FSDataInputStream checksumStream = rawFs.open(checksumFile);
        byte[] buf = new byte[8192];
        int read = checksumStream.read(buf, 0, buf.length);
        checksumStream.close();
        // Now rewrite the checksum file with the last byte missing
        FSDataOutputStream replaceStream = rawFs.create(checksumFile);
        replaceStream.write(buf, 0, (read - 1));
        replaceStream.close();
        // Now reading the file should fail with a ChecksumException
        try {
            FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024);
            Assert.fail(("Did not throw a ChecksumException when reading truncated " + "crc file"));
        } catch (ChecksumException ie) {
        }
        // telling it not to verify checksums, should avoid issue.
        TestChecksumFileSystem.localFs.setVerifyChecksum(false);
        String str = FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024).toString();
        Assert.assertTrue("read", "testing truncation".equals(str));
    }

    @Test
    public void testStreamType() throws Exception {
        Path testPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testStreamType");
        TestChecksumFileSystem.localFs.create(testPath).close();
        FSDataInputStream in = null;
        TestChecksumFileSystem.localFs.setVerifyChecksum(true);
        in = TestChecksumFileSystem.localFs.open(testPath);
        Assert.assertTrue("stream is input checker", ((in.getWrappedStream()) instanceof FSInputChecker));
        TestChecksumFileSystem.localFs.setVerifyChecksum(false);
        in = TestChecksumFileSystem.localFs.open(testPath);
        Assert.assertFalse("stream is not input checker", ((in.getWrappedStream()) instanceof FSInputChecker));
    }

    @Test
    public void testCorruptedChecksum() throws Exception {
        Path testPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testCorruptChecksum");
        Path checksumPath = TestChecksumFileSystem.localFs.getChecksumFile(testPath);
        // write a file to generate checksum
        FSDataOutputStream out = TestChecksumFileSystem.localFs.create(testPath, true);
        out.write("testing 1 2 3".getBytes());
        out.close();
        Assert.assertTrue(TestChecksumFileSystem.localFs.exists(checksumPath));
        FileStatus stat = TestChecksumFileSystem.localFs.getFileStatus(checksumPath);
        // alter file directly so checksum is invalid
        out = TestChecksumFileSystem.localFs.getRawFileSystem().create(testPath, true);
        out.write("testing stale checksum".getBytes());
        out.close();
        Assert.assertTrue(TestChecksumFileSystem.localFs.exists(checksumPath));
        // checksum didn't change on disk
        Assert.assertEquals(stat, TestChecksumFileSystem.localFs.getFileStatus(checksumPath));
        Exception e = null;
        try {
            TestChecksumFileSystem.localFs.setVerifyChecksum(true);
            FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024);
        } catch (ChecksumException ce) {
            e = ce;
        } finally {
            Assert.assertNotNull("got checksum error", e);
        }
        TestChecksumFileSystem.localFs.setVerifyChecksum(false);
        String str = FileSystemTestHelper.readFile(TestChecksumFileSystem.localFs, testPath, 1024);
        Assert.assertEquals("testing stale checksum", str);
    }

    @Test
    public void testRenameFileToFile() throws Exception {
        Path srcPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testRenameSrc");
        Path dstPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testRenameDst");
        verifyRename(srcPath, dstPath, false);
    }

    @Test
    public void testRenameFileIntoDir() throws Exception {
        Path srcPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testRenameSrc");
        Path dstPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testRenameDir");
        TestChecksumFileSystem.localFs.mkdirs(dstPath);
        verifyRename(srcPath, dstPath, true);
    }

    @Test
    public void testRenameFileIntoDirFile() throws Exception {
        Path srcPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testRenameSrc");
        Path dstPath = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testRenameDir/testRenameDst");
        Assert.assertTrue(TestChecksumFileSystem.localFs.mkdirs(dstPath));
        verifyRename(srcPath, dstPath, false);
    }

    @Test
    public void testSetConf() {
        Configuration conf = new Configuration();
        conf.setInt(LOCAL_FS_BYTES_PER_CHECKSUM_KEY, 0);
        try {
            TestChecksumFileSystem.localFs.setConf(conf);
            Assert.fail("Should have failed because zero bytes per checksum is invalid");
        } catch (IllegalStateException ignored) {
        }
        conf.setInt(LOCAL_FS_BYTES_PER_CHECKSUM_KEY, (-1));
        try {
            TestChecksumFileSystem.localFs.setConf(conf);
            Assert.fail("Should have failed because negative bytes per checksum is invalid");
        } catch (IllegalStateException ignored) {
        }
        conf.setInt(LOCAL_FS_BYTES_PER_CHECKSUM_KEY, 512);
        TestChecksumFileSystem.localFs.setConf(conf);
    }

    @Test
    public void testSetPermissionCrc() throws Exception {
        FileSystem rawFs = TestChecksumFileSystem.localFs.getRawFileSystem();
        Path p = new Path(TestChecksumFileSystem.TEST_ROOT_DIR, "testCrcPermissions");
        TestChecksumFileSystem.localFs.createNewFile(p);
        Path crc = TestChecksumFileSystem.localFs.getChecksumFile(p);
        assert rawFs.exists(crc);
        for (short mode : Arrays.asList(((short) (438)), ((short) (432)), ((short) (384)))) {
            FsPermission perm = new FsPermission(mode);
            TestChecksumFileSystem.localFs.setPermission(p, perm);
            Assert.assertEquals(perm, TestChecksumFileSystem.localFs.getFileStatus(p).getPermission());
            Assert.assertEquals(perm, rawFs.getFileStatus(crc).getPermission());
        }
    }
}

