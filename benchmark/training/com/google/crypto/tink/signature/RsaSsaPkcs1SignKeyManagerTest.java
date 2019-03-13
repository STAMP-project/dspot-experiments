/**
 * Copyright 2018 Google Inc.
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
package com.google.crypto.tink.signature;


import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import RsaSsaPkcs1SignKeyManager.TYPE_URL;
import SignatureKeyTemplates.RSA_SSA_PKCS1_3072_SHA256_F4;
import SignatureKeyTemplates.RSA_SSA_PKCS1_4096_SHA512_F4;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.RsaSsaPkcs1PrivateKey;
import com.google.crypto.tink.subtle.Random;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for RsaSsaPkcs1SignKeyManager.
 */
@RunWith(JUnit4.class)
public class RsaSsaPkcs1SignKeyManagerTest {
    final byte[] msg = Random.randBytes(20);

    @Test
    public void testNewKeyWithVerifier() throws Exception {
        if (TestUtil.isTsan()) {
            // This test times out when running under thread sanitizer, so we just skip.
            return;
        }
        testNewKeyWithVerifier(RSA_SSA_PKCS1_3072_SHA256_F4);
        testNewKeyWithVerifier(RSA_SSA_PKCS1_4096_SHA512_F4);
    }

    @Test
    public void testNewKeyWithCorruptedFormat() {
        ByteString serialized = ByteString.copyFrom(new byte[128]);
        KeyTemplate keyTemplate = KeyTemplate.newBuilder().setTypeUrl(TYPE_URL).setValue(serialized).build();
        RsaSsaPkcs1SignKeyManager keyManager = new RsaSsaPkcs1SignKeyManager();
        try {
            keyManager.newKey(serialized);
            Assert.fail("Corrupted format, should have thrown exception");
        } catch (GeneralSecurityException expected) {
            // Expected
        }
        try {
            keyManager.newKeyData(keyTemplate.getValue());
            Assert.fail("Corrupted format, should have thrown exception");
        } catch (GeneralSecurityException expected) {
            // Expected
        }
    }

    /**
     * Tests that a public key is extracted properly from a private key.
     */
    @Test
    public void testGetPublicKeyData() throws Exception {
        if (TestUtil.isTsan()) {
            // This test takes over a minute in successful tsan runs and sometimes times out.
            return;
        }
        KeysetHandle privateHandle = KeysetHandle.generateNew(RSA_SSA_PKCS1_3072_SHA256_F4);
        KeyData privateKeyData = TestUtil.getKeyset(privateHandle).getKey(0).getKeyData();
        RsaSsaPkcs1SignKeyManager privateManager = new RsaSsaPkcs1SignKeyManager();
        KeyData publicKeyData = privateManager.getPublicKeyData(privateKeyData.getValue());
        Assert.assertEquals(RsaSsaPkcs1VerifyKeyManager.TYPE_URL, publicKeyData.getTypeUrl());
        Assert.assertEquals(ASYMMETRIC_PUBLIC, publicKeyData.getKeyMaterialType());
        RsaSsaPkcs1PrivateKey privateKey = RsaSsaPkcs1PrivateKey.parseFrom(privateKeyData.getValue());
        Assert.assertArrayEquals(privateKey.getPublicKey().toByteArray(), publicKeyData.getValue().toByteArray());
        RsaSsaPkcs1VerifyKeyManager publicManager = new RsaSsaPkcs1VerifyKeyManager();
        PublicKeySign signer = privateManager.getPrimitive(privateKeyData.getValue());
        PublicKeyVerify verifier = publicManager.getPrimitive(publicKeyData.getValue());
        byte[] message = Random.randBytes(20);
        try {
            verifier.verify(signer.sign(message), message);
        } catch (GeneralSecurityException e) {
            Assert.fail(("Should not fail: " + e));
        }
    }
}

