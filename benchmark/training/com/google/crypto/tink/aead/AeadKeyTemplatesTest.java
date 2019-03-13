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
package com.google.crypto.tink.aead;


import AesGcmKeyManager.TYPE_URL;
import HashType.SHA256;
import OutputPrefixType.TINK;
import com.google.crypto.tink.proto.AesCtrHmacAeadKeyFormat;
import com.google.crypto.tink.proto.AesEaxKeyFormat;
import com.google.crypto.tink.proto.AesGcmKeyFormat;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KmsAeadKeyFormat;
import com.google.crypto.tink.proto.KmsEnvelopeAeadKeyFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static AeadKeyTemplates.AES128_CTR_HMAC_SHA256;
import static AeadKeyTemplates.AES128_EAX;
import static AeadKeyTemplates.AES128_GCM;
import static AeadKeyTemplates.AES256_CTR_HMAC_SHA256;
import static AeadKeyTemplates.AES256_EAX;
import static AeadKeyTemplates.AES256_GCM;
import static AeadKeyTemplates.CHACHA20_POLY1305;
import static AeadKeyTemplates.XCHACHA20_POLY1305;


/**
 * Tests for AeadKeyTemplates.
 */
@RunWith(JUnit4.class)
public class AeadKeyTemplatesTest {
    @Test
    public void testAES128_GCM() throws Exception {
        KeyTemplate template = AES128_GCM;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesGcmKeyFormat format = AesGcmKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(16, format.getKeySize());
    }

    @Test
    public void testAES256_GCM() throws Exception {
        KeyTemplate template = AES256_GCM;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesGcmKeyFormat format = AesGcmKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(32, format.getKeySize());
    }

