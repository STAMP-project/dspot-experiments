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
package com.google.crypto.tink.aead;


import CryptoFormat.NON_RAW_PREFIX_SIZE;
import KeyStatusType.ENABLED;
import OutputPrefixType.LEGACY;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.CryptoFormat;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.Keyset.Key;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for AeadFactory.
 */
@RunWith(JUnit4.class)
public class AeadFactoryTest {
    private static final int AES_KEY_SIZE = 16;

    private static final int HMAC_KEY_SIZE = 20;

    @Test
    public void testBasicAesCtrHmacAead() throws Exception {
        byte[] aesCtrKeyValue = Random.randBytes(AeadFactoryTest.AES_KEY_SIZE);
        byte[] hmacKeyValue = Random.randBytes(AeadFactoryTest.HMAC_KEY_SIZE);
        int ivSize = 12;
        int tagSize = 16;
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 42, ENABLED, TINK)));
        TestUtil.runBasicAeadTests(keysetHandle.getPrimitive(Aead.class));
    }

    @Test
    public void testMultipleKeys() throws Exception {
        byte[] aesCtrKeyValue = Random.randBytes(AeadFactoryTest.AES_KEY_SIZE);
        byte[] hmacKeyValue = Random.randBytes(AeadFactoryTest.HMAC_KEY_SIZE);
        int ivSize = 12;
        int tagSize = 16;
        Key primary = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 42, ENABLED, TINK);
        Key raw = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 43, ENABLED, RAW);
        Key legacy = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 44, ENABLED, LEGACY);
        Key tink = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 45, ENABLED, TINK);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(primary, raw, legacy, tink));
        Aead aead = keysetHandle.getPrimitive(Aead.class);
        byte[] plaintext = Random.randBytes(20);
        byte[] associatedData = Random.randBytes(20);
        byte[] ciphertext = aead.encrypt(plaintext, associatedData);
        byte[] prefix = Arrays.copyOfRange(ciphertext, 0, NON_RAW_PREFIX_SIZE);
        Assert.assertArrayEquals(prefix, CryptoFormat.getOutputPrefix(primary));
        Assert.assertArrayEquals(plaintext, aead.decrypt(ciphertext, associatedData));
        Assert.assertEquals(((((CryptoFormat.NON_RAW_PREFIX_SIZE) + (plaintext.length)) + ivSize) + tagSize), ciphertext.length);
        // encrypt with a non-primary RAW key and decrypt with the keyset
        KeysetHandle keysetHandle2 = TestUtil.createKeysetHandle(TestUtil.createKeyset(raw, legacy, tink));
        Aead aead2 = keysetHandle2.getPrimitive(Aead.class);
        ciphertext = aead2.encrypt(plaintext, associatedData);
        Assert.assertArrayEquals(plaintext, aead.decrypt(ciphertext, associatedData));
        // encrypt with a random key not in the keyset, decrypt with the keyset should fail
        byte[] aesCtrKeyValue2 = Random.randBytes(AeadFactoryTest.AES_KEY_SIZE);
        byte[] hmacKeyValue2 = Random.randBytes(AeadFactoryTest.HMAC_KEY_SIZE);
        Key random = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue2, ivSize, hmacKeyValue2, tagSize), 44, ENABLED, TINK);
        keysetHandle2 = TestUtil.createKeysetHandle(TestUtil.createKeyset(random));
        aead2 = keysetHandle2.getPrimitive(Aead.class);
        ciphertext = aead2.encrypt(plaintext, associatedData);
        try {
            aead.decrypt(ciphertext, associatedData);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "decryption failed");
        }
    }

    @Test
    public void testRawKeyAsPrimary() throws Exception {
        byte[] aesCtrKeyValue = Random.randBytes(AeadFactoryTest.AES_KEY_SIZE);
        byte[] hmacKeyValue = Random.randBytes(AeadFactoryTest.HMAC_KEY_SIZE);
        int ivSize = 12;
        int tagSize = 16;
        Key primary = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 42, ENABLED, RAW);
        Key raw = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 43, ENABLED, RAW);
        Key legacy = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 44, ENABLED, LEGACY);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(primary, raw, legacy));
        Aead aead = keysetHandle.getPrimitive(Aead.class);
        byte[] plaintext = Random.randBytes(20);
        byte[] associatedData = Random.randBytes(20);
        byte[] ciphertext = aead.encrypt(plaintext, associatedData);
        Assert.assertArrayEquals(plaintext, aead.decrypt(ciphertext, associatedData));
        Assert.assertEquals(((((CryptoFormat.RAW_PREFIX_SIZE) + (plaintext.length)) + ivSize) + tagSize), ciphertext.length);
    }

    @Test
    public void testSmallPlaintextWithRawKey() throws Exception {
        byte[] aesCtrKeyValue = Random.randBytes(AeadFactoryTest.AES_KEY_SIZE);
        byte[] hmacKeyValue = Random.randBytes(AeadFactoryTest.HMAC_KEY_SIZE);
        int ivSize = 12;
        int tagSize = 16;
        Key primary = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 42, ENABLED, RAW);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(primary));
        Aead aead = keysetHandle.getPrimitive(Aead.class);
        byte[] plaintext = Random.randBytes(1);
        byte[] associatedData = Random.randBytes(20);
        byte[] ciphertext = aead.encrypt(plaintext, associatedData);
        Assert.assertArrayEquals(plaintext, aead.decrypt(ciphertext, associatedData));
        Assert.assertEquals(((((CryptoFormat.RAW_PREFIX_SIZE) + (plaintext.length)) + ivSize) + tagSize), ciphertext.length);
    }

    @Test
    public void testInvalidKeyMaterial() throws Exception {
        byte[] aesCtrKeyValue = Random.randBytes(AeadFactoryTest.AES_KEY_SIZE);
        byte[] hmacKeyValue = Random.randBytes(AeadFactoryTest.HMAC_KEY_SIZE);
        int ivSize = 12;
        int tagSize = 16;
        Key valid = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(aesCtrKeyValue, ivSize, hmacKeyValue, tagSize), 42, ENABLED, RAW);
        Key invalid = TestUtil.createKey(TestUtil.createAesSivKeyData(64), 43, ENABLED, TINK);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(valid, invalid));
        try {
            keysetHandle.getPrimitive(Aead.class);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "Primitive type com.google.crypto.tink.DeterministicAead");
        }
        // invalid as the primary key.
        keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(invalid, valid));
        try {
            keysetHandle.getPrimitive(Aead.class);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "Primitive type com.google.crypto.tink.DeterministicAead");
        }
    }
}

