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
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.Registry;
import com.google.crypto.tink.subtle.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for DeterministicAeadWrapper.
 */
@RunWith(JUnit4.class)
public class DeterministicAeadWrapperTest {
    private Integer[] keySizeInBytes;

    @Test
    public void testEncrytDecrypt() throws Exception {
        KeysetHandle keysetHandle = KeysetHandle.generateNew(AES256_SIV);
        DeterministicAead aead = new DeterministicAeadWrapper().wrap(Registry.getPrimitives(keysetHandle));
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
            DeterministicAeadWrapperTest.testMultipleKeys(keySize);
        }
    }

    @Test
    public void testRawKeyAsPrimary() throws Exception {
        for (int keySize : keySizeInBytes) {
            DeterministicAeadWrapperTest.testRawKeyAsPrimary(keySize);
        }
    }

    @Test
    public void testSmallPlaintextWithRawKey() throws Exception {
        for (int keySize : keySizeInBytes) {
            DeterministicAeadWrapperTest.testSmallPlaintextWithRawKey(keySize);
        }
    }
}

