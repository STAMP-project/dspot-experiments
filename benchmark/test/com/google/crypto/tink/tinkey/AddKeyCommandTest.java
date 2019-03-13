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


import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.mac.MacKeyTemplates;
import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code AddKeyCommand}.
 */
@RunWith(JUnit4.class)
public class AddKeyCommandTest {
    private static final KeyTemplate EXISTING_TEMPLATE = MacKeyTemplates.HMAC_SHA256_128BITTAG;

    private static final KeyTemplate NEW_TEMPLATE = MacKeyTemplates.HMAC_SHA256_256BITTAG;

    private static final String OUTPUT_FORMAT = "json";

    private static final String INPUT_FORMAT = "json";

    @Test
    public void testAddCleartext_shouldAddNewKey() throws Exception {
        // Create an input stream containing a cleartext keyset.
        String masterKeyUri = null;
        String credentialPath = null;
        InputStream inputStream = TinkeyUtil.createKeyset(AddKeyCommandTest.EXISTING_TEMPLATE, AddKeyCommandTest.INPUT_FORMAT, masterKeyUri, credentialPath);
        // Add a new key to the existing keyset.
        Keyset keyset = addNewKeyToKeyset(AddKeyCommandTest.OUTPUT_FORMAT, inputStream, AddKeyCommandTest.INPUT_FORMAT, masterKeyUri, credentialPath, AddKeyCommandTest.NEW_TEMPLATE).read();
        assertThat(keyset.getKeyCount()).isEqualTo(2);
        assertThat(keyset.getPrimaryKeyId()).isEqualTo(keyset.getKey(0).getKeyId());
        TestUtil.assertHmacKey(AddKeyCommandTest.EXISTING_TEMPLATE, keyset.getKey(0));
        TestUtil.assertHmacKey(AddKeyCommandTest.NEW_TEMPLATE, keyset.getKey(1));
    }

    @Test
    public void testAddCleartext_shouldThrowExceptionIfExistingKeysetIsEmpty() throws Exception {
        InputStream emptyStream = new ByteArrayInputStream(new byte[0]);
        String masterKeyUri = null;// This ensures that the keyset won't be encrypted.

        String credentialPath = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            AddKeyCommand.add(outputStream, AddKeyCommandTest.OUTPUT_FORMAT, emptyStream, AddKeyCommandTest.INPUT_FORMAT, masterKeyUri, credentialPath, AddKeyCommandTest.NEW_TEMPLATE);
            Assert.fail("Expected IOException");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testAddEncrypted_shouldAddNewKey() throws Exception {
        // Create an input stream containing an encrypted keyset.
        String masterKeyUri = TestUtil.RESTRICTED_CRYPTO_KEY_URI;
        String credentialPath = TestUtil.SERVICE_ACCOUNT_FILE;
        InputStream inputStream = TinkeyUtil.createKeyset(AddKeyCommandTest.EXISTING_TEMPLATE, AddKeyCommandTest.INPUT_FORMAT, masterKeyUri, credentialPath);
        EncryptedKeyset encryptedKeyset = addNewKeyToKeyset(AddKeyCommandTest.OUTPUT_FORMAT, inputStream, AddKeyCommandTest.INPUT_FORMAT, masterKeyUri, credentialPath, AddKeyCommandTest.NEW_TEMPLATE).readEncrypted();
        KeysetInfo keysetInfo = encryptedKeyset.getKeysetInfo();
        assertThat(keysetInfo.getKeyInfoCount()).isEqualTo(2);
        assertThat(keysetInfo.getPrimaryKeyId()).isEqualTo(keysetInfo.getKeyInfo(0).getKeyId());
        TestUtil.assertKeyInfo(AddKeyCommandTest.EXISTING_TEMPLATE, keysetInfo.getKeyInfo(0));
        TestUtil.assertKeyInfo(AddKeyCommandTest.NEW_TEMPLATE, keysetInfo.getKeyInfo(0));
    }
}

