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


import HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM;
import SignatureKeyTemplates.ECDSA_P256;
import SignatureKeyTemplates.ED25519;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@code CreatePublicKeysetCommand}.
 */
@RunWith(JUnit4.class)
public class CreatePublicKeysetCommandTest {
    private enum KeyType {

        HYBRID,
        SIGNATURE;}

    private static final String OUTPUT_FORMAT = "json";

    private static final String INPUT_FORMAT = "json";

    @Test
    public void testCreate_hybrid_cleartextPrivate_shouldCreateCleartextPublic() throws Exception {
        testCreate_cleartextPrivate_shouldCreateCleartextPublic(ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM, CreatePublicKeysetCommandTest.KeyType.HYBRID);
    }

    @Test
    public void testCreate_hybrid_encryptedPrivate_shouldCreateCleartextPublic() throws Exception {
        testCreate_encryptedPrivate_shouldCreateCleartextPublic(ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM, CreatePublicKeysetCommandTest.KeyType.HYBRID);
    }

    @Test
    public void testCreate_signature_cleartextPrivate_shouldCreateCleartextPublic() throws Exception {
        testCreate_cleartextPrivate_shouldCreateCleartextPublic(ECDSA_P256, CreatePublicKeysetCommandTest.KeyType.SIGNATURE);
        testCreate_cleartextPrivate_shouldCreateCleartextPublic(ED25519, CreatePublicKeysetCommandTest.KeyType.SIGNATURE);
    }

    @Test
    public void testCreate_signature_encryptedPrivate_shouldCreateCleartextPublic() throws Exception {
        testCreate_encryptedPrivate_shouldCreateCleartextPublic(ECDSA_P256, CreatePublicKeysetCommandTest.KeyType.SIGNATURE);
        testCreate_encryptedPrivate_shouldCreateCleartextPublic(ED25519, CreatePublicKeysetCommandTest.KeyType.SIGNATURE);
    }
}

