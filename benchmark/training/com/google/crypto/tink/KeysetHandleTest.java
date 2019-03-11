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
package com.google.crypto.tink;


import AeadKeyTemplates.AES128_GCM;
import KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
import KeyStatusType.ENABLED;
import MacKeyTemplates.HMAC_SHA256_128BITTAG;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import SignatureConfig.ECDSA_PUBLIC_KEY_TYPE_URL;
import SignatureKeyTemplates.ECDSA_P256;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.EcdsaPrivateKey;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.signature.PublicKeySignFactory;
import com.google.crypto.tink.signature.PublicKeyVerifyFactory;
import com.google.crypto.tink.subtle.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for KeysetHandle.
 */
@RunWith(JUnit4.class)
public class KeysetHandleTest {
    /**
     * Tests that toString doesn't contain key material.
     */
    @Test
    public void testToString() throws Exception {
        String keyValue = "01234567890123456";
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, ENABLED, TINK));
        KeysetHandle handle = KeysetHandle.fromKeyset(keyset);
        Assert.assertEquals(keyset, handle.getKeyset());
        String keysetInfo = handle.toString();
        Assert.assertFalse(keysetInfo.contains(keyValue));
        Assert.assertTrue(handle.getKeyset().toString().contains(keyValue));
    }

    @Test
    public void testWriteEncrypted() throws Exception {
        KeysetHandle handle = KeysetHandle.generateNew(HMAC_SHA256_128BITTAG);
        // Encrypt the keyset with an AeadKey.
        KeyTemplate masterKeyTemplate = AeadKeyTemplates.AES128_EAX;
        Aead masterKey = Registry.getPrimitive(Registry.newKeyData(masterKeyTemplate));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        KeysetWriter writer = BinaryKeysetWriter.withOutputStream(outputStream);
        handle.write(writer, masterKey);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        KeysetReader reader = BinaryKeysetReader.withInputStream(inputStream);
        KeysetHandle handle2 = KeysetHandle.read(reader, masterKey);
        Assert.assertEquals(handle.getKeyset(), handle2.getKeyset());
    }

    /**
     * Tests a public keyset is extracted properly from a private keyset.
     */
    @Test
    public void testGetPublicKeysetHandle() throws Exception {
        KeysetHandle privateHandle = KeysetHandle.generateNew(ECDSA_P256);
        KeyData privateKeyData = privateHandle.getKeyset().getKey(0).getKeyData();
        EcdsaPrivateKey privateKey = EcdsaPrivateKey.parseFrom(privateKeyData.getValue());
        KeysetHandle publicHandle = privateHandle.getPublicKeysetHandle();
        Assert.assertEquals(1, publicHandle.getKeyset().getKeyCount());
        Assert.assertEquals(privateHandle.getKeyset().getPrimaryKeyId(), publicHandle.getKeyset().getPrimaryKeyId());
        KeyData publicKeyData = publicHandle.getKeyset().getKey(0).getKeyData();
        Assert.assertEquals(ECDSA_PUBLIC_KEY_TYPE_URL, publicKeyData.getTypeUrl());
        Assert.assertEquals(ASYMMETRIC_PUBLIC, publicKeyData.getKeyMaterialType());
        Assert.assertArrayEquals(privateKey.getPublicKey().toByteArray(), publicKeyData.getValue().toByteArray());
        PublicKeySign signer = PublicKeySignFactory.getPrimitive(privateHandle);
        PublicKeyVerify verifier = PublicKeyVerifyFactory.getPrimitive(publicHandle);
        byte[] message = Random.randBytes(20);
        try {
            verifier.verify(signer.sign(message), message);
        } catch (GeneralSecurityException e) {
            Assert.fail(("Should not fail: " + e));
        }
    }

    /**
     * Tests that when encryption failed an exception is thrown.
     */
    @Test
    public void testEncryptFailed() throws Exception {
        KeysetHandle handle = KeysetManager.withEmptyKeyset().rotate(HMAC_SHA256_128BITTAG).getKeysetHandle();
        // Encrypt with dummy Aead.
        TestUtil.DummyAead faultyAead = new TestUtil.DummyAead();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        KeysetWriter writer = BinaryKeysetWriter.withOutputStream(outputStream);
        try {
            handle.write(writer, faultyAead);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "dummy");
        }
    }

    @Test
    public void testVoidInputs() throws Exception {
        KeysetHandle unused;
        try {
            KeysetReader reader = BinaryKeysetReader.withBytes(new byte[0]);
            unused = /* masterKey */
            KeysetHandle.read(reader, null);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "empty keyset");
        }
    }

    @Test
    public void testGetPrimitive_basic() throws Exception {
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(Registry.newKeyData(AES128_GCM), 42, ENABLED, TINK));
        KeysetHandle handle = KeysetHandle.fromKeyset(keyset);
        byte[] message = Random.randBytes(20);
        byte[] aad = Random.randBytes(20);
        Aead aead = handle.getPrimitive(Aead.class);
        Assert.assertArrayEquals(aead.decrypt(aead.encrypt(message, aad), aad), message);
    }

    // Tests that getPrimitive does correct wrapping and not just return the primary. For this, we
    // simply add a raw, non-primary key and encrypt directly with it.
    @Test
    public void testGetPrimitive_wrappingDoneCorrectly() throws Exception {
        KeyData rawKeyData = Registry.newKeyData(AES128_GCM);
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(Registry.newKeyData(AES128_GCM), 42, ENABLED, TINK), TestUtil.createKey(rawKeyData, 43, ENABLED, RAW));
        KeysetHandle handle = KeysetHandle.fromKeyset(keyset);
        byte[] message = Random.randBytes(20);
        byte[] aad = Random.randBytes(20);
        Aead aeadToEncrypt = Registry.getPrimitive(rawKeyData, Aead.class);
        Aead aead = handle.getPrimitive(Aead.class);
        Assert.assertArrayEquals(aead.decrypt(aeadToEncrypt.encrypt(message, aad), aad), message);
    }

    @Test
    public void testGetPrimitive_customKeyManager() throws Exception {
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(Registry.newKeyData(AES128_GCM), 42, ENABLED, TINK));
        KeysetHandle handle = KeysetHandle.fromKeyset(keyset);
        // The TestKeyManager accepts AES128_GCM keys, but creates a DummyAead which always fails.
        Aead aead = handle.getPrimitive(new KeyManagerBaseTest.TestKeyManager(), Aead.class);
        try {
            aead.encrypt(new byte[0], new byte[0]);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "dummy");
        }
    }

    @Test
    public void testGetPrimitive_nullKeyManager_throwsInvalidArgument() throws Exception {
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(Registry.newKeyData(AES128_GCM), 42, ENABLED, TINK));
        KeysetHandle handle = KeysetHandle.fromKeyset(keyset);
        try {
            handle.getPrimitive(null, Aead.class);
            Assert.fail("Expected GeneralSecurityException");
        } catch (IllegalArgumentException e) {
            TestUtil.assertExceptionContains(e, "customKeyManager");
        }
    }

    @Test
    public void readNoSecretShouldWork() throws Exception {
        KeysetHandle privateHandle = KeysetHandle.generateNew(ECDSA_P256);
        Keyset keyset = privateHandle.getPublicKeysetHandle().getKeyset();
        Keyset keyset2 = KeysetHandle.readNoSecret(keyset.toByteArray()).getKeyset();
        Keyset keyset3 = KeysetHandle.readNoSecret(BinaryKeysetReader.withBytes(keyset.toByteArray())).getKeyset();
        Assert.assertEquals(keyset, keyset2);
        Assert.assertEquals(keyset, keyset3);
    }

    @Test
    public void readNoSecretFailWithTypeSymmetric() throws Exception {
        String keyValue = "01234567890123456";
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, ENABLED, TINK));
        try {
            KeysetHandle unused = KeysetHandle.readNoSecret(keyset.toByteArray());
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains secret key material");
        }
        try {
            KeysetHandle unused = KeysetHandle.readNoSecret(BinaryKeysetReader.withBytes(keyset.toByteArray()));
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains secret key material");
        }
    }

    @Test
    public void readNoSecretFailWithTypeAsymmetricPrivate() throws Exception {
        Keyset keyset = KeysetHandle.generateNew(ECDSA_P256).getKeyset();
        try {
            KeysetHandle unused = KeysetHandle.readNoSecret(keyset.toByteArray());
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains secret key material");
        }
        try {
            KeysetHandle unused = KeysetHandle.readNoSecret(BinaryKeysetReader.withBytes(keyset.toByteArray()));
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains secret key material");
        }
    }

    @Test
    public void readNoSecretFailWithEmptyKeyset() throws Exception {
        try {
            KeysetHandle unused = KeysetHandle.readNoSecret(new byte[0]);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "empty keyset");
        }
    }

    @Test
    public void readNoSecretFailWithInvalidKeyset() throws Exception {
        byte[] proto = new byte[]{ 0, 1, 2 };
        try {
            KeysetHandle unused = KeysetHandle.readNoSecret(proto);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "invalid");
        }
    }

    @Test
    public void writeNoSecretShouldWork() throws Exception {
        KeysetHandle privateHandle = KeysetHandle.generateNew(ECDSA_P256);
        KeysetHandle publicHandle = privateHandle.getPublicKeysetHandle();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        KeysetWriter writer = BinaryKeysetWriter.withOutputStream(outputStream);
        Keyset keyset = publicHandle.getKeyset();
        publicHandle.writeNoSecret(writer);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        KeysetReader reader = BinaryKeysetReader.withInputStream(inputStream);
        Keyset keyset2 = KeysetHandle.readNoSecret(reader).getKeyset();
        Assert.assertEquals(keyset, keyset2);
    }

    @Test
    public void writeNoSecretFailWithTypeSymmetric() throws Exception {
        String keyValue = "01234567890123456";
        Keyset keyset = TestUtil.createKeyset(TestUtil.createKey(TestUtil.createHmacKeyData(keyValue.getBytes("UTF-8"), 16), 42, ENABLED, TINK));
        KeysetHandle handle = KeysetHandle.fromKeyset(keyset);
        try {
            /* writer */
            handle.writeNoSecret(null);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains secret key material");
        }
    }

    @Test
    public void writeNoSecretFailWithTypeAsymmetricPrivate() throws Exception {
        KeysetHandle handle = KeysetHandle.generateNew(ECDSA_P256);
        try {
            /* writer */
            handle.writeNoSecret(null);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "keyset contains secret key material");
        }
    }
}

