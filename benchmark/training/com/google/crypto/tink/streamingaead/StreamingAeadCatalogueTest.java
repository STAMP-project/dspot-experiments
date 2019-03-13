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


import com.google.crypto.tink.KeyManager;
import com.google.crypto.tink.StreamingAead;
import com.google.crypto.tink.proto.KeyTypeEntry;
import com.google.crypto.tink.proto.RegistryConfig;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static StreamingAeadConfig.TINK_1_1_0;


/**
 * Tests for StreamingAeadCatalogue.
 */
@RunWith(JUnit4.class)
public class StreamingAeadCatalogueTest {
    @Test
    public void testBasic() throws Exception {
        StreamingAeadCatalogue catalogue = new StreamingAeadCatalogue();
        // Check a single key type, incl. case-insensitve primitive name.
        String keyType = "type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey";
        {
            KeyManager<StreamingAead> manager = catalogue.getKeyManager(keyType, "StreamingAead", 0);
            assertThat(manager.doesSupport(keyType)).isTrue();
        }
        {
            KeyManager<StreamingAead> manager = catalogue.getKeyManager(keyType, "STReaMIngAeAD", 0);
            assertThat(manager.doesSupport(keyType)).isTrue();
        }
        {
            KeyManager<StreamingAead> manager = catalogue.getKeyManager(keyType, "STREAMINgaEAD", 0);
            assertThat(manager.doesSupport(keyType)).isTrue();
        }
        // Check all entries from the current StreamingAeadConfig.
        RegistryConfig config = TINK_1_1_0;
        int count = 0;
        for (KeyTypeEntry entry : config.getEntryList()) {
            if ("StreamingAead".equals(entry.getPrimitiveName())) {
                count = count + 1;
                KeyManager<StreamingAead> manager = catalogue.getKeyManager(entry.getTypeUrl(), "streamingaead", entry.getKeyManagerVersion());
                assertThat(manager.doesSupport(entry.getTypeUrl())).isTrue();
            }
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void testErrors() throws Exception {
        StreamingAeadCatalogue catalogue = new StreamingAeadCatalogue();
        String keyType = "type.googleapis.com/google.crypto.tink.AesGcmHkdfStreamingKey";
        // Wrong primitive name.
        try {
            catalogue.getKeyManager(keyType, "aead", 0);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No support for primitive");
        }
        // Wrong key manager version.
        try {
            catalogue.getKeyManager(keyType, "StreamingAead", 1);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager");
            assertThat(e.toString()).contains("version at least");
        }
        // Wrong key type.
        try {
            catalogue.getKeyManager("some.unknown.key.type", "streamingAead", 0);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No support for primitive");
            assertThat(e.toString()).contains("with key type");
        }
    }
}

