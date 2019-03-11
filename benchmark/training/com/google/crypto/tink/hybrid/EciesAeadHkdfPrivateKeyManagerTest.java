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
package com.google.crypto.tink.hybrid;


import EciesAeadHkdfPrivateKeyManager.TYPE_URL;
import HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256;
import KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE;
import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import KeyStatusType.ENABLED;
import OutputPrefixType.RAW;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.EcPointFormat;
import com.google.crypto.tink.proto.EciesAeadHkdfKeyFormat;
import com.google.crypto.tink.proto.EciesAeadHkdfParams;
import com.google.crypto.tink.proto.EciesAeadHkdfPrivateKey;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.Keyset.Key;
import com.google.crypto.tink.subtle.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for EciesAeadHkdfPrivateKeyManager.
 */
@RunWith(JUnit4.class)
public class EciesAeadHkdfPrivateKeyManagerTest {
    @Test
    public void testNewKey() throws Exception {
        EllipticCurveType curve = EllipticCurveType.NIST_P384;
        HashType hashType = HashType.SHA256;
        EcPointFormat pointFormat = EcPointFormat.UNCOMPRESSED;
        KeyTemplate demKeyTemplate = AeadKeyTemplates.AES128_CTR_HMAC_SHA256;
        byte[] salt = "some salt".getBytes("UTF-8");
        EciesAeadHkdfParams params = HybridKeyTemplates.createEciesAeadHkdfParams(curve, hashType, pointFormat, demKeyTemplate, salt);
        EciesAeadHkdfPrivateKeyManager manager = new EciesAeadHkdfPrivateKeyManager();
        EciesAeadHkdfPrivateKey keyProto = ((EciesAeadHkdfPrivateKey) (manager.newKey(EciesAeadHkdfKeyFormat.newBuilder().setParams(params).build())));
        Assert.assertEquals(params, keyProto.getPublicKey().getParams());
        Key primaryPriv = TestUtil.createKey(TestUtil.createKeyData(keyProto, TYPE_URL, ASYMMETRIC_PRIVATE), 8, ENABLED, RAW);
        Key primaryPub = TestUtil.createKey(TestUtil.createKeyData(keyProto.getPublicKey(), EciesAeadHkdfPublicKeyManager.TYPE_URL, ASYMMETRIC_PUBLIC), 42, ENABLED, RAW);
        KeysetHandle keysetHandlePub = TestUtil.createKeysetHandle(TestUtil.createKeyset(primaryPub));
        KeysetHandle keysetHandlePriv = TestUtil.createKeysetHandle(TestUtil.createKeyset(primaryPriv));
        HybridEncrypt hybridEncrypt = HybridEncryptFactory.getPrimitive(keysetHandlePub);
        HybridDecrypt hybridDecrypt = HybridDecryptFactory.getPrimitive(keysetHandlePriv);
        byte[] plaintext = Random.randBytes(20);
        byte[] contextInfo = Random.randBytes(20);
        byte[] ciphertext = hybridEncrypt.encrypt(plaintext, contextInfo);
        Assert.assertArrayEquals(plaintext, hybridDecrypt.decrypt(ciphertext, contextInfo));
    }

    /**
     * Tests that a public key is extracted properly from a private key.
     */
    @Test
    public void testGetPublicKeyData() throws Exception {
        KeysetHandle privateHandle = KeysetHandle.generateNew(ECIES_P256_HKDF_HMAC_SHA256_AES128_CTR_HMAC_SHA256);
        KeyData privateKeyData = TestUtil.getKeyset(privateHandle).getKey(0).getKeyData();
        EciesAeadHkdfPrivateKeyManager privateManager = new EciesAeadHkdfPrivateKeyManager();
        KeyData publicKeyData = privateManager.getPublicKeyData(privateKeyData.getValue());
        Assert.assertEquals(EciesAeadHkdfPublicKeyManager.TYPE_URL, publicKeyData.getTypeUrl());
        Assert.assertEquals(ASYMMETRIC_PUBLIC, publicKeyData.getKeyMaterialType());
        EciesAeadHkdfPrivateKey privateKey = EciesAeadHkdfPrivateKey.parseFrom(privateKeyData.getValue());
        Assert.assertArrayEquals(privateKey.getPublicKey().toByteArray(), publicKeyData.getValue().toByteArray());
        EciesAeadHkdfPublicKeyManager publicManager = new EciesAeadHkdfPublicKeyManager();
        HybridEncrypt hybridEncrypt = publicManager.getPrimitive(publicKeyData.getValue());
        HybridDecrypt hybridDecrypt = privateManager.getPrimitive(privateKeyData.getValue());
        byte[] message = Random.randBytes(20);
        byte[] contextInfo = Random.randBytes(20);
        Assert.assertArrayEquals(message, hybridDecrypt.decrypt(hybridEncrypt.encrypt(message, contextInfo), contextInfo));
    }
}

