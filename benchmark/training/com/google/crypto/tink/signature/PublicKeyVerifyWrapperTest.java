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


import EcdsaSignatureEncoding.DER;
import EcdsaVerifyKeyManager.TYPE_URL;
import EllipticCurveType.NIST_P256;
import EllipticCurveType.NIST_P384;
import EllipticCurveType.NIST_P521;
import HashType.SHA256;
import HashType.SHA512;
import KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE;
import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import KeyStatusType.ENABLED;
import OutputPrefixType.CRUNCHY;
import OutputPrefixType.LEGACY;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.Registry;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.EcdsaPrivateKey;
import com.google.crypto.tink.proto.Keyset.Key;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link PublicKeyVerifyWrapper}.
 */
// TODO(quannguyen): Add more tests.
@RunWith(JUnit4.class)
public class PublicKeyVerifyWrapperTest {
    @Test
    public void testMultipleKeys() throws Exception {
        EcdsaPrivateKey tinkPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P521, SHA512, DER);
        Key tink = TestUtil.createKey(TestUtil.createKeyData(tinkPrivateKey.getPublicKey(), TYPE_URL, ASYMMETRIC_PUBLIC), 1, ENABLED, TINK);
        EcdsaPrivateKey legacyPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P256, SHA256, DER);
        Key legacy = TestUtil.createKey(TestUtil.createKeyData(legacyPrivateKey.getPublicKey(), TYPE_URL, ASYMMETRIC_PUBLIC), 2, ENABLED, LEGACY);
        EcdsaPrivateKey rawPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P384, SHA512, DER);
        Key raw = TestUtil.createKey(TestUtil.createKeyData(rawPrivateKey.getPublicKey(), TYPE_URL, ASYMMETRIC_PUBLIC), 3, ENABLED, RAW);
        EcdsaPrivateKey crunchyPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P384, SHA512, DER);
        Key crunchy = TestUtil.createKey(TestUtil.createKeyData(crunchyPrivateKey.getPublicKey(), TYPE_URL, ASYMMETRIC_PUBLIC), 4, ENABLED, CRUNCHY);
        Key[] keys = new Key[]{ tink, legacy, raw, crunchy };
        EcdsaPrivateKey[] privateKeys = new EcdsaPrivateKey[]{ tinkPrivateKey, legacyPrivateKey, rawPrivateKey, crunchyPrivateKey };
        int j = keys.length;
        for (int i = 0; i < j; i++) {
            KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(keys[i], keys[((i + 1) % j)], keys[((i + 2) % j)], keys[((i + 3) % j)]));
            PublicKeyVerify verifier = new PublicKeyVerifyWrapper().wrap(Registry.getPrimitives(keysetHandle));
            // Signature from any keys in the keyset should be valid.
            for (int k = 0; k < j; k++) {
                PublicKeySign signer = PublicKeySignFactory.getPrimitive(TestUtil.createKeysetHandle(TestUtil.createKeyset(TestUtil.createKey(TestUtil.createKeyData(privateKeys[k], EcdsaSignKeyManager.TYPE_URL, ASYMMETRIC_PRIVATE), keys[k].getKeyId(), ENABLED, keys[k].getOutputPrefixType()))));
                byte[] plaintext = Random.randBytes(1211);
                byte[] sig = signer.sign(plaintext);
                try {
                    verifier.verify(sig, plaintext);
                } catch (GeneralSecurityException ex) {
                    Assert.fail(("Valid signature, should not throw exception: " + k));
                }
            }
            // Signature from a random key should be invalid.
            EcdsaPrivateKey randomPrivKey = TestUtil.generateEcdsaPrivKey(NIST_P521, SHA512, DER);
            PublicKeySign signer = PublicKeySignFactory.getPrimitive(TestUtil.createKeysetHandle(TestUtil.createKeyset(TestUtil.createKey(TestUtil.createKeyData(randomPrivKey, EcdsaSignKeyManager.TYPE_URL, ASYMMETRIC_PRIVATE), 1, ENABLED, keys[0].getOutputPrefixType()))));
            byte[] plaintext = Random.randBytes(1211);
            byte[] sig = signer.sign(plaintext);
            try {
                verifier.verify(sig, plaintext);
                Assert.fail("Invalid signature, should have thrown exception");
            } catch (GeneralSecurityException expected) {
                // Expected
            }
        }
    }
}

