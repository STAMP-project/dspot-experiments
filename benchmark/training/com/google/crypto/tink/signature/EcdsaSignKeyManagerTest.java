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
package com.google.crypto.tink.signature;


import EcdsaSignKeyManager.TYPE_URL;
import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import SignatureKeyTemplates.ECDSA_P256;
import SignatureKeyTemplates.ECDSA_P256_IEEE_P1363;
import SignatureKeyTemplates.ECDSA_P384;
import SignatureKeyTemplates.ECDSA_P384_IEEE_P1363;
import SignatureKeyTemplates.ECDSA_P521;
import SignatureKeyTemplates.ECDSA_P521_IEEE_P1363;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.EcdsaPrivateKey;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.subtle.Random;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for EcdsaSignKeyManager.
 *
 * <p>TODO(quannguyen): Add more tests.
 */
@RunWith(JUnit4.class)
public class EcdsaSignKeyManagerTest {
    private static class HashAndCurveType {
        public HashType hashType;

        public EllipticCurveType curveType;

        public HashAndCurveType(HashType hashType, EllipticCurveType curveType) {
            this.hashType = hashType;
            this.curveType = curveType;
        }
    }

    final byte[] msg = Random.randBytes(20);

    @Test
    public void testNewKeyWithVerifier() throws Exception {
        testNewKeyWithVerifier(ECDSA_P256);
        testNewKeyWithVerifier(ECDSA_P384);
        testNewKeyWithVerifier(ECDSA_P521);
        testNewKeyWithVerifier(ECDSA_P256_IEEE_P1363);
        testNewKeyWithVerifier(ECDSA_P384_IEEE_P1363);
        testNewKeyWithVerifier(ECDSA_P521_IEEE_P1363);
    }

    @Test
    public void testNewKeyWithCorruptedFormat() {
        ByteString serialized = ByteString.copyFrom(new byte[128]);
        KeyTemplate keyTemplate = KeyTemplate.newBuilder().setTypeUrl(TYPE_URL).setValue(serialized).build();
        EcdsaSignKeyManager keyManager = new EcdsaSignKeyManager();
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

    @Test
    public void testNewKeyUnsupportedKeyFormat() throws Exception {
        EcdsaSignKeyManagerTest.HashAndCurveType[] hashAndCurves = new EcdsaSignKeyManagerTest.HashAndCurveType[]{ new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA1, EllipticCurveType.NIST_P256), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA1, EllipticCurveType.NIST_P384), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA1, EllipticCurveType.NIST_P521), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA256, EllipticCurveType.NIST_P384), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA256, EllipticCurveType.NIST_P521), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA512, EllipticCurveType.NIST_P256) };
        for (int i = 0; i < (hashAndCurves.length); i++) {
            testNewKeyUnsupportedKeyFormat(hashAndCurves[i]);
        }
    }

    @Test
    public void testGetPrimitiveWithUnsupportedKey() throws Exception {
        EcdsaSignKeyManagerTest.HashAndCurveType[] hashAndCurves = new EcdsaSignKeyManagerTest.HashAndCurveType[]{ new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA1, EllipticCurveType.NIST_P256), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA1, EllipticCurveType.NIST_P384), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA1, EllipticCurveType.NIST_P521), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA256, EllipticCurveType.NIST_P384), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA256, EllipticCurveType.NIST_P521), new EcdsaSignKeyManagerTest.HashAndCurveType(HashType.SHA512, EllipticCurveType.NIST_P256) };
        for (int i = 0; i < (hashAndCurves.length); i++) {
            testGetPrimitiveWithUnsupportedKey(hashAndCurves[i]);
        }
    }

    /**
     * Tests that a public key is extracted properly from a private key.
     */
    @Test
    public void testGetPublicKeyData() throws Exception {
        KeysetHandle privateHandle = KeysetHandle.generateNew(ECDSA_P256);
        KeyData privateKeyData = TestUtil.getKeyset(privateHandle).getKey(0).getKeyData();
        EcdsaSignKeyManager privateManager = new EcdsaSignKeyManager();
        KeyData publicKeyData = privateManager.getPublicKeyData(privateKeyData.getValue());
        Assert.assertEquals(EcdsaVerifyKeyManager.TYPE_URL, publicKeyData.getTypeUrl());
        Assert.assertEquals(ASYMMETRIC_PUBLIC, publicKeyData.getKeyMaterialType());
        EcdsaPrivateKey privateKey = EcdsaPrivateKey.parseFrom(privateKeyData.getValue());
        Assert.assertArrayEquals(privateKey.getPublicKey().toByteArray(), publicKeyData.getValue().toByteArray());
        EcdsaVerifyKeyManager publicManager = new EcdsaVerifyKeyManager();
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

