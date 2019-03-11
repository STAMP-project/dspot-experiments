/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.upstream.crypto;


import com.google.android.exoplayer2.testutil.TestUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.Random;
import javax.crypto.Cipher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Unit tests for {@link AesFlushingCipher}.
 */
@RunWith(RobolectricTestRunner.class)
public class AesFlushingCipherTest {
    private static final int DATA_LENGTH = 65536;

    private static final byte[] KEY = Util.getUtf8Bytes("testKey:12345678");

    private static final long NONCE = 0;

    private static final long START_OFFSET = 11;

    private static final long RANDOM_SEED = 305419896;

    private AesFlushingCipher encryptCipher;

    private AesFlushingCipher decryptCipher;

    // Test a single encrypt and decrypt call.
    @Test
    public void testSingle() {
        byte[] reference = TestUtil.buildTestData(AesFlushingCipherTest.DATA_LENGTH);
        byte[] data = reference.clone();
        encryptCipher.updateInPlace(data, 0, data.length);
        int unchangedByteCount = (data.length) - (AesFlushingCipherTest.getDifferingByteCount(reference, data));
        assertThat((unchangedByteCount <= (AesFlushingCipherTest.getMaxUnchangedBytesAllowedPostEncryption(data.length)))).isTrue();
        decryptCipher.updateInPlace(data, 0, data.length);
        int differingByteCount = AesFlushingCipherTest.getDifferingByteCount(reference, data);
        assertThat(differingByteCount).isEqualTo(0);
    }

    // Test several encrypt and decrypt calls, each aligned on a 16 byte block size.
    @Test
    public void testAligned() {
        byte[] reference = TestUtil.buildTestData(AesFlushingCipherTest.DATA_LENGTH);
        byte[] data = reference.clone();
        Random random = new Random(AesFlushingCipherTest.RANDOM_SEED);
        int offset = 0;
        while (offset < (data.length)) {
            int bytes = (1 + (random.nextInt(50))) * 16;
            bytes = Math.min(bytes, ((data.length) - offset));
            assertThat((bytes % 16)).isEqualTo(0);
            encryptCipher.updateInPlace(data, offset, bytes);
            offset += bytes;
        } 
        int unchangedByteCount = (data.length) - (AesFlushingCipherTest.getDifferingByteCount(reference, data));
        assertThat((unchangedByteCount <= (AesFlushingCipherTest.getMaxUnchangedBytesAllowedPostEncryption(data.length)))).isTrue();
        offset = 0;
        while (offset < (data.length)) {
            int bytes = (1 + (random.nextInt(50))) * 16;
            bytes = Math.min(bytes, ((data.length) - offset));
            assertThat((bytes % 16)).isEqualTo(0);
            decryptCipher.updateInPlace(data, offset, bytes);
            offset += bytes;
        } 
        int differingByteCount = AesFlushingCipherTest.getDifferingByteCount(reference, data);
        assertThat(differingByteCount).isEqualTo(0);
    }

    // Test several encrypt and decrypt calls, not aligned on block boundary.
    @Test
    public void testUnAligned() {
        byte[] reference = TestUtil.buildTestData(AesFlushingCipherTest.DATA_LENGTH);
        byte[] data = reference.clone();
        Random random = new Random(AesFlushingCipherTest.RANDOM_SEED);
        // Encrypt
        int offset = 0;
        while (offset < (data.length)) {
            int bytes = 1 + (random.nextInt(4095));
            bytes = Math.min(bytes, ((data.length) - offset));
            encryptCipher.updateInPlace(data, offset, bytes);
            offset += bytes;
        } 
        int unchangedByteCount = (data.length) - (AesFlushingCipherTest.getDifferingByteCount(reference, data));
        assertThat((unchangedByteCount <= (AesFlushingCipherTest.getMaxUnchangedBytesAllowedPostEncryption(data.length)))).isTrue();
        offset = 0;
        while (offset < (data.length)) {
            int bytes = 1 + (random.nextInt(4095));
            bytes = Math.min(bytes, ((data.length) - offset));
            decryptCipher.updateInPlace(data, offset, bytes);
            offset += bytes;
        } 
        int differingByteCount = AesFlushingCipherTest.getDifferingByteCount(reference, data);
        assertThat(differingByteCount).isEqualTo(0);
    }

    // Test decryption starting from the middle of an encrypted block.
    @Test
    public void testMidJoin() {
        byte[] reference = TestUtil.buildTestData(AesFlushingCipherTest.DATA_LENGTH);
        byte[] data = reference.clone();
        Random random = new Random(AesFlushingCipherTest.RANDOM_SEED);
        // Encrypt
        int offset = 0;
        while (offset < (data.length)) {
            int bytes = 1 + (random.nextInt(4095));
            bytes = Math.min(bytes, ((data.length) - offset));
            encryptCipher.updateInPlace(data, offset, bytes);
            offset += bytes;
        } 
        // Verify
        int unchangedByteCount = (data.length) - (AesFlushingCipherTest.getDifferingByteCount(reference, data));
        assertThat((unchangedByteCount <= (AesFlushingCipherTest.getMaxUnchangedBytesAllowedPostEncryption(data.length)))).isTrue();
        // Setup decryption from random location
        offset = random.nextInt(4096);
        decryptCipher = new AesFlushingCipher(Cipher.DECRYPT_MODE, AesFlushingCipherTest.KEY, AesFlushingCipherTest.NONCE, (offset + (AesFlushingCipherTest.START_OFFSET)));
        int remainingLength = (data.length) - offset;
        int originalOffset = offset;
        // Decrypt
        while (remainingLength > 0) {
            int bytes = 1 + (random.nextInt(4095));
            bytes = Math.min(bytes, remainingLength);
            decryptCipher.updateInPlace(data, offset, bytes);
            offset += bytes;
            remainingLength -= bytes;
        } 
        // Verify
        int differingByteCount = AesFlushingCipherTest.getDifferingByteCount(reference, data, originalOffset);
        assertThat(differingByteCount).isEqualTo(0);
    }
}

