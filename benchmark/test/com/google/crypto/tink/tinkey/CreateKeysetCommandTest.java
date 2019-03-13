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
package com.google.crypto.tink.tinkey;


import com.google.crypto.tink.mac.MacKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code CreateKeysetCommand}.
 */
@RunWith(JUnit4.class)
public class CreateKeysetCommandTest {
    private static final KeyTemplate KEY_TEMPLATE = MacKeyTemplates.HMAC_SHA256_128BITTAG;

    @Test
    public void testCreateCleartext_shouldCreateNewKeyset() throws Exception {
        testCreateCleartext_shouldCreateNewKeyset("json");
        testCreateCleartext_shouldCreateNewKeyset("binary");
    }

    @Test
    public void testCreateEncrypted_shouldCreateNewKeyset() throws Exception {
        testCreateEncrypted_shouldCreateNewKeyset("json");
        testCreateEncrypted_shouldCreateNewKeyset("binary");
    }
}

