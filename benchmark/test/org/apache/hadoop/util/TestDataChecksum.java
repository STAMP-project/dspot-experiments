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
import DataChecksum.Type.CRC32;
import DataChecksum.Type.CRC32C;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.fs.ChecksumException;
import org.junit.Assert;
import org.junit.Test;


public class TestDataChecksum {
    // Set up buffers that have some header and trailer before the
    // actual data or checksums, to make sure the code handles
    // buffer.position(), limit, etc correctly.
    private static final int SUMS_OFFSET_IN_BUFFER = 3;

    private static final int DATA_OFFSET_IN_BUFFER = 3;

    private static final int DATA_TRAILER_IN_BUFFER = 3;

    private static final int BYTES_PER_CHUNK = 512;

    private static final Type[] CHECKSUM_TYPES = new Type[]{ Type.CRC32, Type.CRC32C };

    @Test
    public void testBulkOps() throws Exception {
        for (DataChecksum.Type type : TestDataChecksum.CHECKSUM_TYPES) {
            System.err.println((("---- beginning tests with checksum type " + type) + "----"));
            DataChecksum checksum = DataChecksum.newDataChecksum(type, TestDataChecksum.BYTES_PER_CHUNK);
            for (boolean useDirect : new boolean[]{ false, true }) {
                doBulkTest(checksum, 1023, useDirect);
                doBulkTest(checksum, 1024, useDirect);
                doBulkTest(checksum, 1025, useDirect);
            }
        }
    }

    private static class Harness {
        final DataChecksum checksum;

        final int dataLength;

        final int sumsLength;

        final int numSums;

        ByteBuffer dataBuf;

        ByteBuffer checksumBuf;

        Harness(DataChecksum checksum, int dataLength, boolean useDirect) {
            this.checksum = checksum;
            this.dataLength = dataLength;
            numSums = ((dataLength - 1) / (checksum.getBytesPerChecksum())) + 1;
            sumsLength = (numSums) * (checksum.getChecksumSize());
            byte[] data = new byte[(dataLength + (TestDataChecksum.DATA_OFFSET_IN_BUFFER)) + (TestDataChecksum.DATA_TRAILER_IN_BUFFER)];
            new Random().nextBytes(data);
            dataBuf = ByteBuffer.wrap(data, TestDataChecksum.DATA_OFFSET_IN_BUFFER, dataLength);
            byte[] checksums = new byte[(TestDataChecksum.SUMS_OFFSET_IN_BUFFER) + (sumsLength)];
            checksumBuf = ByteBuffer.wrap(checksums, TestDataChecksum.SUMS_OFFSET_IN_BUFFER, sumsLength);
            // Swap out for direct buffers if requested.
            if (useDirect) {
                dataBuf = TestDataChecksum.directify(dataBuf);
                checksumBuf = TestDataChecksum.directify(checksumBuf);
            }
        }

        void testCorrectness() throws ChecksumException {
            // calculate real checksum, make sure it passes
            checksum.calculateChunkedSums(dataBuf, checksumBuf);
            checksum.verifyChunkedSums(dataBuf, checksumBuf, "fake file", 0);
            // Change a byte in the header and in the trailer, make sure
            // it doesn't affect checksum result
            TestDataChecksum.corruptBufferOffset(checksumBuf, 0);
            checksum.verifyChunkedSums(dataBuf, checksumBuf, "fake file", 0);
            TestDataChecksum.corruptBufferOffset(dataBuf, 0);
            dataBuf.limit(((dataBuf.limit()) + 1));
            TestDataChecksum.corruptBufferOffset(dataBuf, ((dataLength) + (TestDataChecksum.DATA_OFFSET_IN_BUFFER)));
            dataBuf.limit(((dataBuf.limit()) - 1));
            checksum.verifyChunkedSums(dataBuf, checksumBuf, "fake file", 0);
            // Make sure bad checksums fail - error at beginning of array
            TestDataChecksum.corruptBufferOffset(checksumBuf, TestDataChecksum.SUMS_OFFSET_IN_BUFFER);
            try {
                checksum.verifyChunkedSums(dataBuf, checksumBuf, "fake file", 0);
                Assert.fail("Did not throw on bad checksums");
            } catch (ChecksumException ce) {
                Assert.assertEquals(0, ce.getPos());
            }
            // Make sure bad checksums fail - error at end of array
            TestDataChecksum.uncorruptBufferOffset(checksumBuf, TestDataChecksum.SUMS_OFFSET_IN_BUFFER);
            TestDataChecksum.corruptBufferOffset(checksumBuf, (((TestDataChecksum.SUMS_OFFSET_IN_BUFFER) + (sumsLength)) - 1));
            try {
                checksum.verifyChunkedSums(dataBuf, checksumBuf, "fake file", 0);
                Assert.fail("Did not throw on bad checksums");
            } catch (ChecksumException ce) {
                int expectedPos = (checksum.getBytesPerChecksum()) * ((numSums) - 1);
                Assert.assertEquals(expectedPos, ce.getPos());
                Assert.assertTrue(ce.getMessage().contains("fake file"));
            }
        }
    }

    /**
     * Simple performance test for the "common case" checksum usage in HDFS:
     * computing and verifying CRC32C with 512 byte chunking on native
     * buffers.
     */
    @Test
    public void commonUsagePerfTest() throws Exception {
        final int NUM_RUNS = 5;
        final DataChecksum checksum = DataChecksum.newDataChecksum(CRC32C, 512);
        final int dataLength = (512 * 1024) * 1024;
        TestDataChecksum.Harness h = new TestDataChecksum.Harness(checksum, dataLength, true);
        for (int i = 0; i < NUM_RUNS; i++) {
            StopWatch s = new StopWatch().start();
            // calculate real checksum, make sure it passes
            checksum.calculateChunkedSums(h.dataBuf, h.checksumBuf);
            s.stop();
            System.err.println((((("Calculate run #" + i) + ": ") + (s.now(TimeUnit.MICROSECONDS))) + "us"));
            s = new StopWatch().start();
            // calculate real checksum, make sure it passes
            checksum.verifyChunkedSums(h.dataBuf, h.checksumBuf, "fake file", 0);
            s.stop();
            System.err.println((((("Verify run #" + i) + ": ") + (s.now(TimeUnit.MICROSECONDS))) + "us"));
        }
    }

    @Test
    public void testEquality() {
        Assert.assertEquals(DataChecksum.newDataChecksum(CRC32, 512), DataChecksum.newDataChecksum(CRC32, 512));
        Assert.assertFalse(DataChecksum.newDataChecksum(CRC32, 512).equals(DataChecksum.newDataChecksum(CRC32, 1024)));
        Assert.assertFalse(DataChecksum.newDataChecksum(CRC32, 512).equals(DataChecksum.newDataChecksum(CRC32C, 512)));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("DataChecksum(type=CRC32, chunkSize=512)", DataChecksum.newDataChecksum(CRC32, 512).toString());
    }

    @Test
    public void testCrc32() throws Exception {
        new Crc32PerformanceTest(8, 3, true).run();
        new Crc32PerformanceTest(8, 3, false).run();
    }
}

