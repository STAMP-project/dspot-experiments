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

import static DeterministicAeadConfig.LATEST;
import static DeterministicAeadConfig.TINK_1_1_0;


/**
 * Tests for DeterministicAeadConfig. Using FixedMethodOrder to ensure that aaaTestInitialization
 * runs first, as it tests execution of a static block within DeterministicAeadConfig-class.
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeterministicAeadConfigTest {
    // This test must run first.
    @Test
    public void aaaTestInitialization() throws Exception {
        try {
            Registry.getCatalogue("tinkdeterministicaead");
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("no catalogue found");
            assertThat(e.toString()).contains("DeterministicAeadConfig.register()");
        }
        // Before registration, the key manager should be absent.
        String typeUrl = "type.googleapis.com/google.crypto.tink.AesSivKey";
        try {
            Registry.getKeyManager(typeUrl);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException e) {
            assertThat(e.toString()).contains("No key manager found");
        }
        // Initialize the config.
        DeterministicAeadConfig.register();
        // Now the catalogues should be present.
        RegistryConfig unused = TINK_1_1_0;
        Registry.getCatalogue("tinkdeterministicaead");
        // After registration, the key manager should be present.
        Registry.getKeyManager(typeUrl);
        // Running init() manually again should succeed.
        DeterministicAeadConfig.register();
    }

    @Test
    public void testConfigContents_1_1_0() throws Exception {
        RegistryConfig config = TINK_1_1_0;
        Assert.assertEquals(1, config.getEntryCount());
        Assert.assertEquals("TINK_DETERMINISTIC_AEAD_1_1_0", config.getConfigName());
        TestUtil.verifyConfigEntry(config.getEntry(0), "TinkDeterministicAead", "DeterministicAead", "type.googleapis.com/google.crypto.tink.AesSivKey", true, 0);
    }

    @Test
    public void testConfigContents_LATEST() throws Exception {
        RegistryConfig config = LATEST;
        Assert.assertEquals(1, config.getEntryCount());
        Assert.assertEquals("TINK_DETERMINISTIC_AEAD", config.getConfigName());
        TestUtil.verifyConfigEntry(config.getEntry(0), "TinkDeterministicAead", "DeterministicAead", "type.googleapis.com/google.crypto.tink.AesSivKey", true, 0);
    }
}

