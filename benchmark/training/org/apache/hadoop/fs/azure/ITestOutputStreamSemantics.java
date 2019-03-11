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


import BlockListingFilter.COMMITTED;
import StreamCapabilities.DROPBEHIND;
import StreamCapabilities.HFLUSH;
import StreamCapabilities.HSYNC;
import StreamCapabilities.READAHEAD;
import StreamCapabilities.UNBUFFER;
import com.microsoft.azure.storage.blob.BlockEntry;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test semantics of functions flush, hflush, hsync, and close for block blobs,
 * block blobs with compaction, and page blobs.
 */
public class ITestOutputStreamSemantics extends AbstractWasbTestBase {
    private static final String PAGE_BLOB_DIR = "/pageblob";

    private static final String BLOCK_BLOB_DIR = "/blockblob";

    private static final String BLOCK_BLOB_COMPACTION_DIR = "/compaction";

    // Verify flush writes data to storage for Page Blobs
    @Test
    public void testPageBlobFlush() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.PAGE_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            stream.flush();
            // flush is asynchronous for Page Blob, so we need to
            // wait for it to complete
            SyncableDataOutputStream syncStream = ((SyncableDataOutputStream) (stream.getWrappedStream()));
            PageBlobOutputStream pageBlobStream = ((PageBlobOutputStream) (syncStream.getOutStream()));
            pageBlobStream.waitForLastFlushCompletion();
            validate(path, buffer, true);
        }
    }

    // Verify hflush writes data to storage for Page Blobs
    @Test
    public void testPageBlobHFlush() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.PAGE_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isPageBlobStreamWrapper(stream));
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            stream.hflush();
            validate(path, buffer, true);
        }
    }

    // HSync must write data to storage for Page Blobs
    @Test
    public void testPageBlobHSync() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.PAGE_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isPageBlobStreamWrapper(stream));
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            stream.hsync();
            validate(path, buffer, true);
        }
    }

    // Close must write data to storage for Page Blobs
    @Test
    public void testPageBlobClose() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.PAGE_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isPageBlobStreamWrapper(stream));
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            stream.close();
            validate(path, buffer, true);
        }
    }

    // Page Blobs have StreamCapabilities.HFLUSH and StreamCapabilities.HSYNC.
    @Test
    public void testPageBlobCapabilities() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.PAGE_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(stream.hasCapability(HFLUSH));
            Assert.assertTrue(stream.hasCapability(HSYNC));
            Assert.assertFalse(stream.hasCapability(DROPBEHIND));
            Assert.assertFalse(stream.hasCapability(READAHEAD));
            Assert.assertFalse(stream.hasCapability(UNBUFFER));
            stream.write(getRandomBytes());
        }
    }

    // Verify flush does not write data to storage for Block Blobs
    @Test
    public void testBlockBlobFlush() throws Exception {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_DIR);
        byte[] buffer = getRandomBytes();
        try (FSDataOutputStream stream = fs.create(path)) {
            for (int i = 0; i < 10; i++) {
                stream.write(buffer);
                stream.flush();
            }
        }
        String blobPath = path.toUri().getPath();
        // Create a blob reference to read and validate the block list
        CloudBlockBlob blob = testAccount.getBlobReference(blobPath.substring(1));
        // after the stream is closed, the block list should be non-empty
        ArrayList<BlockEntry> blockList = blob.downloadBlockList(COMMITTED, null, null, null);
        Assert.assertEquals(1, blockList.size());
    }

    // Verify hflush does not write data to storage for Block Blobs
    @Test
    public void testBlockBlobHFlush() throws Exception {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_DIR);
        byte[] buffer = getRandomBytes();
        try (FSDataOutputStream stream = fs.create(path)) {
            for (int i = 0; i < 10; i++) {
                stream.write(buffer);
                stream.hflush();
            }
        }
        String blobPath = path.toUri().getPath();
        // Create a blob reference to read and validate the block list
        CloudBlockBlob blob = testAccount.getBlobReference(blobPath.substring(1));
        // after the stream is closed, the block list should be non-empty
        ArrayList<BlockEntry> blockList = blob.downloadBlockList(COMMITTED, null, null, null);
        Assert.assertEquals(1, blockList.size());
    }

    // Verify hsync does not write data to storage for Block Blobs
    @Test
    public void testBlockBlobHSync() throws Exception {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_DIR);
        byte[] buffer = getRandomBytes();
        try (FSDataOutputStream stream = fs.create(path)) {
            for (int i = 0; i < 10; i++) {
                stream.write(buffer);
                stream.hsync();
            }
        }
        String blobPath = path.toUri().getPath();
        // Create a blob reference to read and validate the block list
        CloudBlockBlob blob = testAccount.getBlobReference(blobPath.substring(1));
        // after the stream is closed, the block list should be non-empty
        ArrayList<BlockEntry> blockList = blob.downloadBlockList(COMMITTED, null, null, null);
        Assert.assertEquals(1, blockList.size());
    }

    // Close must write data to storage for Block Blobs
    @Test
    public void testBlockBlobClose() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            stream.close();
            validate(path, buffer, true);
        }
    }

    // Block Blobs do not have any StreamCapabilities.
    @Test
    public void testBlockBlobCapabilities() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertFalse(stream.hasCapability(HFLUSH));
            Assert.assertFalse(stream.hasCapability(HSYNC));
            Assert.assertFalse(stream.hasCapability(DROPBEHIND));
            Assert.assertFalse(stream.hasCapability(READAHEAD));
            Assert.assertFalse(stream.hasCapability(UNBUFFER));
            stream.write(getRandomBytes());
        }
    }

    // Verify flush writes data to storage for Block Blobs with compaction
    @Test
    public void testBlockBlobCompactionFlush() throws Exception {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_COMPACTION_DIR);
        byte[] buffer = getRandomBytes();
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isBlockBlobAppendStreamWrapper(stream));
            for (int i = 0; i < 10; i++) {
                stream.write(buffer);
                stream.flush();
            }
        }
        String blobPath = path.toUri().getPath();
        // Create a blob reference to read and validate the block list
        CloudBlockBlob blob = testAccount.getBlobReference(blobPath.substring(1));
        // after the stream is closed, the block list should be non-empty
        ArrayList<BlockEntry> blockList = blob.downloadBlockList(COMMITTED, null, null, null);
        Assert.assertEquals(1, blockList.size());
    }

    // Verify hflush writes data to storage for Block Blobs with Compaction
    @Test
    public void testBlockBlobCompactionHFlush() throws Exception {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_COMPACTION_DIR);
        byte[] buffer = getRandomBytes();
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isBlockBlobAppendStreamWrapper(stream));
            for (int i = 0; i < 10; i++) {
                stream.write(buffer);
                stream.hflush();
            }
        }
        String blobPath = path.toUri().getPath();
        // Create a blob reference to read and validate the block list
        CloudBlockBlob blob = testAccount.getBlobReference(blobPath.substring(1));
        // after the stream is closed, the block list should be non-empty
        ArrayList<BlockEntry> blockList = blob.downloadBlockList(COMMITTED, null, null, null);
        Assert.assertEquals(10, blockList.size());
    }

    // Verify hsync writes data to storage for Block Blobs with compaction
    @Test
    public void testBlockBlobCompactionHSync() throws Exception {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_COMPACTION_DIR);
        byte[] buffer = getRandomBytes();
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isBlockBlobAppendStreamWrapper(stream));
            for (int i = 0; i < 10; i++) {
                stream.write(buffer);
                stream.hsync();
            }
        }
        String blobPath = path.toUri().getPath();
        // Create a blob reference to read and validate the block list
        CloudBlockBlob blob = testAccount.getBlobReference(blobPath.substring(1));
        // after the stream is closed, the block list should be non-empty
        ArrayList<BlockEntry> blockList = blob.downloadBlockList(COMMITTED, null, null, null);
        Assert.assertEquals(10, blockList.size());
    }

    // Close must write data to storage for Block Blobs with compaction
    @Test
    public void testBlockBlobCompactionClose() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_COMPACTION_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isBlockBlobAppendStreamWrapper(stream));
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            stream.close();
            validate(path, buffer, true);
        }
    }

    // Block Blobs with Compaction have StreamCapabilities.HFLUSH and HSYNC.
    @Test
    public void testBlockBlobCompactionCapabilities() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_COMPACTION_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(stream.hasCapability(HFLUSH));
            Assert.assertTrue(stream.hasCapability(HSYNC));
            Assert.assertFalse(stream.hasCapability(DROPBEHIND));
            Assert.assertFalse(stream.hasCapability(READAHEAD));
            Assert.assertFalse(stream.hasCapability(UNBUFFER));
            stream.write(getRandomBytes());
        }
    }

    // A small write does not write data to storage for Page Blobs
    @Test
    public void testPageBlobSmallWrite() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.PAGE_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isPageBlobStreamWrapper(stream));
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            validate(path, buffer, false);
        }
    }

    // A small write does not write data to storage for Block Blobs
    @Test
    public void testBlockBlobSmallWrite() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            validate(path, buffer, false);
        }
    }

    // A small write does not write data to storage for Block Blobs
    // with Compaction
    @Test
    public void testBlockBlobCompactionSmallWrite() throws IOException {
        Path path = getBlobPathWithTestName(ITestOutputStreamSemantics.BLOCK_BLOB_COMPACTION_DIR);
        try (FSDataOutputStream stream = fs.create(path)) {
            Assert.assertTrue(isBlockBlobAppendStreamWrapper(stream));
            byte[] buffer = getRandomBytes();
            stream.write(buffer);
            validate(path, buffer, false);
        }
    }
}

