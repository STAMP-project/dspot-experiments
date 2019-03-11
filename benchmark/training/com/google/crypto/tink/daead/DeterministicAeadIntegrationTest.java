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
package com.google.crypto.tink.daead;


import DeterministicAeadKeyTemplates.AES256_SIV;
import KeyStatusType.ENABLED;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.Keyset.Key;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests which run the everything for the DeterministicAead primitives.
 */
@RunWith(JUnit4.class)
public class DeterministicAeadIntegrationTest {
    private Integer[] keySizeInBytes;

    @Test
    public void testEncrytDecrypt() throws Exception {
        KeysetHandle keysetHandle = KeysetHandle.generateNew(AES256_SIV);
        DeterministicAead aead = keysetHandle.getPrimitive(DeterministicAead.class);
        byte[] plaintext = Random.randBytes(20);
        byte[] associatedData = Random.randBytes(20);
        byte[] ciphertext = aead.encryptDeterministically(plaintext, associatedData);
        byte[] ciphertext2 = aead.encryptDeterministically(plaintext, associatedData);
        byte[] decrypted = aead.decryptDeterministically(ciphertext, associatedData);
        byte[] decrypted2 = aead.decryptDeterministically(ciphertext2, associatedData);
        Assert.assertArrayEquals(ciphertext, ciphertext2);
        Assert.assertArrayEquals(plaintext, decrypted);
        Assert.assertArrayEquals(plaintext, decrypted2);
    }

    @Test
    public void testMultipleKeys() throws Exception {
        for (int keySize : keySizeInBytes) {
            DeterministicAeadIntegrationTest.testMultipleKeys(keySize);
        }
    }

    @Test
    public void testRawKeyAsPrimary() throws Exception {
        for (int keySize : keySizeInBytes) {
            DeterministicAeadIntegrationTest.testRawKeyAsPrimary(keySize);
        }
    }

    @Test
    public void testSmallPlaintextWithRawKey() throws Exception {
        for (int keySize : keySizeInBytes) {
            DeterministicAeadIntegrationTest.testSmallPlaintextWithRawKey(keySize);
        }
    }

    @Test
    public void testInvalidKeyMaterial() throws Exception {
        Key valid = TestUtil.createKey(TestUtil.createAesSivKeyData(64), 42, ENABLED, TINK);
        Key invalid = TestUtil.createKey(TestUtil.createAesCtrHmacAeadKeyData(Random.randBytes(16), 12, Random.randBytes(16), 16), 43, ENABLED, RAW);
        KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(valid, invalid));
        try {
            keysetHandle.getPrimitive(DeterministicAead.class);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "not match requested primitive type com.google.crypto.tink.DeterministicAead");
        }
        // invalid as the primary key.
        keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(invalid, valid));
        try {
            keysetHandle.getPrimitive(DeterministicAead.class);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "not match requested primitive type com.google.crypto.tink.DeterministicAead");
        }
    }
}

