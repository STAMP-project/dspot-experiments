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


import MacKeyTemplates.HMAC_SHA256_128BITTAG;
import com.google.crypto.tink.mac.MacKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for JsonKeysetWriter.
 */
@RunWith(JUnit4.class)
public class JsonKeysetWriterTest {
    @Test
    public void testWrite_singleKey_shouldWork() throws Exception {
        KeyTemplate template = MacKeyTemplates.HMAC_SHA256_128BITTAG;
        KeysetHandle handle1 = KeysetHandle.generateNew(template);
        testWrite_shouldWork(handle1);
    }

    @Test
    public void testWrite_multipleKeys_shouldWork() throws Exception {
        KeyTemplate template = MacKeyTemplates.HMAC_SHA256_128BITTAG;
        KeysetHandle handle1 = KeysetManager.withEmptyKeyset().rotate(template).add(template).add(template).getKeysetHandle();
        testWrite_shouldWork(handle1);
    }

    @Test
    public void testWriteEncrypted_singleKey_shouldWork() throws Exception {
        // Encrypt the keyset with an AeadKey.
        KeysetHandle handle1 = KeysetHandle.generateNew(HMAC_SHA256_128BITTAG);
        testWriteEncrypted_shouldWork(handle1);
    }

    @Test
    public void testWriteEncrypted_multipleKeys_shouldWork() throws Exception {
        // Encrypt the keyset with an AeadKey.
        KeyTemplate template = MacKeyTemplates.HMAC_SHA256_128BITTAG;
        KeysetHandle handle1 = KeysetManager.withEmptyKeyset().rotate(template).add(template).add(template).getKeysetHandle();
        testWriteEncrypted_shouldWork(handle1);
    }
}

