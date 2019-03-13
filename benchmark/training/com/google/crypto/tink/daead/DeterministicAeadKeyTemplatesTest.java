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
package com.google.crypto.tink.daead;


import AesSivKeyManager.TYPE_URL;
import OutputPrefixType.TINK;
import com.google.crypto.tink.proto.AesSivKeyFormat;
import com.google.crypto.tink.proto.KeyTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static DeterministicAeadKeyTemplates.AES256_SIV;


/**
 * Tests for DeterministicAeadKeyTemplates.
 */
@RunWith(JUnit4.class)
public class DeterministicAeadKeyTemplatesTest {
    @Test
    public void testAES256_SIV() throws Exception {
        KeyTemplate template = AES256_SIV;
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesSivKeyFormat format = AesSivKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(64, format.getKeySize());
    }

    @Test
    public void testCreateAesSivKeyTemplate() throws Exception {
        // Intentionally using "weird" or invalid values for parameters,
        // to test that the function correctly puts them in the resulting template.
        int keySize = 42;
        KeyTemplate template = DeterministicAeadKeyTemplates.createAesSivKeyTemplate(keySize);
        Assert.assertEquals(TYPE_URL, template.getTypeUrl());
        Assert.assertEquals(TINK, template.getOutputPrefixType());
        AesSivKeyFormat format = AesSivKeyFormat.parseFrom(template.getValue());
        Assert.assertEquals(keySize, format.getKeySize());
    }
}

