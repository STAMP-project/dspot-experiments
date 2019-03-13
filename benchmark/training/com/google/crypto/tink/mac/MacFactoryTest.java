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
package com.google.crypto.tink.mac;


import CryptoFormat.NON_RAW_PREFIX_SIZE;
import KeyStatusType.ENABLED;
import OutputPrefixType.CRUNCHY;
import OutputPrefixType.LEGACY;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import com.google.crypto.tink.CryptoFormat;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Mac;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.Keyset.Key;
import com.google.crypto.tink.subtle.Bytes;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for MacFactory.
 */
@RunWith(JUnit4.class)
public class MacFactoryTest {
    private static final int HMAC_KEY_SIZE = 20;

    @Test
    public void testMultipleKeys() throws Exception {
        byte[] keyValue = Random.randBytes(MacFactoryTest.HMAC_KEY_SIZE);
        Key tink = TestUtil.createKey(TestUtil.createHmacKeyData(keyValue, 16), 42, ENABLED, TINK);
        Key legacy = TestUtil.createKey(TestUtil.createHmacKeyData(keyValue, 16), 43, ENABLED, LEGACY);
        Key raw = TestUtil.createKey(TestUtil.createHmacKeyData(keyValue, 16), 44, ENABLED, RAW);
        Key crunchy = TestUtil.createKey(TestUtil.createHmacKeyData(keyValue, 16), 45, ENABLED, CRUNCHY);
        Key[] keys = new Key[]{ tink, legacy, raw, crunchy };
        int j = keys.length;
        for (int i = 0; i < j; i++) {
            KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(keys[i], keys[((i + 1) % j)], keys[((i + 2) % j)], keys[((i + 3) % j)]));
            Mac mac = MacFactory.getPrimitive(keysetHandle);
            byte[] plaintext = "plaintext".getBytes("UTF-8");
            byte[] tag = mac.computeMac(plaintext);
            if (!(keys[i].getOutputPrefixType().equals(RAW))) {
                byte[] prefix = Arrays.copyOfRange(tag, 0, NON_RAW_PREFIX_SIZE);
                Assert.assertArrayEquals(prefix, CryptoFormat.getOutputPrefix(keys[i]));
            }
            try {
                mac.verifyMac(tag, plaintext);
            } catch (GeneralSecurityException e) {
                Assert.fail(("Valid MAC, should not throw exception: " + i));
            }
            // Modify plaintext or tag and make sure the verifyMac failed.
            byte[] plaintextAndTag = Bytes.concat(plaintext, tag);
            for (int b = 0; b < (plaintextAndTag.length); b++) {
                for (int bit = 0; bit < 8; bit++) {
                    byte[] modified = Arrays.copyOf(plaintextAndTag, plaintextAndTag.length);
                    modified[b] ^= ((byte) (1 << bit));
                    try {
                        mac.verifyMac(Arrays.copyOfRange(modified, plaintext.length, modified.length), Arrays.copyOfRange(modified, 0, plaintext.length));
                        Assert.fail("Invalid tag or plaintext, should have thrown exception");
                    } catch (GeneralSecurityException expected) {
                        // Expected
                    }
                }
            }
            // mac with a non-primary RAW key, verify with the keyset
            KeysetHandle keysetHandle2 = TestUtil.createKeysetHandle(TestUtil.createKeyset(raw, legacy, tink, crunchy));
            Mac mac2 = MacFactory.getPrimitive(keysetHandle2);
            tag = mac2.computeMac(plaintext);
            try {
                mac.verifyMac(tag, plaintext);
            } catch (GeneralSecurityException e) {
                Assert.fail("Valid MAC, should not throw exception");
            }
            // mac with a random key not in the keyset, verify with the keyset should fail
            byte[] keyValue2 = Random.randBytes(MacFactoryTest.HMAC_KEY_SIZE);
            Key random = TestUtil.createKey(TestUtil.createHmacKeyData(keyValue2, 16), 44, ENABLED, TINK);
            keysetHandle2 = TestUtil.createKeysetHandle(TestUtil.createKeyset(random));
            mac2 = MacFactory.getPrimitive(keysetHandle2);
            tag = mac2.computeMac(plaintext);
            try {
                mac.verifyMac(tag, plaintext);
                Assert.fail("Invalid MAC MAC, should have thrown exception");
            } catch (GeneralSecurityException expected) {
                // Expected
            }
        }
    }

    @Test
    public void testSmallPlaintextWithRawKey() throws Exception {
        byte[] keyValue = Random.randBytes(MacFactoryTest.HMAC_KEY_SIZE);
        Key primary = TestUtil.createKey(TestUtil.createHmacKeyData(keyValue, 16), 42, ENABLED, RAW);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(primary));
        Mac mac = MacFactory.getPrimitive(keysetHandle);
        byte[] plaintext = "blah".getBytes("UTF-8");
        byte[] tag = mac.computeMac(plaintext);
        // no prefix
        /* TAG */
        Assert.assertEquals(16, tag.length);
        try {
            mac.verifyMac(tag, plaintext);
        } catch (GeneralSecurityException e) {
            Assert.fail("Valid MAC, should not throw exception");
        }
    }

    @Test
    public void testInvalidKeyMaterial() throws Exception {
        Key valid = TestUtil.createKey(TestUtil.createHmacKeyData(Random.randBytes(MacFactoryTest.HMAC_KEY_SIZE), 16), 42, ENABLED, TINK);
        Key invalid = TestUtil.createKey(TestUtil.createAesSivKeyData(64), 43, ENABLED, TINK);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(valid, invalid));
        try {
            MacFactory.getPrimitive(keysetHandle);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "not match requested primitive type com.google.crypto.tink.Mac");
        }
        // invalid as the primary key.
        keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(invalid, valid));
        try {
            MacFactory.getPrimitive(keysetHandle);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "not match requested primitive type com.google.crypto.tink.Mac");
        }
    }
}

