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
package com.google.crypto.tink.config;


import TinkConfig.TINK_1_1_0;
import com.google.crypto.tink.Config;
import com.google.crypto.tink.Registry;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.proto.RegistryConfig;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import static TinkConfig.TINK_1_0_0;
import static TinkConfig.TINK_1_1_0;


/**
 * Tests for TinkConfig. Using FixedMethodOrder to ensure that aaaTestInitialization runs first, as
 * it tests execution of a static block within referenced Config-classes.
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TinkConfigTest {
    @Test
    public void aaaTestInitialization() throws Exception {
        try {
            Registry.getCatalogue("tinkmac");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("MacConfig.register()");
        }
        try {
            Registry.getCatalogue("tinkaead");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("AeadConfig.register()");
        }
        try {
            Registry.getCatalogue("tinkhybriddecrypt");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("HybridConfig.register()");
        }
        try {
            Registry.getCatalogue("tinkhybridencrypt");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("HybridConfig.register()");
        }
        try {
            Registry.getCatalogue("tinkpublickeysign");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("SignatureConfig.register()");
        }
        try {
            Registry.getCatalogue("tinkpublickeyverify");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("SignatureConfig.register()");
        }
        String macTypeUrl = "type.googleapis.com/google.crypto.tink.HmacKey";
        String aeadTypeUrl = "type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey";
        String daeadTypeUrl = "type.googleapis.com/google.crypto.tink.AesSivKey";
        String hybridTypeUrl = "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPrivateKey";
        String signTypeUrl = "type.googleapis.com/google.crypto.tink.EcdsaPrivateKey";
        String streamingAeadTypeUrl = "type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey";
        try {
            Registry.getKeyManager(macTypeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        try {
            Registry.getKeyManager(aeadTypeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        try {
            Registry.getKeyManager(daeadTypeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        try {
            Registry.getKeyManager(hybridTypeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        try {
            Registry.getKeyManager(signTypeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        try {
            Registry.getKeyManager(streamingAeadTypeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        // Initialize the config.
        TinkConfig.register();
        // Now the catalogues should be present.
        Registry.getCatalogue("tinkmac");
        Registry.getCatalogue("tinkaead");
        Registry.getCatalogue("tinkdeterministicaead");
        Registry.getCatalogue("tinkhybridencrypt");
        Registry.getCatalogue("tinkhybriddecrypt");
        Registry.getCatalogue("tinkpublickeysign");
        Registry.getCatalogue("tinkpublickeyverify");
        // After registration the key managers should be present.
        Config.register(TINK_1_1_0);
        Registry.getKeyManager(macTypeUrl);
        Registry.getKeyManager(aeadTypeUrl);
        Registry.getKeyManager(daeadTypeUrl);
        Registry.getKeyManager(hybridTypeUrl);
        Registry.getKeyManager(signTypeUrl);
        Registry.getKeyManager(streamingAeadTypeUrl);
    }

    @Test
    public void testConfigContentsVersion1_0_0() throws Exception {
        RegistryConfig config = TINK_1_0_0;
        Assert.assertEquals(13, config.getEntryCount());
        Assert.assertEquals("TINK_1_0_0", config.getConfigName());
        TestUtil.verifyConfigEntry(config.getEntry(0), "TinkMac", "Mac", "type.googleapis.com/google.crypto.tink.HmacKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(1), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(2), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.AesEaxKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(3), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.AesGcmKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(4), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.ChaCha20Poly1305Key", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(5), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.KmsAeadKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(6), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(7), "TinkHybridDecrypt", "HybridDecrypt", "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPrivateKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(8), "TinkHybridEncrypt", "HybridEncrypt", "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPublicKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(9), "TinkPublicKeySign", "PublicKeySign", "type.googleapis.com/google.crypto.tink.EcdsaPrivateKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(10), "TinkPublicKeySign", "PublicKeySign", "type.googleapis.com/google.crypto.tink.Ed25519PrivateKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(11), "TinkPublicKeyVerify", "PublicKeyVerify", "type.googleapis.com/google.crypto.tink.EcdsaPublicKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(12), "TinkPublicKeyVerify", "PublicKeyVerify", "type.googleapis.com/google.crypto.tink.Ed25519PublicKey", true, 0);
    }

    @Test
    public void testConfigContentsVersion1_1_0() throws Exception {
        RegistryConfig config = TINK_1_1_0;
        Assert.assertEquals(16, config.getEntryCount());
        Assert.assertEquals("TINK_1_1_0", config.getConfigName());
        TestUtil.verifyConfigEntry(config.getEntry(0), "TinkMac", "Mac", "type.googleapis.com/google.crypto.tink.HmacKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(1), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.AesCtrHmacAeadKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(2), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.AesEaxKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(3), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.AesGcmKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(4), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.ChaCha20Poly1305Key", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(5), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.KmsAeadKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(6), "TinkAead", "Aead", "type.googleapis.com/google.crypto.tink.KmsEnvelopeAeadKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(7), "TinkHybridDecrypt", "HybridDecrypt", "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPrivateKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(8), "TinkHybridEncrypt", "HybridEncrypt", "type.googleapis.com/google.crypto.tink.EciesAeadHkdfPublicKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(9), "TinkPublicKeySign", "PublicKeySign", "type.googleapis.com/google.crypto.tink.EcdsaPrivateKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(10), "TinkPublicKeySign", "PublicKeySign", "type.googleapis.com/google.crypto.tink.Ed25519PrivateKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(11), "TinkPublicKeyVerify", "PublicKeyVerify", "type.googleapis.com/google.crypto.tink.EcdsaPublicKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(12), "TinkPublicKeyVerify", "PublicKeyVerify", "type.googleapis.com/google.crypto.tink.Ed25519PublicKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(13), "TinkDeterministicAead", "DeterministicAead", "type.googleapis.com/google.crypto.tink.AesSivKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(14), "TinkStreamingAead", "StreamingAead", "type.googleapis.com/google.crypto.tink.AesCtrHmacStreamingKey", true, 0);
        TestUtil.verifyConfigEntry(config.getEntry(15), "TinkStreamingAead", "StreamingAead", "type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey", true, 0);
    }
}

