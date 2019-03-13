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
package com.google.crypto.tink.streamingaead;


import AesCtrHmacStreamingKeyManager.TYPE_URL;
import HashType.SHA256;
import OutputPrefixType.RAW;
import com.google.crypto.tink.proto.AesCtrHmacStreamingKeyFormat;
import com.google.crypto.tink.proto.AesGcmHkdfStreamingKeyFormat;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static StreamingAeadKeyTemplates.AES128_CTR_HMAC_SHA256_4KB;
import static StreamingAeadKeyTemplates.AES128_GCM_HKDF_4KB;
import static StreamingAeadKeyTemplates.AES256_CTR_HMAC_SHA256_4KB;
import static StreamingAeadKeyTemplates.AES256_GCM_HKDF_4KB;


/**
 * Tests for StreamingAeadKeyTemplates.
 */
@RunWith(JUnit4.class)
public class StreamingAeadKeyTemplatesTest {
    @Test
    public void testAES128_CTR_HMAC_SHA256_4KB() throws Exception {
        KeyTemplate template = AES128_CTR_HMAC_SHA256_4KB;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(RAW, template.getOutputPrefixType());
        AesCtrHmacStreamingKeyFormat format = AesCtrHmacStreamingKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(16, format.getKeySize());
        Assert.assertEquals(16, format.getParams().getDerivedKeySize());
        Assert.assertEquals(SHA256, format.getParams().getHkdfHashType());
        Assert.assertEquals(4096, format.getParams().getCiphertextSegmentSize());
        Assert.assertEquals(SHA256, format.getParams().getHmacParams().getHash());
        Assert.assertEquals(32, format.getParams().getHmacParams().getTagSize());
    }

    @Test
    public void testAES256_CTR_HMAC_SHA256_4KB() throws Exception {
        KeyTemplate template = AES256_CTR_HMAC_SHA256_4KB;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(RAW, template.getOutputPrefixType());
        AesCtrHmacStreamingKeyFormat format = AesCtrHmacStreamingKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(32, format.getKeySize());
        Assert.assertEquals(32, format.getParams().getDerivedKeySize());
        Assert.assertEquals(SHA256, format.getParams().getHkdfHashType());
        Assert.assertEquals(4096, format.getParams().getCiphertextSegmentSize());
        Assert.assertEquals(SHA256, format.getParams().getHmacParams().getHash());
        Assert.assertEquals(32, format.getParams().getHmacParams().getTagSize());
    }

    @Test
    public void testAES128_GCM_HKDF_4KB() throws Exception {
        KeyTemplate template = AES128_GCM_HKDF_4KB;
        Assert.assertEquals(AesGcmHkdfStreamingKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(RAW, template.getOutputPrefixType());
        AesGcmHkdfStreamingKeyFormat format = AesGcmHkdfStreamingKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(16, format.getKeySize());
        Assert.assertEquals(16, format.getParams().getDerivedKeySize());
        Assert.assertEquals(SHA256, format.getParams().getHkdfHashType());
        Assert.assertEquals(4096, format.getParams().getCiphertextSegmentSize());
    }

    @Test
    public void testAES256_GCM_HKDF_4KB() throws Exception {
        KeyTemplate template = AES256_GCM_HKDF_4KB;
        Assert.assertEquals(AesGcmHkdfStreamingKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(RAW, template.getOutputPrefixType());
        AesGcmHkdfStreamingKeyFormat format = AesGcmHkdfStreamingKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(32, format.getKeySize());
        Assert.assertEquals(32, format.getParams().getDerivedKeySize());
        Assert.assertEquals(SHA256, format.getParams().getHkdfHashType());
        Assert.assertEquals(4096, format.getParams().getCiphertextSegmentSize());
    }

    @Test
    public void testCreateAesCtrHmacStreamingKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int mainKeySize = 42;
        int derivedKeySize = 24;
        int tagSize = 45;
        int ciphertextSegmentSize = 12345;
        HashType hkdfHashType = HashType.SHA512;
        HashType macHashType = HashType.UNKNOWN_HASH;
        KeyTemplate template = StreamingAeadKeyTemplates.createAesCtrHmacStreamingKeyTemplate(mainKeySize, hkdfHashType, derivedKeySize, macHashType, tagSize, ciphertextSegmentSize);
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(RAW, template.getOutputPrefixType());
        AesCtrHmacStreamingKeyFormat format = AesCtrHmacStreamingKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(mainKeySize, format.getKeySize());
        Assert.assertEquals(derivedKeySize, format.getParams().getDerivedKeySize());
        Assert.assertEquals(hkdfHashType, format.getParams().getHkdfHashType());
        Assert.assertEquals(ciphertextSegmentSize, format.getParams().getCiphertextSegmentSize());
        Assert.assertEquals(macHashType, format.getParams().getHmacParams().getHash());
        Assert.assertEquals(tagSize, format.getParams().getHmacParams().getTagSize());
    }

    @Test
    public void testCreateAesGcmHkdfStreamingKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int mainKeySize = 42;
        int derivedKeySize = 24;
        int ciphertextSegmentSize = 12345;
        HashType hkdfHashType = HashType.SHA512;
        KeyTemplate template = StreamingAeadKeyTemplates.createAesGcmHkdfStreamingKeyTemplate(mainKeySize, hkdfHashType, derivedKeySize, ciphertextSegmentSize);
        Assert.assertEquals(AesGcmHkdfStreamingKeyManager.TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(RAW, template.getOutputPrefixType());
        AesGcmHkdfStreamingKeyFormat format = AesGcmHkdfStreamingKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(mainKeySize, format.getKeySize());
        Assert.assertEquals(derivedKeySize, format.getParams().getDerivedKeySize());
        Assert.assertEquals(hkdfHashType, format.getParams().getHkdfHashType());
        Assert.assertEquals(ciphertextSegmentSize, format.getParams().getCiphertextSegmentSize());
    }
}

