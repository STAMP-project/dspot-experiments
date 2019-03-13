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


import Ed25519PublicKeyManager.TYPE_URL;
import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import SignatureKeyTemplates.ED25519;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.PublicKeyVerify;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.Ed25519PrivateKey;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.subtle.Ed25519Sign;
import com.google.crypto.tink.subtle.Ed25519Verify;
import com.google.crypto.tink.subtle.Random;
import com.google.protobuf.MessageLite;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static SignatureKeyTemplates.ED25519;


/**
 * Unit tests for Ed25519PrivateKeyManager.
 */
@RunWith(JUnit4.class)
public class Ed25519PrivateKeyManagerTest {
    @Test
    public void testBasic() throws Exception {
        Ed25519PrivateKeyManager manager = new Ed25519PrivateKeyManager();
        KeyTemplate template = ED25519;
        MessageLite key = manager.newKey(template.getValue());
        Assert.assertTrue((key instanceof Ed25519PrivateKey));
        Ed25519PrivateKey keyProto = ((Ed25519PrivateKey) (key));
        Assert.assertEquals(32, keyProto.getKeyValue().size());
        PublicKeySign signer = manager.getPrimitive(key);
        Assert.assertTrue((signer instanceof Ed25519Sign));
        byte[] message = Random.randBytes(20);
        byte[] signature = signer.sign(message);
        Assert.assertEquals(64, signature.length);
        Ed25519PublicKeyManager publicKeyManager = new Ed25519PublicKeyManager();
        PublicKeyVerify verifier = publicKeyManager.getPrimitive(keyProto.getPublicKey());
        Assert.assertTrue((verifier instanceof Ed25519Verify));
        try {
            verifier.verify(signature, message);
        } catch (GeneralSecurityException e) {
            Assert.fail(("Do not expect GeneralSecurityException: " + e));
        }
    }

    /**
     * Tests that a public key is extracted properly from a private key.
     */
    @Test
    public void testGetPublicKeyData() throws Exception {
        KeysetHandle privateHandle = KeysetHandle.generateNew(ED25519);
        KeyData privateKeyData = TestUtil.getKeyset(privateHandle).getKey(0).getKeyData();
        Ed25519PrivateKeyManager privateManager = new Ed25519PrivateKeyManager();
        KeyData publicKeyData = privateManager.getPublicKeyData(privateKeyData.getValue());
        Assert.assertEquals(TYPE_URL, publicKeyData.getTypeUrl());
        Assert.assertEquals(ASYMMETRIC_PUBLIC, publicKeyData.getKeyMaterialType());
        Ed25519PrivateKey privateKey = Ed25519PrivateKey.parseFrom(privateKeyData.getValue());
        Assert.assertArrayEquals(privateKey.getPublicKey().toByteArray(), publicKeyData.getValue().toByteArray());
        Ed25519PublicKeyManager publicManager = new Ed25519PublicKeyManager();
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