    @Test
    public void testCreateAesGcmKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int keySize = 42;
        KeyTemplate template = AeadKeyTemplates.createAesGcmKeyTemplate(keySize);
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesGcmKeyFormat format = AesGcmKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(keySize, format.getKeySize());
    }

    @Test
    public void testAES128_EAX() throws Exception {
        KeyTemplate template = AES128_EAX;
        Assert.assertEquals(AesEaxKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesEaxKeyFormat format = AesEaxKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(16, format.getKeySize());
        Assert.assertTrue(format.hasParams());
        Assert.assertEquals(16, format.getParams().getIvSize());
    }

    @Test
    public void testAES256_EAX() throws Exception {
        KeyTemplate template = AES256_EAX;
        Assert.assertEquals(AesEaxKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesEaxKeyFormat format = AesEaxKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(32, format.getKeySize());
        Assert.assertTrue(format.hasParams());
        Assert.assertEquals(16, format.getParams().getIvSize());
    }

    @Test
    public void testCreateAesEaxKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int keySize = 42;
        int ivSize = 72;
        KeyTemplate template = AeadKeyTemplates.createAesEaxKeyTemplate(keySize, ivSize);
        Assert.assertEquals(AesEaxKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesEaxKeyFormat format = AesEaxKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(keySize, format.getKeySize());
        Assert.assertTrue(format.hasParams());
        Assert.assertEquals(ivSize, format.getParams().getIvSize());
    }

    @Test
    public void testAES128_CTR_HMAC_SHA256() throws Exception {
        KeyTemplate template = AES128_CTR_HMAC_SHA256;
        Assert.assertEquals(AesCtrHmacAeadKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesCtrHmacAeadKeyFormat format = AesCtrHmacAeadKeyFormat.parseFrom(template.getValue());
        Assert.assertTrue(format.hasAesCtrKeyFormat());
        Assert.assertTrue(format.getAesCtrKeyFormat().hasParams());
        Assert.assertEquals(16, format.getAesCtrKeyFormat().getKeySize());
        Assert.assertEquals(16, format.getAesCtrKeyFormat().getParams().getIvSize());
        Assert.assertTrue(format.hasHmacKeyFormat());
        Assert.assertTrue(format.getHmacKeyFormat().hasParams());
        Assert.assertEquals(32, format.getHmacKeyFormat().getKeySize());
        Assert.assertEquals(16, format.getHmacKeyFormat().getParams().getTagSize());
        Assert.assertEquals(SHA256, format.getHmacKeyFormat().getParams().getHash());
    }

    @Test
    public void testAES256_CTR_HMAC_SHA256() throws Exception {
        KeyTemplate template = AES256_CTR_HMAC_SHA256;
        Assert.assertEquals(AesCtrHmacAeadKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesCtrHmacAeadKeyFormat format = AesCtrHmacAeadKeyFormat.parseFrom(template.getValue());
        Assert.assertTrue(format.hasAesCtrKeyFormat());
        Assert.assertTrue(format.getAesCtrKeyFormat().hasParams());
        Assert.assertEquals(32, format.getAesCtrKeyFormat().getKeySize());
        Assert.assertEquals(16, format.getAesCtrKeyFormat().getParams().getIvSize());
        Assert.assertTrue(format.hasHmacKeyFormat());
        Assert.assertTrue(format.getHmacKeyFormat().hasParams());
        Assert.assertEquals(32, format.getHmacKeyFormat().getKeySize());
        Assert.assertEquals(32, format.getHmacKeyFormat().getParams().getTagSize());
        Assert.assertEquals(SHA256, format.getHmacKeyFormat().getParams().getHash());
    }

    @Test
    public void testCreateAesCtrHmacAeadKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int aesKeySize = 42;
        int ivSize = 72;
        int hmacKeySize = 24;
        int tagSize = 27;
        HashType hashType = HashType.UNKNOWN_HASH;
        KeyTemplate template = AeadKeyTemplates.createAesCtrHmacAeadKeyTemplate(aesKeySize, ivSize, hmacKeySize, tagSize, hashType);
        Assert.assertEquals(AesCtrHmacAeadKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesCtrHmacAeadKeyFormat format = AesCtrHmacAeadKeyFormat.parseFrom(template.getValue());
        Assert.assertTrue(format.hasAesCtrKeyFormat());
        Assert.assertTrue(format.getAesCtrKeyFormat().hasParams());
        Assert.assertEquals(aesKeySize, format.getAesCtrKeyFormat().getKeySize());
        Assert.assertEquals(ivSize, format.getAesCtrKeyFormat().getParams().getIvSize());
        Assert.assertTrue(format.hasHmacKeyFormat());
        Assert.assertTrue(format.getHmacKeyFormat().hasParams());
        Assert.assertEquals(hmacKeySize, format.getHmacKeyFormat().getKeySize());
        Assert.assertEquals(tagSize, format.getHmacKeyFormat().getParams().getTagSize());
        Assert.assertEquals(hashType, format.getHmacKeyFormat().getParams().getHash());
    }

    @Test
    public void testCHACHA20_POLY1305() throws Exception {
        KeyTemplate template = CHACHA20_POLY1305;
        Assert.assertEquals(ChaCha20Poly1305KeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        Assert.assertTrue(template.getValue().isEmpty());// Empty format.

    }

    @Test
    public void testXCHACHA20_POLY1305() throws Exception {
        KeyTemplate template = XCHACHA20_POLY1305;
        Assert.assertEquals(XChaCha20Poly1305KeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        Assert.assertTrue(template.getValue().isEmpty());// Empty format.

    }

    @Test
    public void testCreateKmsAeadKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        String keyUri = "some example URI";
        KeyTemplate template = AeadKeyTemplates.createKmsAeadKeyTemplate(keyUri);
        Assert.assertEquals(KmsAeadKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        KmsAeadKeyFormat format = KmsAeadKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(keyUri, format.getKeyUri());
    }

    @Test
    public void testCreateKmsEnvelopeAeadKeyFormat() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        String kekUri = "some example KEK URI";
        KeyTemplate dekTemplate = AES256_GCM;
        KeyTemplate template = AeadKeyTemplates.createKmsEnvelopeAeadKeyTemplate(kekUri, dekTemplate);
        Assert.assertEquals(KmsEnvelopeAeadKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        KmsEnvelopeAeadKeyFormat format = KmsEnvelopeAeadKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(kekUri, format.getKekUri());
        Assert.assertEquals(dekTemplate.toString(), format.getDekTemplate().toString());
    }
}

