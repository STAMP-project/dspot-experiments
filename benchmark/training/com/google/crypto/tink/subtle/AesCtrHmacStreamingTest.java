/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink.subtle;


import com.google.crypto.tink.StreamingTestUtil;
import com.google.crypto.tink.TestUtil;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@code AesCtrHmacStreaming}-implementation of {@code StreamingAead}-primitive.
 *
 * <p>TODO(b/66921440): adding more tests, including tests for other MAC and HKDF algos.
 */
@RunWith(JUnit4.class)
public class AesCtrHmacStreamingTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    /* The ciphertext is smaller than 1 segment */
    @Test
    public void testEncryptDecryptSmall() throws Exception {
        testEncryptDecrypt(16, 12, 256, 0, 20, 64);
        testEncryptDecrypt(16, 12, 512, 0, 400, 64);
    }

    /* The ciphertext has a non-zero offset */
    @Test
    public void testEncryptDecryptSmallWithOffset() throws Exception {
        testEncryptDecrypt(16, 12, 256, 8, 20, 64);
        testEncryptDecrypt(16, 12, 512, 8, 400, 64);
    }

    /* Empty plaintext */
    @Test
    public void testEncryptDecryptEmpty() throws Exception {
        testEncryptDecrypt(16, 12, 256, 0, 0, 128);
        testEncryptDecrypt(16, 12, 256, 8, 0, 128);
    }

    /* The ciphertext contains more than 1 segment. */
    @Test
    public void testEncryptDecryptMedium() throws Exception {
        testEncryptDecrypt(16, 12, 256, 0, 1024, 128);
        testEncryptDecrypt(16, 12, 512, 0, 3086, 128);
        testEncryptDecrypt(32, 12, 1024, 0, 12345, 128);
    }

    /* Test with different tag sizes */
    @Test
    public void testEncryptDecryptTagSize() throws Exception {
        testEncryptDecrypt(16, 12, 512, 0, 5000, 128);
        testEncryptDecrypt(16, 16, 512, 0, 5000, 128);
        testEncryptDecrypt(16, 20, 512, 0, 5000, 128);
        testEncryptDecrypt(16, 32, 512, 0, 5000, 128);
    }

    /* During decryption large plaintext chunks are requested */
    @Test
    public void testEncryptDecryptLargeChunks() throws Exception {
        testEncryptDecrypt(16, 12, 256, 0, 1024, 4096);
        testEncryptDecrypt(16, 12, 512, 0, 5086, 4096);
        testEncryptDecrypt(32, 16, 1024, 0, 12345, 5000);
    }

    @Test
    public void testEncryptDecryptMediumWithOffset() throws Exception {
        testEncryptDecrypt(16, 12, 256, 8, 1024, 64);
        testEncryptDecrypt(16, 12, 512, 20, 3086, 256);
        testEncryptDecrypt(32, 16, 1024, 10, 12345, 5000);
    }

    /* The ciphertext ends at a segment boundary. */
    @Test
    public void testEncryptDecryptLastSegmentFull() throws Exception {
        testEncryptDecrypt(16, 12, 256, 0, 216, 64);
        testEncryptDecrypt(16, 12, 256, 16, 200, 256);
        testEncryptDecrypt(16, 12, 256, 16, 440, 1024);
    }

    /* During decryption single bytes are requested */
    @Test
    public void testEncryptDecryptSingleBytes() throws Exception {
        testEncryptDecrypt(16, 12, 256, 0, 1024, 1);
        testEncryptDecrypt(32, 12, 512, 0, 5086, 1);
    }

    /* The ciphertext is smaller than 1 segment. */
    @Test
    public void testEncryptDecryptRandomAccessSmall() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 256, 0, 100);
        testEncryptDecryptRandomAccess(16, 12, 512, 0, 400);
    }

    @Test
    public void testEncryptDecryptRandomAccessSmallWithOffset() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 256, 8, 20);
        testEncryptDecryptRandomAccess(16, 12, 256, 8, 100);
        testEncryptDecryptRandomAccess(16, 12, 512, 8, 400);
    }

    /* Empty plaintext */
    @Test
    public void testEncryptDecryptRandomAccessEmpty() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 256, 0, 0);
        testEncryptDecryptRandomAccess(16, 12, 256, 8, 0);
    }

    @Test
    public void testEncryptDecryptRandomAccessMedium() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 256, 0, 2048);
        testEncryptDecryptRandomAccess(16, 12, 256, 0, 4096);
        testEncryptDecryptRandomAccess(32, 16, 1024, 0, 12345);
    }

    @Test
    public void testEncryptDecryptRandomAccessMediumWithOffset() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 256, 8, 2048);
        testEncryptDecryptRandomAccess(16, 12, 256, 10, 4096);
        testEncryptDecryptRandomAccess(32, 16, 1024, 20, 12345);
        testEncryptDecryptRandomAccess(16, 12, 4096, 0, 123456);
    }

    /* Test with different tag sizes */
    @Test
    public void testEncryptDecryptRandomAccessTagSize() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 512, 0, 12345);
        testEncryptDecryptRandomAccess(16, 16, 512, 0, 5000);
        testEncryptDecryptRandomAccess(16, 20, 512, 0, 4096);
        testEncryptDecryptRandomAccess(16, 32, 512, 0, 4096);
        testEncryptDecryptRandomAccess(16, 16, 256, 16, 440);
    }

    /* The ciphertext ends at a segment boundary. */
    @Test
    public void testEncryptDecryptRandomAccessLastSegmentFull() throws Exception {
        testEncryptDecryptRandomAccess(16, 12, 256, 0, 216);
        testEncryptDecryptRandomAccess(16, 12, 256, 16, 200);
        testEncryptDecryptRandomAccess(16, 12, 256, 16, 440);
    }

    /* Encryption is done byte by byte. */
    @Test
    public void testEncryptWithStream() throws Exception {
        testEncryptSingleBytes(16, 1024);
        testEncryptSingleBytes(16, 12345);
        testEncryptSingleBytes(16, 111111);
    }

    /**
     * Encrypts and decrypts a with non-ASCII characters using CharsetEncoders and CharsetDecoders.
     */
    @Test
    public void testEncryptDecryptString() throws Exception {
        StreamingTestUtil.testEncryptDecryptString(createAesCtrHmacStreaming());
    }

    /**
     * Test encryption with a simulated ciphertext channel, which has only a limited capacity.
     */
    @Test
    public void testEncryptLimitedCiphertextChannel() throws Exception {
        int segmentSize = 512;
        int firstSegmentOffset = 0;
        int keySizeInBytes = 16;
        int tagSizeInBytes = 12;
        byte[] ikm = TestUtil.hexDecode("000102030405060708090a0b0c0d0e0f00112233445566778899aabbccddeeff");
        AesCtrHmacStreaming ags = new AesCtrHmacStreaming(ikm, "HmacSha256", keySizeInBytes, "HmacSha256", tagSizeInBytes, segmentSize, firstSegmentOffset);
        int plaintextSize = 1 << 15;
        int maxChunkSize = 100;
        byte[] aad = TestUtil.hexDecode("aabbccddeeff");
        byte[] plaintext = StreamingTestUtil.generatePlaintext(plaintextSize);
        int ciphertextLength = ((int) (ags.expectedCiphertextSize(plaintextSize)));
        ByteBuffer ciphertext = ByteBuffer.allocate(ciphertextLength);
        WritableByteChannel ctChannel = new StreamingTestUtil.SeekableByteBufferChannel(ciphertext, maxChunkSize);
        WritableByteChannel encChannel = ags.newEncryptingChannel(ctChannel, aad);
        ByteBuffer plaintextBuffer = ByteBuffer.wrap(plaintext);
        int loops = 0;
        while ((plaintextBuffer.remaining()) > 0) {
            encChannel.write(plaintextBuffer);
            loops += 1;
            if (loops > 100000) {
                System.out.println(encChannel.toString());
                Assert.fail("Too many loops");
            }
        } 
        encChannel.close();
        Assert.assertFalse(encChannel.isOpen());
        StreamingTestUtil.isValidCiphertext(ags, plaintext, aad, ciphertext.array());
    }

    // Modifies the ciphertext. Checks that decryption either results in correct plaintext
    // or an exception.
    // The following modifications are tested:
    // (1) truncate ciphertext
    // (2) append stuff
    // (3) flip bits
    // (4) remove segments
    // (5) duplicate segments
    // (6) modify aad
    @Test
    public void testModifiedCiphertext() throws Exception {
        byte[] ikm = TestUtil.hexDecode("000102030405060708090a0b0c0d0e0f");
        int keySize = 16;
        int tagSize = 12;
        int segmentSize = 256;
        int offset = 8;
        AesCtrHmacStreaming ags = new AesCtrHmacStreaming(ikm, "HmacSha256", keySize, "HmacSha256", tagSize, segmentSize, offset);
        StreamingTestUtil.testModifiedCiphertext(ags, segmentSize, offset);
    }

    @Test
    public void testSkipWithStream() throws Exception {
        byte[] ikm = TestUtil.hexDecode("000102030405060708090a0b0c0d0e0f");
        int keySize = 16;
        int tagSize = 12;
        int segmentSize = 256;
        int offset = 8;
        int plaintextSize = 1 << 16;
        AesCtrHmacStreaming ags = new AesCtrHmacStreaming(ikm, "HmacSha256", keySize, "HmacSha256", tagSize, segmentSize, offset);
        // Smallest possible chunk size
        StreamingTestUtil.testSkipWithStream(ags, offset, plaintextSize, 1);
        // Chunk size < segmentSize
        StreamingTestUtil.testSkipWithStream(ags, offset, plaintextSize, 37);
        // Chunk size > segmentSize
        StreamingTestUtil.testSkipWithStream(ags, offset, plaintextSize, 384);
        // Chunk size > 3*segmentSize
        StreamingTestUtil.testSkipWithStream(ags, offset, plaintextSize, 800);
    }

    @Test
    public void testModifiedCiphertextWithSeekableByteChannel() throws Exception {
        byte[] ikm = TestUtil.hexDecode("000102030405060708090a0b0c0d0e0f");
        int keySize = 16;
        int tagSize = 12;
        int segmentSize = 256;
        int offset = 8;
        AesCtrHmacStreaming ags = new AesCtrHmacStreaming(ikm, "HmacSha256", keySize, "HmacSha256", tagSize, segmentSize, offset);
        StreamingTestUtil.testModifiedCiphertextWithSeekableByteChannel(ags, segmentSize, offset);
    }

    /**
     * Encrypts a plaintext consisting of 0's and checks that the ciphertext has no repeating blocks.
     * This is a simple test to catch basic errors that violate semantic security. The probability of
     * false positives is smaller than 2^{-100}.
     */
    @Test
    public void testKeyStream() throws Exception {
        HashSet<String> ciphertextBlocks = new HashSet<String>();
        byte[] ikm = TestUtil.hexDecode("000102030405060708090a0b0c0d0e0f");
        byte[] aad = TestUtil.hexDecode("aabbccddeeff");
        int keySize = 16;
        int tagSize = 12;
        int segmentSize = 256;
        int offset = 0;
        int plaintextSize = 2000;
        int samples = 8;
        int blocksize = 16;
        AesCtrHmacStreaming ags = new AesCtrHmacStreaming(ikm, "HmacSha256", keySize, "HmacSha256", tagSize, segmentSize, offset);
        byte[] plaintext = new byte[plaintextSize];
        for (int sample = 0; sample < samples; sample++) {
            byte[] ciphertext = StreamingTestUtil.encryptWithChannel(ags, plaintext, aad, ags.getFirstSegmentOffset());
            for (int pos = ags.getHeaderLength(); (pos + blocksize) <= (ciphertext.length); pos++) {
                String block = TestUtil.hexEncode(Arrays.copyOfRange(ciphertext, pos, (pos + blocksize)));
                if (!(ciphertextBlocks.add(block))) {
                    Assert.fail(((("Ciphertext contains a repeating block " + block) + " at position ") + pos));
                }
            }
        }
    }

    /**
     * Encrypt and decrypt a long ciphertext.
     */
    @Test
    public void testEncryptDecryptLong() throws Exception {
        long plaintextSize = (1L << 26) + 1234567;
        StreamingTestUtil.testEncryptDecryptLong(createAesCtrHmacStreaming(), plaintextSize);
    }

    /**
     * Encrypt some plaintext to a file, then decrypt from the file
     */
    @Test
    public void testFileEncryption() throws Exception {
        int plaintextSize = 1 << 20;
        StreamingTestUtil.testFileEncryption(createAesCtrHmacStreaming(), tmpFolder.newFile(), plaintextSize);
    }
}

