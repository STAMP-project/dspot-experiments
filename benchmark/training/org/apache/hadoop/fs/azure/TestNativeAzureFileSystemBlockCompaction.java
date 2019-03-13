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


import NativeAzureFileSystem.NativeAzureFsOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class that runs WASB block compaction process for block blobs.
 */
public class TestNativeAzureFileSystemBlockCompaction extends AbstractWasbTestBase {
    private static final String TEST_FILE = "/user/active/test.dat";

    private static final Path TEST_PATH = new Path(TestNativeAzureFileSystemBlockCompaction.TEST_FILE);

    private static final String TEST_FILE_NORMAL = "/user/normal/test.dat";

    private static final Path TEST_PATH_NORMAL = new Path(TestNativeAzureFileSystemBlockCompaction.TEST_FILE_NORMAL);

    private AzureBlobStorageTestAccount testAccount = null;

    @Test
    public void testCompactionDisabled() throws Throwable {
        try (FSDataOutputStream appendStream = fs.create(TestNativeAzureFileSystemBlockCompaction.TEST_PATH_NORMAL)) {
            // testing new file
            SyncableDataOutputStream dataOutputStream = null;
            OutputStream wrappedStream = appendStream.getWrappedStream();
            if (wrappedStream instanceof NativeAzureFileSystem.NativeAzureFsOutputStream) {
                NativeAzureFileSystem.NativeAzureFsOutputStream fsOutputStream = ((NativeAzureFileSystem.NativeAzureFsOutputStream) (wrappedStream));
                dataOutputStream = ((SyncableDataOutputStream) (fsOutputStream.getOutStream()));
            } else
                if (wrappedStream instanceof SyncableDataOutputStream) {
                    dataOutputStream = ((SyncableDataOutputStream) (wrappedStream));
                } else {
                    Assert.fail(((("Unable to determine type of " + wrappedStream) + " class of ") + (wrappedStream.getClass())));
                }

            Assert.assertFalse(("Data output stream is a BlockBlobAppendStream: " + dataOutputStream), ((dataOutputStream.getOutStream()) instanceof BlockBlobAppendStream));
        }
    }

    @Test
    public void testCompaction() throws Throwable {
        final int n2 = 2;
        final int n4 = 4;
        final int n10 = 10;
        final int n12 = 12;
        final int n14 = 14;
        final int n16 = 16;
        final int maxBlockSize = 16;
        final int compactionBlockCount = 4;
        ByteArrayOutputStream memStream = new ByteArrayOutputStream();
        try (FSDataOutputStream appendStream = fs.create(TestNativeAzureFileSystemBlockCompaction.TEST_PATH)) {
            // test new file
            BlockBlobAppendStream blockBlobStream = getBlockBlobAppendStream(appendStream);
            blockBlobStream.setMaxBlockSize(maxBlockSize);
            blockBlobStream.setCompactionBlockCount(compactionBlockCount);
            appendBlockList(appendStream, memStream, new int[]{ n2 });
            verifyBlockList(blockBlobStream, new int[]{ n2 });
            appendStream.hflush();
            verifyBlockList(blockBlobStream, new int[]{ n2 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n2, n4 });
            appendStream.hsync();
            verifyBlockList(blockBlobStream, new int[]{ n2, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n2, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n2, n4, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n4, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n2, n4, n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n10 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n10, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4, n4, n4, n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16 });
            appendBlockList(appendStream, memStream, new int[]{ n4, n4, n4, n4, n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n4, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n4, n4, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            appendStream.close();
            ContractTestUtils.verifyFileContents(fs, TestNativeAzureFileSystemBlockCompaction.TEST_PATH, memStream.toByteArray());
        }
        try (FSDataOutputStream appendStream = fs.append(TestNativeAzureFileSystemBlockCompaction.TEST_PATH)) {
            // test existing file
            BlockBlobAppendStream blockBlobStream = getBlockBlobAppendStream(appendStream);
            blockBlobStream.setMaxBlockSize(maxBlockSize);
            blockBlobStream.setCompactionBlockCount(compactionBlockCount);
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n16, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n16, n4, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n16, n4, n4, n4, n4 });
            appendBlockList(appendStream, memStream, new int[]{ n4 });
            verifyBlockList(blockBlobStream, new int[]{ n14, n12, n14, n16, n16, n16, n16, n4 });
            appendStream.close();
            ContractTestUtils.verifyFileContents(fs, TestNativeAzureFileSystemBlockCompaction.TEST_PATH, memStream.toByteArray());
        }
    }
}

