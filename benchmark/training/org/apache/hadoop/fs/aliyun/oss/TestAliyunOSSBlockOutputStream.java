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


import FileSystem.Statistics;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static Constants.MULTIPART_UPLOAD_PART_NUM_LIMIT;


/**
 * Tests regular and multi-part upload functionality for
 * AliyunOSSBlockOutputStream.
 */
public class TestAliyunOSSBlockOutputStream {
    private FileSystem fs;

    private static String testRootPath = AliyunOSSTestUtils.generateUniqueTestPath();

    @Rule
    public Timeout testTimeout = new Timeout(((30 * 60) * 1000));

    @Test
    public void testZeroByteUpload() throws IOException {
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), 0);
    }

    @Test
    public void testRegularUpload() throws IOException {
        FileSystem.clearStatistics();
        long size = 1024 * 1024;
        FileSystem.Statistics statistics = FileSystem.getStatistics("oss", AliyunOSSFileSystem.class);
        // This test is a little complicated for statistics, lifecycle is
        // generateTestFile
        // fs.create(getFileStatus)    read 1
        // output stream write         write 1
        // path exists(fs.exists)        read 1
        // verifyReceivedData
        // fs.open(getFileStatus)      read 1
        // input stream read           read 2(part size is 512K)
        // fs.delete
        // getFileStatus & delete & exists & create fake dir read 2, write 2
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), (size - 1));
        Assert.assertEquals(7, statistics.getReadOps());
        Assert.assertEquals((size - 1), statistics.getBytesRead());
        Assert.assertEquals(3, statistics.getWriteOps());
        Assert.assertEquals((size - 1), statistics.getBytesWritten());
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), size);
        Assert.assertEquals(14, statistics.getReadOps());
        Assert.assertEquals(((2 * size) - 1), statistics.getBytesRead());
        Assert.assertEquals(6, statistics.getWriteOps());
        Assert.assertEquals(((2 * size) - 1), statistics.getBytesWritten());
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), (size + 1));
        Assert.assertEquals(22, statistics.getReadOps());
        Assert.assertEquals((3 * size), statistics.getBytesRead());
        Assert.assertEquals(10, statistics.getWriteOps());
        Assert.assertEquals((3 * size), statistics.getBytesWritten());
    }

    @Test
    public void testMultiPartUpload() throws IOException {
        long size = (6 * 1024) * 1024;
        FileSystem.clearStatistics();
        FileSystem.Statistics statistics = FileSystem.getStatistics("oss", AliyunOSSFileSystem.class);
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), (size - 1));
        Assert.assertEquals(17, statistics.getReadOps());
        Assert.assertEquals((size - 1), statistics.getBytesRead());
        Assert.assertEquals(8, statistics.getWriteOps());
        Assert.assertEquals((size - 1), statistics.getBytesWritten());
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), size);
        Assert.assertEquals(34, statistics.getReadOps());
        Assert.assertEquals(((2 * size) - 1), statistics.getBytesRead());
        Assert.assertEquals(16, statistics.getWriteOps());
        Assert.assertEquals(((2 * size) - 1), statistics.getBytesWritten());
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), (size + 1));
        Assert.assertEquals(52, statistics.getReadOps());
        Assert.assertEquals((3 * size), statistics.getBytesRead());
        Assert.assertEquals(25, statistics.getWriteOps());
        Assert.assertEquals((3 * size), statistics.getBytesWritten());
    }

    @Test
    public void testMultiPartUploadConcurrent() throws IOException {
        FileSystem.clearStatistics();
        long size = ((50 * 1024) * 1024) - 1;
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), size);
        FileSystem.Statistics statistics = FileSystem.getStatistics("oss", AliyunOSSFileSystem.class);
        Assert.assertEquals(105, statistics.getReadOps());
        Assert.assertEquals(size, statistics.getBytesRead());
        Assert.assertEquals(52, statistics.getWriteOps());
        Assert.assertEquals(size, statistics.getBytesWritten());
    }

    @Test
    public void testHugeUpload() throws IOException {
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), ((Constants.MULTIPART_UPLOAD_PART_SIZE_DEFAULT) - 1));
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), Constants.MULTIPART_UPLOAD_PART_SIZE_DEFAULT);
        ContractTestUtils.createAndVerifyFile(fs, getTestPath(), ((Constants.MULTIPART_UPLOAD_PART_SIZE_DEFAULT) + 1));
    }

    @Test
    public void testMultiPartUploadLimit() throws IOException {
        long partSize1 = AliyunOSSUtils.calculatePartSize((10 * 1024), (100 * 1024));
        assert ((10 * 1024) / partSize1) < (MULTIPART_UPLOAD_PART_NUM_LIMIT);
        long partSize2 = AliyunOSSUtils.calculatePartSize((200 * 1024), (100 * 1024));
        assert ((200 * 1024) / partSize2) < (MULTIPART_UPLOAD_PART_NUM_LIMIT);
        long partSize3 = AliyunOSSUtils.calculatePartSize(((10000 * 100) * 1024), (100 * 1024));
        assert (((10000 * 100) * 1024) / partSize3) < (MULTIPART_UPLOAD_PART_NUM_LIMIT);
        long partSize4 = AliyunOSSUtils.calculatePartSize(((10001 * 100) * 1024), (100 * 1024));
        assert (((10001 * 100) * 1024) / partSize4) < (MULTIPART_UPLOAD_PART_NUM_LIMIT);
    }
}

