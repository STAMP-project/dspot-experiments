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
package org.apache.hadoop.util;


import DataChecksum.Type;
import java.nio.ByteBuffer;
import org.apache.hadoop.fs.ChecksumException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestNativeCrc32 {
    private static final long BASE_POSITION = 0;

    private static final int IO_BYTES_PER_CHECKSUM_DEFAULT = 512;

    private static final String IO_BYTES_PER_CHECKSUM_KEY = "io.bytes.per.checksum";

    private static final int NUM_CHUNKS = 3;

    private final Type checksumType;

    private int bytesPerChecksum;

    private String fileName;

    private ByteBuffer data;

    private ByteBuffer checksums;

    private DataChecksum checksum;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public TestNativeCrc32(DataChecksum.Type checksumType) {
        this.checksumType = checksumType;
    }

    @Test
    public void testVerifyChunkedSumsSuccess() throws ChecksumException {
        allocateDirectByteBuffers();
        fillDataAndValidChecksums();
        NativeCrc32.verifyChunkedSums(bytesPerChecksum, checksumType.id, checksums, data, fileName, TestNativeCrc32.BASE_POSITION);
    }

    @Test
    public void testVerifyChunkedSumsFail() throws ChecksumException {
        allocateDirectByteBuffers();
        fillDataAndInvalidChecksums();
        exception.expect(ChecksumException.class);
        NativeCrc32.verifyChunkedSums(bytesPerChecksum, checksumType.id, checksums, data, fileName, TestNativeCrc32.BASE_POSITION);
    }

    @Test
    public void testVerifyChunkedSumsSuccessOddSize() throws ChecksumException {
        // Test checksum with an odd number of bytes. This is a corner case that
        // is often broken in checksum calculation, because there is an loop which
        // handles an even multiple or 4 or 8 bytes and then some additional code
        // to finish the few odd bytes at the end. This code can often be broken
        // but is never tested because we are always calling it with an even value
        // such as 512.
        (bytesPerChecksum)--;
        allocateDirectByteBuffers();
        fillDataAndValidChecksums();
        NativeCrc32.verifyChunkedSums(bytesPerChecksum, checksumType.id, checksums, data, fileName, TestNativeCrc32.BASE_POSITION);
        (bytesPerChecksum)++;
    }

    @Test
    public void testVerifyChunkedSumsByteArraySuccess() throws ChecksumException {
        allocateArrayByteBuffers();
        fillDataAndValidChecksums();
        NativeCrc32.verifyChunkedSumsByteArray(bytesPerChecksum, checksumType.id, checksums.array(), checksums.position(), data.array(), data.position(), data.remaining(), fileName, TestNativeCrc32.BASE_POSITION);
    }

    @Test
    public void testVerifyChunkedSumsByteArrayFail() throws ChecksumException {
        allocateArrayByteBuffers();
        fillDataAndInvalidChecksums();
        exception.expect(ChecksumException.class);
        NativeCrc32.verifyChunkedSumsByteArray(bytesPerChecksum, checksumType.id, checksums.array(), checksums.position(), data.array(), data.position(), data.remaining(), fileName, TestNativeCrc32.BASE_POSITION);
    }

    @Test
    public void testCalculateChunkedSumsSuccess() throws ChecksumException {
        allocateDirectByteBuffers();
        fillDataAndValidChecksums();
        NativeCrc32.calculateChunkedSums(bytesPerChecksum, checksumType.id, checksums, data);
    }

    @Test
    public void testCalculateChunkedSumsFail() throws ChecksumException {
        allocateDirectByteBuffers();
        fillDataAndInvalidChecksums();
        NativeCrc32.calculateChunkedSums(bytesPerChecksum, checksumType.id, checksums, data);
    }

    @Test
    public void testCalculateChunkedSumsByteArraySuccess() throws ChecksumException {
        allocateArrayByteBuffers();
        fillDataAndValidChecksums();
        NativeCrc32.calculateChunkedSumsByteArray(bytesPerChecksum, checksumType.id, checksums.array(), checksums.position(), data.array(), data.position(), data.remaining());
    }

    @Test
    public void testCalculateChunkedSumsByteArrayFail() throws ChecksumException {
        allocateArrayByteBuffers();
        fillDataAndInvalidChecksums();
        NativeCrc32.calculateChunkedSumsByteArray(bytesPerChecksum, checksumType.id, checksums.array(), checksums.position(), data.array(), data.position(), data.remaining());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNativeVerifyChunkedSumsSuccess() throws ChecksumException {
        allocateDirectByteBuffers();
        fillDataAndValidChecksums();
        NativeCrc32.nativeVerifyChunkedSums(bytesPerChecksum, checksumType.id, checksums, checksums.position(), data, data.position(), data.remaining(), fileName, TestNativeCrc32.BASE_POSITION);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNativeVerifyChunkedSumsFail() throws ChecksumException {
        allocateDirectByteBuffers();
        fillDataAndInvalidChecksums();
        exception.expect(ChecksumException.class);
        NativeCrc32.nativeVerifyChunkedSums(bytesPerChecksum, checksumType.id, checksums, checksums.position(), data, data.position(), data.remaining(), fileName, TestNativeCrc32.BASE_POSITION);
    }
}

