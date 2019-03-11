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
package org.apache.hadoop.fs.azure;


import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azure.integration.AbstractAzureScaleTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static PageBlobFormatHelpers.PAGE_HEADER_SIZE;
import static PageBlobFormatHelpers.PAGE_SIZE;


/**
 * Write data into a page blob and verify you can read back all of it
 * or just a part of it.
 */
public class ITestReadAndSeekPageBlobAfterWrite extends AbstractAzureScaleTest {
    private static final Logger LOG = LoggerFactory.getLogger(ITestReadAndSeekPageBlobAfterWrite.class);

    private FileSystem fs;

    private byte[] randomData;

    // Page blob physical page size
    private static final int PAGE_SIZE = PAGE_SIZE;

    // Size of data on page (excluding header)
    private static final int PAGE_DATA_SIZE = (ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE) - (PAGE_HEADER_SIZE);

    private static final int MAX_BYTES = 33554432;// maximum bytes in a file that we'll test


    private static final int MAX_PAGES = (ITestReadAndSeekPageBlobAfterWrite.MAX_BYTES) / (ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE);// maximum number of pages we'll test


    private Random rand = new Random();

    // A key with a prefix under /pageBlobs, which for the test file system will
    // force use of a page blob.
    private static final String KEY = "/pageBlobs/file.dat";

    // path of page blob file to read and write
    private Path blobPath;

    /**
     * Make sure the file name (key) is a page blob file name. If anybody changes that,
     * we need to come back and update this test class.
     */
    @Test
    public void testIsPageBlobFileName() {
        AzureNativeFileSystemStore store = getStore();
        String[] a = blobPath.toUri().getPath().split("/");
        String key2 = (a[1]) + "/";
        Assert.assertTrue(("Not a page blob: " + (blobPath)), store.isPageBlobKey(key2));
    }

    /**
     * For a set of different file sizes, write some random data to a page blob,
     * read it back, and compare that what was read is the same as what was written.
     */
    @Test
    public void testReadAfterWriteRandomData() throws IOException {
        // local shorthand
        final int pds = ITestReadAndSeekPageBlobAfterWrite.PAGE_DATA_SIZE;
        // Test for sizes at and near page boundaries
        int[] dataSizes = new int[]{ // on first page
        0, 1, 2, 3, // Near first physical page boundary (because the implementation
        // stores PDS + the page header size bytes on each page).
        pds - 1, pds, pds + 1, pds + 2, pds + 3, // near second physical page boundary
        (2 * pds) - 1, 2 * pds, (2 * pds) + 1, (2 * pds) + 2, (2 * pds) + 3, // near tenth physical page boundary
        (10 * pds) - 1, 10 * pds, (10 * pds) + 1, (10 * pds) + 2, (10 * pds) + 3, // test one big size, >> 4MB (an internal buffer size in the code)
        ITestReadAndSeekPageBlobAfterWrite.MAX_BYTES };
        for (int i : dataSizes) {
            testReadAfterWriteRandomData(i);
        }
    }

    /**
     * Write data to a page blob, open it, seek, and then read a range of data.
     * Then compare that the data read from that range is the same as the data originally written.
     */
    @Test
    public void testPageBlobSeekAndReadAfterWrite() throws IOException {
        writeRandomData(((ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE) * (ITestReadAndSeekPageBlobAfterWrite.MAX_PAGES)));
        int recordSize = 100;
        byte[] b = new byte[recordSize];
        try (FSDataInputStream stream = fs.open(blobPath)) {
            // Seek to a boundary around the middle of the 6th page
            int seekPosition = (5 * (ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE)) + 250;
            stream.seek(seekPosition);
            // Read a record's worth of bytes and verify results
            int bytesRead = stream.read(b);
            verifyReadRandomData(b, bytesRead, seekPosition, recordSize);
            // Seek to another spot and read a record greater than a page
            seekPosition = (10 * (ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE)) + 250;
            stream.seek(seekPosition);
            recordSize = 1000;
            b = new byte[recordSize];
            bytesRead = stream.read(b);
            verifyReadRandomData(b, bytesRead, seekPosition, recordSize);
            // Read the last 100 bytes of the file
            recordSize = 100;
            seekPosition = ((ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE) * (ITestReadAndSeekPageBlobAfterWrite.MAX_PAGES)) - recordSize;
            stream.seek(seekPosition);
            b = new byte[recordSize];
            bytesRead = stream.read(b);
            verifyReadRandomData(b, bytesRead, seekPosition, recordSize);
            // Read past the end of the file and we should get only partial data.
            recordSize = 100;
            seekPosition = (((ITestReadAndSeekPageBlobAfterWrite.PAGE_SIZE) * (ITestReadAndSeekPageBlobAfterWrite.MAX_PAGES)) - recordSize) + 50;
            stream.seek(seekPosition);
            b = new byte[recordSize];
            bytesRead = stream.read(b);
            Assert.assertEquals(50, bytesRead);
            // compare last 50 bytes written with those read
            byte[] tail = Arrays.copyOfRange(randomData, seekPosition, randomData.length);
            Assert.assertTrue(comparePrefix(tail, b, 50));
        }
    }

    // Test many small flushed writes interspersed with periodic hflush calls.
    // For manual testing, increase NUM_WRITES to a large number.
    // The goal for a long-running manual test is to make sure that it finishes
    // and the close() call does not time out. It also facilitates debugging into
    // hflush/hsync.
    @Test
    public void testManySmallWritesWithHFlush() throws IOException {
        writeAndReadOneFile(50, 100, 20);
    }

    // Test writing to a large file repeatedly as a stress test.
    // Set the repetitions to a larger number for manual testing
    // for a longer stress run.
    @Test
    public void testLargeFileStress() throws IOException {
        int numWrites = 32;
        int recordSize = 1024 * 1024;
        int syncInterval = 10;
        int repetitions = 1;
        for (int i = 0; i < repetitions; i++) {
            writeAndReadOneFile(numWrites, recordSize, syncInterval);
        }
    }
}

