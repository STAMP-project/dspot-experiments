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
package org.apache.hadoop.fs.aliyun.oss;


import ReadBuffer.STATUS.ERROR;
import ReadBuffer.STATUS.SUCCESS;
import java.util.Random;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Constants.MULTIPART_DOWNLOAD_SIZE_DEFAULT;


/**
 * Tests basic functionality for AliyunOSSInputStream, including seeking and
 * reading files.
 */
public class TestAliyunOSSInputStream {
    private FileSystem fs;

    private static final Logger LOG = LoggerFactory.getLogger(TestAliyunOSSInputStream.class);

    private static String testRootPath = AliyunOSSTestUtils.generateUniqueTestPath();

    @Rule
    public Timeout testTimeout = new Timeout(((30 * 60) * 1000));

    @Test
    public void testSeekFile() throws Exception {
        Path smallSeekFile = setPath("/test/smallSeekFile.txt");
        long size = (5 * 1024) * 1024;
        ContractTestUtils.generateTestFile(this.fs, smallSeekFile, size, 256, 255);
        TestAliyunOSSInputStream.LOG.info("5MB file created: smallSeekFile.txt");
        FSDataInputStream instream = this.fs.open(smallSeekFile);
        int seekTimes = 5;
        TestAliyunOSSInputStream.LOG.info("multiple fold position seeking test...:");
        for (int i = 0; i < seekTimes; i++) {
            long pos = (size / (seekTimes - i)) - 1;
            TestAliyunOSSInputStream.LOG.info(("begin seeking for pos: " + pos));
            instream.seek(pos);
            Assert.assertTrue(((("expected position at:" + pos) + ", but got:") + (instream.getPos())), ((instream.getPos()) == pos));
            TestAliyunOSSInputStream.LOG.info(("completed seeking at pos: " + (instream.getPos())));
        }
        TestAliyunOSSInputStream.LOG.info("random position seeking test...:");
        Random rand = new Random();
        for (int i = 0; i < seekTimes; i++) {
            long pos = (Math.abs(rand.nextLong())) % size;
            TestAliyunOSSInputStream.LOG.info(("begin seeking for pos: " + pos));
            instream.seek(pos);
            Assert.assertTrue(((("expected position at:" + pos) + ", but got:") + (instream.getPos())), ((instream.getPos()) == pos));
            TestAliyunOSSInputStream.LOG.info(("completed seeking at pos: " + (instream.getPos())));
        }
        IOUtils.closeStream(instream);
    }

    @Test
    public void testSequentialAndRandomRead() throws Exception {
        Path smallSeekFile = setPath("/test/smallSeekFile.txt");
        long size = (5 * 1024) * 1024;
        ContractTestUtils.generateTestFile(this.fs, smallSeekFile, size, 256, 255);
        TestAliyunOSSInputStream.LOG.info("5MB file created: smallSeekFile.txt");
        FSDataInputStream fsDataInputStream = this.fs.open(smallSeekFile);
        AliyunOSSInputStream in = ((AliyunOSSInputStream) (fsDataInputStream.getWrappedStream()));
        Assert.assertTrue(((("expected position at:" + 0) + ", but got:") + (fsDataInputStream.getPos())), ((fsDataInputStream.getPos()) == 0));
        Assert.assertTrue(((("expected position at:" + (MULTIPART_DOWNLOAD_SIZE_DEFAULT)) + ", but got:") + (in.getExpectNextPos())), ((in.getExpectNextPos()) == (MULTIPART_DOWNLOAD_SIZE_DEFAULT)));
        fsDataInputStream.seek(((4 * 1024) * 1024));
        Assert.assertTrue((((("expected position at:" + ((4 * 1024) * 1024)) + (MULTIPART_DOWNLOAD_SIZE_DEFAULT)) + ", but got:") + (in.getExpectNextPos())), ((in.getExpectNextPos()) == (((4 * 1024) * 1024) + (MULTIPART_DOWNLOAD_SIZE_DEFAULT))));
        IOUtils.closeStream(fsDataInputStream);
    }

    @Test
    public void testOSSFileReaderTask() throws Exception {
        Path smallSeekFile = setPath("/test/smallSeekFileOSSFileReader.txt");
        long size = (5 * 1024) * 1024;
        ContractTestUtils.generateTestFile(this.fs, smallSeekFile, size, 256, 255);
        TestAliyunOSSInputStream.LOG.info("5MB file created: smallSeekFileOSSFileReader.txt");
        ReadBuffer readBuffer = new ReadBuffer(12, 24);
        AliyunOSSFileReaderTask task = new AliyunOSSFileReaderTask("1", getStore(), readBuffer);
        // NullPointerException, fail
        task.run();
        Assert.assertEquals(readBuffer.getStatus(), ERROR);
        // OK
        task = new AliyunOSSFileReaderTask("test/test/smallSeekFileOSSFileReader.txt", getStore(), readBuffer);
        task.run();
        Assert.assertEquals(readBuffer.getStatus(), SUCCESS);
    }

    @Test
    public void testReadFile() throws Exception {
        final int bufLen = 256;
        final int sizeFlag = 5;
        String filename = ("readTestFile_" + sizeFlag) + ".txt";
        Path readTestFile = setPath(("/test/" + filename));
        long size = (sizeFlag * 1024) * 1024;
        ContractTestUtils.generateTestFile(this.fs, readTestFile, size, 256, 255);
        TestAliyunOSSInputStream.LOG.info(((sizeFlag + "MB file created: /test/") + filename));
        FSDataInputStream instream = this.fs.open(readTestFile);
        byte[] buf = new byte[bufLen];
        long bytesRead = 0;
        while (bytesRead < size) {
            int bytes;
            if ((size - bytesRead) < bufLen) {
                int remaining = ((int) (size - bytesRead));
                bytes = instream.read(buf, 0, remaining);
            } else {
                bytes = instream.read(buf, 0, bufLen);
            }
            bytesRead += bytes;
            if ((bytesRead % (1024 * 1024)) == 0) {
                int available = instream.available();
                int remaining = ((int) (size - bytesRead));
                Assert.assertTrue(((("expected remaining:" + remaining) + ", but got:") + available), (remaining == available));
                TestAliyunOSSInputStream.LOG.info((("Bytes read: " + (Math.round((((double) (bytesRead)) / (1024 * 1024))))) + " MB"));
            }
        } 
        Assert.assertTrue(((instream.available()) == 0));
        IOUtils.closeStream(instream);
    }

    @Test
    public void testDirectoryModifiedTime() throws Exception {
        Path emptyDirPath = setPath("/test/emptyDirectory");
        fs.mkdirs(emptyDirPath);
        FileStatus dirFileStatus = fs.getFileStatus(emptyDirPath);
        Assert.assertTrue("expected the empty dir is new", ((dirFileStatus.getModificationTime()) > 0L));
    }
}

