/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.record;


import KafkaLZ4BlockInputStream.DESCRIPTOR_HASH_MISMATCH;
import KafkaLZ4BlockInputStream.NOT_SUPPORTED;
import KafkaLZ4BlockInputStream.PREMATURE_EOS;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class KafkaLZ4Test {
    private static final Random RANDOM = new Random(0);

    private final boolean useBrokenFlagDescriptorChecksum;

    private final boolean ignoreFlagDescriptorChecksum;

    private final byte[] payload;

    private final boolean close;

    private final boolean blockChecksum;

    static class Payload {
        String name;

        byte[] payload;

        Payload(String name, byte[] payload) {
            this.name = name;
            this.payload = payload;
        }

        @Override
        public String toString() {
            return ((((("Payload{" + "size=") + (payload.length)) + ", name='") + (name)) + '\'') + '}';
        }
    }

    public KafkaLZ4Test(boolean useBrokenFlagDescriptorChecksum, boolean ignoreFlagDescriptorChecksum, boolean blockChecksum, boolean close, KafkaLZ4Test.Payload payload) {
        this.useBrokenFlagDescriptorChecksum = useBrokenFlagDescriptorChecksum;
        this.ignoreFlagDescriptorChecksum = ignoreFlagDescriptorChecksum;
        this.payload = payload.payload;
        this.close = close;
        this.blockChecksum = blockChecksum;
    }

    @Test
    public void testHeaderPrematureEnd() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        IOException e = Assert.assertThrows(IOException.class, () -> makeInputStream(buffer));
        Assert.assertEquals(PREMATURE_EOS, e.getMessage());
    }

    @Test
    public void testNotSupported() throws Exception {
        byte[] compressed = compressedBytes();
        compressed[0] = 0;
        ByteBuffer buffer = ByteBuffer.wrap(compressed);
        IOException e = Assert.assertThrows(IOException.class, () -> makeInputStream(buffer));
        Assert.assertEquals(NOT_SUPPORTED, e.getMessage());
    }

    @Test
    public void testBadFrameChecksum() throws Exception {
        byte[] compressed = compressedBytes();
        compressed[6] = ((byte) (255));
        ByteBuffer buffer = ByteBuffer.wrap(compressed);
        if (ignoreFlagDescriptorChecksum) {
            makeInputStream(buffer);
        } else {
            IOException e = Assert.assertThrows(IOException.class, () -> makeInputStream(buffer));
            Assert.assertEquals(DESCRIPTOR_HASH_MISMATCH, e.getMessage());
        }
    }

    @Test
    public void testBadBlockSize() throws Exception {
        if ((!(close)) || ((useBrokenFlagDescriptorChecksum) && (!(ignoreFlagDescriptorChecksum))))
            return;

        byte[] compressed = compressedBytes();
        ByteBuffer buffer = ByteBuffer.wrap(compressed).order(ByteOrder.LITTLE_ENDIAN);
        int blockSize = buffer.getInt(7);
        blockSize = (blockSize & (KafkaLZ4BlockOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK)) | ((1 << 24) & (~(KafkaLZ4BlockOutputStream.LZ4_FRAME_INCOMPRESSIBLE_MASK)));
        buffer.putInt(7, blockSize);
        IOException e = Assert.assertThrows(IOException.class, () -> testDecompression(buffer));
        MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("exceeded max"));
    }

    @Test
    public void testCompression() throws Exception {
        byte[] compressed = compressedBytes();
        // Check magic bytes stored as little-endian
        int offset = 0;
        Assert.assertEquals(4, compressed[(offset++)]);
        Assert.assertEquals(34, compressed[(offset++)]);
        Assert.assertEquals(77, compressed[(offset++)]);
        Assert.assertEquals(24, compressed[(offset++)]);
        // Check flg descriptor
        byte flg = compressed[(offset++)];
        // 2-bit version must be 01
        int version = (flg >>> 6) & 3;
        Assert.assertEquals(1, version);
        // Reserved bits should always be 0
        int reserved = flg & 3;
        Assert.assertEquals(0, reserved);
        // Check block descriptor
        byte bd = compressed[(offset++)];
        // Block max-size
        int blockMaxSize = (bd >>> 4) & 7;
        // Only supported values are 4 (64KB), 5 (256KB), 6 (1MB), 7 (4MB)
        Assert.assertTrue((blockMaxSize >= 4));
        Assert.assertTrue((blockMaxSize <= 7));
        // Multiple reserved bit ranges in block descriptor
        reserved = bd & 15;
        Assert.assertEquals(0, reserved);
        reserved = (bd >>> 7) & 1;
        Assert.assertEquals(0, reserved);
        // If flg descriptor sets content size flag
        // there are 8 additional bytes before checksum
        boolean contentSize = ((flg >>> 3) & 1) != 0;
        if (contentSize)
            offset += 8;

        // Checksum applies to frame descriptor: flg, bd, and optional contentsize
        // so initial offset should be 4 (for magic bytes)
        int off = 4;
        int len = offset - 4;
        // Initial implementation of checksum incorrectly applied to full header
        // including magic bytes
        if (this.useBrokenFlagDescriptorChecksum) {
            off = 0;
            len = offset;
        }
        int hash = net.jpountz.xxhash.XXHashFactory.fastestInstance().hash32().hash(compressed, off, len, 0);
        byte hc = compressed[(offset++)];
        Assert.assertEquals(((byte) ((hash >> 8) & 255)), hc);
        // Check EndMark, data block with size `0` expressed as a 32-bits value
        if (this.close) {
            offset = (compressed.length) - 4;
            Assert.assertEquals(0, compressed[(offset++)]);
            Assert.assertEquals(0, compressed[(offset++)]);
            Assert.assertEquals(0, compressed[(offset++)]);
            Assert.assertEquals(0, compressed[(offset++)]);
        }
    }

    @Test
    public void testArrayBackedBuffer() throws IOException {
        byte[] compressed = compressedBytes();
        testDecompression(ByteBuffer.wrap(compressed));
    }

    @Test
    public void testArrayBackedBufferSlice() throws IOException {
        byte[] compressed = compressedBytes();
        int sliceOffset = 12;
        ByteBuffer buffer = ByteBuffer.allocate((((compressed.length) + sliceOffset) + 123));
        buffer.position(sliceOffset);
        buffer.put(compressed).flip();
        buffer.position(sliceOffset);
        ByteBuffer slice = buffer.slice();
        testDecompression(slice);
        int offset = 42;
        buffer = ByteBuffer.allocate((((compressed.length) + sliceOffset) + offset));
        buffer.position((sliceOffset + offset));
        buffer.put(compressed).flip();
        buffer.position(sliceOffset);
        slice = buffer.slice();
        slice.position(offset);
        testDecompression(slice);
    }

    @Test
    public void testDirectBuffer() throws IOException {
        byte[] compressed = compressedBytes();
        ByteBuffer buffer;
        buffer = ByteBuffer.allocateDirect(compressed.length);
        buffer.put(compressed).flip();
        testDecompression(buffer);
        int offset = 42;
        buffer = ByteBuffer.allocateDirect((((compressed.length) + offset) + 123));
        buffer.position(offset);
        buffer.put(compressed).flip();
        buffer.position(offset);
        testDecompression(buffer);
    }

    @Test
    public void testSkip() throws Exception {
        if ((!(close)) || ((useBrokenFlagDescriptorChecksum) && (!(ignoreFlagDescriptorChecksum))))
            return;

        final KafkaLZ4BlockInputStream in = makeInputStream(ByteBuffer.wrap(compressedBytes()));
        int n = 100;
        int remaining = payload.length;
        long skipped = in.skip(n);
        Assert.assertEquals(Math.min(n, remaining), skipped);
        n = 10000;
        remaining -= skipped;
        skipped = in.skip(n);
        Assert.assertEquals(Math.min(n, remaining), skipped);
    }
}

