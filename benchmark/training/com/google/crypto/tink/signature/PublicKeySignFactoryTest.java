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


import CryptoFormat.NON_RAW_PREFIX_SIZE;
import EcdsaSignKeyManager.TYPE_URL;
import EcdsaSignatureEncoding.DER;
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
import com.google.crypto.tink.CryptoFormat;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.EcdsaPrivateKey;
import com.google.crypto.tink.proto.Keyset.Key;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link PublicKeySignFactory}.
 */
// TODO(quannguyen): Add more tests.
@RunWith(JUnit4.class)
public class PublicKeySignFactoryTest {
    @Test
    public void testMultipleKeys() throws Exception {
        EcdsaPrivateKey tinkPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P521, SHA512, DER);
        Key tink = TestUtil.createKey(TestUtil.createKeyData(tinkPrivateKey, TYPE_URL, ASYMMETRIC_PRIVATE), 1, ENABLED, TINK);
        EcdsaPrivateKey legacyPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P256, SHA256, DER);
        Key legacy = TestUtil.createKey(TestUtil.createKeyData(legacyPrivateKey, TYPE_URL, ASYMMETRIC_PRIVATE), 2, ENABLED, LEGACY);
        EcdsaPrivateKey rawPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P384, SHA512, DER);
        Key raw = TestUtil.createKey(TestUtil.createKeyData(rawPrivateKey, TYPE_URL, ASYMMETRIC_PRIVATE), 3, ENABLED, RAW);
        EcdsaPrivateKey crunchyPrivateKey = TestUtil.generateEcdsaPrivKey(NIST_P384, SHA512, DER);
        Key crunchy = TestUtil.createKey(TestUtil.createKeyData(crunchyPrivateKey, TYPE_URL, ASYMMETRIC_PRIVATE), 4, ENABLED, CRUNCHY);
        Key[] keys = new Key[]{ tink, legacy, raw, crunchy };
        EcdsaPrivateKey[] privateKeys = new EcdsaPrivateKey[]{ tinkPrivateKey, legacyPrivateKey, rawPrivateKey, crunchyPrivateKey };
        int j = keys.length;
        for (int i = 0; i < j; i++) {
            KeysetHandle keysetHandle = TestUtil.createKeysetHandle(TestUtil.createKeyset(keys[i], keys[((i + 1) % j)], keys[((i + 2) % j)], keys[((i + 3) % j)]));
            // Signs with the primary private key.
            PublicKeySign signer = PublicKeySignFactory.getPrimitive(keysetHandle);
            byte[] plaintext = Random.randBytes(1211);
            byte[] sig = signer.sign(plaintext);
            if ((keys[i].getOutputPrefixType()) != (OutputPrefixType.RAW)) {
                byte[] prefix = Arrays.copyOfRange(sig, 0, NON_RAW_PREFIX_SIZE);
                Assert.assertArrayEquals(prefix, CryptoFormat.getOutputPrefix(keys[i]));
            }
            // Verifying with the primary public key should work.
            PublicKeyVerify verifier = PublicKeyVerifyFactory.getPrimitive(TestUtil.createKeysetHandle(TestUtil.createKeyset(TestUtil.createKey(TestUtil.createKeyData(privateKeys[i].getPublicKey(), EcdsaVerifyKeyManager.TYPE_URL, ASYMMETRIC_PUBLIC), keys[i].getKeyId(), ENABLED, keys[i].getOutputPrefixType()))));
            try {
                verifier.verify(sig, plaintext);
            } catch (GeneralSecurityException ex) {
                Assert.fail("Valid signature, should not throw exception");
            }
            // Verifying with a random public key should fail.
            EcdsaPrivateKey randomPrivKey = TestUtil.generateEcdsaPrivKey(NIST_P521, SHA512, DER);
            verifier = PublicKeyVerifyFactory.getPrimitive(TestUtil.createKeysetHandle(TestUtil.createKeyset(TestUtil.createKey(TestUtil.createKeyData(randomPrivKey.getPublicKey(), EcdsaVerifyKeyManager.TYPE_URL, ASYMMETRIC_PUBLIC), keys[i].getKeyId(), ENABLED, keys[i].getOutputPrefixType()))));
            try {
                verifier.verify(sig, plaintext);
                Assert.fail("Invalid signature, should have thrown exception");
            } catch (GeneralSecurityException expected) {
                // Expected
            }
        }
    }
}

