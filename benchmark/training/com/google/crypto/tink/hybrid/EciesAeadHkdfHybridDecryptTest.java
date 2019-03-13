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
package com.google.crypto.tink.hybrid;


import AeadKeyTemplates.AES128_CTR_HMAC_SHA256;
import AeadKeyTemplates.AES128_GCM;
import CurveType.NIST_P256;
import CurveType.NIST_P384;
import CurveType.NIST_P521;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link EciesAeadHkdfHybridDecrypt}.
 */
@RunWith(JUnit4.class)
public class EciesAeadHkdfHybridDecryptTest {
    @Test
    public void testModifyDecrypt() throws Exception {
        testModifyDecrypt(NIST_P256, AES128_CTR_HMAC_SHA256);
        testModifyDecrypt(NIST_P384, AES128_CTR_HMAC_SHA256);
        testModifyDecrypt(NIST_P521, AES128_CTR_HMAC_SHA256);
        testModifyDecrypt(NIST_P256, AES128_GCM);
        testModifyDecrypt(NIST_P384, AES128_GCM);
        testModifyDecrypt(NIST_P521, AES128_GCM);
    }
}

