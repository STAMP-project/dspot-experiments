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
package com.google.crypto.tink.mac;


import HashType.SHA256;
import HmacKeyManager.TYPE_URL;
import OutputPrefixType.TINK;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HmacKeyFormat;
import com.google.crypto.tink.proto.KeyTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static MacKeyTemplates.HMAC_SHA256_128BITTAG;
import static MacKeyTemplates.HMAC_SHA256_256BITTAG;


/**
 * Tests for MacKeyTemplates.
 */
@RunWith(JUnit4.class)
public class MacKeyTemplatesTest {
    @Test
    public void testHMAC_SHA256_128BITTAG() throws Exception {
        KeyTemplate template = HMAC_SHA256_128BITTAG;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        HmacKeyFormat format = HmacKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(32, format.getKeySize());
        Assert.assertEquals(16, format.getParams().getTagSize());
        Assert.assertEquals(SHA256, format.getParams().getHash());
    }

    @Test
    public void testHMAC_SHA256_256BITTAG() throws Exception {
        KeyTemplate template = HMAC_SHA256_256BITTAG;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        HmacKeyFormat format = HmacKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(32, format.getKeySize());
        Assert.assertEquals(32, format.getParams().getTagSize());
        Assert.assertEquals(SHA256, format.getParams().getHash());
    }

    @Test
    public void testCreateHmacKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int keySize = 42;
        int tagSize = 24;
        HashType hashType = HashType.SHA512;
        KeyTemplate template = MacKeyTemplates.createHmacKeyTemplate(keySize, tagSize, hashType);
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        HmacKeyFormat format = HmacKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(keySize, format.getKeySize());
        Assert.assertEquals(tagSize, format.getParams().getTagSize());
        Assert.assertEquals(hashType, format.getParams().getHash());
    }
}

