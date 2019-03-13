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
package org.apache.hadoop.fs.contract.s3a;


import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.AbstractContractSeekTest;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3AInputPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * S3A contract tests covering file seek.
 */
@RunWith(Parameterized.class)
public class ITestS3AContractSeek extends AbstractContractSeekTest {
    private static final Logger LOG = LoggerFactory.getLogger(ITestS3AContractSeek.class);

    protected static final int READAHEAD = 1024;

    private final String seekPolicy;

    public static final int DATASET_LEN = (ITestS3AContractSeek.READAHEAD) * 2;

    public static final byte[] DATASET = ContractTestUtils.dataset(ITestS3AContractSeek.DATASET_LEN, 'a', 32);

    /**
     * Run the test with a chosen seek policy.
     *
     * @param seekPolicy
     * 		fadvise policy to use.
     */
    public ITestS3AContractSeek(final String seekPolicy) {
        this.seekPolicy = seekPolicy;
    }

    @Test
    public void testReadPolicyInFS() throws Throwable {
        describe("Verify the read policy is being consistently set");
        S3AFileSystem fs = getFileSystem();
        assertEquals(S3AInputPolicy.getPolicy(seekPolicy), fs.getInputPolicy());
    }

    /**
     * Test for HADOOP-16109: Parquet reading S3AFileSystem causes EOF.
     * This sets up a read which will span the active readahead and,
     * in random IO mode, a subsequent GET.
     */
    @Test
    public void testReadAcrossReadahead() throws Throwable {
        describe(("Sets up a read which will span the active readahead" + " and the rest of the file."));
        Path path = path("testReadAcrossReadahead");
        writeTestDataset(path);
        FileSystem fs = getFileSystem();
        // forward seek reading across readahead boundary
        try (FSDataInputStream in = fs.open(path)) {
            final byte[] temp = new byte[5];
            in.readByte();
            int offset = (ITestS3AContractSeek.READAHEAD) - 1;
            in.readFully(offset, temp);// <-- works

            assertDatasetEquals(offset, "read spanning boundary", temp, temp.length);
        }
        // Read exactly on the the boundary
        try (FSDataInputStream in = fs.open(path)) {
            final byte[] temp = new byte[5];
            readAtEndAndReturn(in);
            assertEquals("current position", 1, ((int) (in.getPos())));
            in.readFully(ITestS3AContractSeek.READAHEAD, temp);
            assertDatasetEquals(ITestS3AContractSeek.READAHEAD, "read exactly on boundary", temp, temp.length);
        }
    }

    /**
     * Read across the end of the read buffer using the readByte call,
     * which will read a single byte only.
     */
    @Test
    public void testReadSingleByteAcrossReadahead() throws Throwable {
        describe("Read over boundary using read()/readByte() calls.");
        Path path = path("testReadSingleByteAcrossReadahead");
        writeTestDataset(path);
        FileSystem fs = getFileSystem();
        try (FSDataInputStream in = fs.open(path)) {
            final byte[] b0 = new byte[1];
            readAtEndAndReturn(in);
            in.seek(((ITestS3AContractSeek.READAHEAD) - 1));
            b0[0] = in.readByte();
            assertDatasetEquals(((ITestS3AContractSeek.READAHEAD) - 1), "read before end of boundary", b0, b0.length);
            b0[0] = in.readByte();
            assertDatasetEquals(ITestS3AContractSeek.READAHEAD, "read at end of boundary", b0, b0.length);
            b0[0] = in.readByte();
            assertDatasetEquals(((ITestS3AContractSeek.READAHEAD) + 1), "read after end of boundary", b0, b0.length);
        }
    }

    @Test
    public void testSeekToReadaheadAndRead() throws Throwable {
        describe(("Seek to just before readahead limit and call" + " InputStream.read(byte[])"));
        Path path = path("testSeekToReadaheadAndRead");
        FileSystem fs = getFileSystem();
        writeTestDataset(path);
        try (FSDataInputStream in = fs.open(path)) {
            readAtEndAndReturn(in);
            final byte[] temp = new byte[5];
            int offset = (ITestS3AContractSeek.READAHEAD) - 1;
            in.seek(offset);
            // expect to read at least one byte.
            int l = in.read(temp);
            assertTrue("Reading in temp data", (l > 0));
            ITestS3AContractSeek.LOG.info("Read of byte array at offset {} returned {} bytes", offset, l);
            assertDatasetEquals(offset, "read at end of boundary", temp, l);
        }
    }

    @Test
    public void testSeekToReadaheadExactlyAndRead() throws Throwable {
        describe(("Seek to exactly the readahead limit and call" + " InputStream.read(byte[])"));
        Path path = path("testSeekToReadaheadExactlyAndRead");
        FileSystem fs = getFileSystem();
        writeTestDataset(path);
        try (FSDataInputStream in = fs.open(path)) {
            readAtEndAndReturn(in);
            final byte[] temp = new byte[5];
            int offset = ITestS3AContractSeek.READAHEAD;
            in.seek(offset);
            // expect to read at least one byte.
            int l = in.read(temp);
            ITestS3AContractSeek.LOG.info("Read of byte array at offset {} returned {} bytes", offset, l);
            assertTrue("Reading in temp data", (l > 0));
            assertDatasetEquals(offset, "read at end of boundary", temp, l);
        }
    }

    @Test
    public void testSeekToReadaheadExactlyAndReadByte() throws Throwable {
        describe(("Seek to exactly the readahead limit and call" + " readByte()"));
        Path path = path("testSeekToReadaheadExactlyAndReadByte");
        FileSystem fs = getFileSystem();
        writeTestDataset(path);
        try (FSDataInputStream in = fs.open(path)) {
            readAtEndAndReturn(in);
            final byte[] temp = new byte[1];
            int offset = ITestS3AContractSeek.READAHEAD;
            in.seek(offset);
            // expect to read a byte successfully.
            temp[0] = in.readByte();
            assertDatasetEquals(ITestS3AContractSeek.READAHEAD, "read at end of boundary", temp, 1);
            ITestS3AContractSeek.LOG.info("Read of byte at offset {} returned expected value", offset);
        }
    }
}

