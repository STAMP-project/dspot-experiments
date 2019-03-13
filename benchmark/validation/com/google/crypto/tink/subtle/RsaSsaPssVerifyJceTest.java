/**
 * Copyright 2018 Google Inc.
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
package com.google.crypto.tink.subtle;


import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.subtle.Enums.HashType;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for RsaSsaPssVerifyJce.
 */
@RunWith(JUnit4.class)
public class RsaSsaPssVerifyJceTest {
    @Test
    public void testConstructorExceptions() throws Exception {
        int keySize = 2048;
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        RSAPublicKey pub = ((RSAPublicKey) (keyGen.generateKeyPair().getPublic()));
        try {
            new RsaSsaPssVerifyJce(pub, HashType.SHA1, HashType.SHA1, 20);
            Assert.fail("Unsafe hash, should have thrown exception.");
        } catch (GeneralSecurityException e) {
            // Expected.
            TestUtil.assertExceptionContains(e, "Unsupported hash: SHA1");
        }
    }

    @Test
    public void testWycheproofVectors() throws Exception {
        RsaSsaPssVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/rsa_pss_2048_sha256_mgf1_0_test.json");
        RsaSsaPssVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/rsa_pss_2048_sha256_mgf1_32_test.json");
        RsaSsaPssVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/rsa_pss_3072_sha256_mgf1_32_test.json");
        RsaSsaPssVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/rsa_pss_4096_sha256_mgf1_32_test.json");
        RsaSsaPssVerifyJceTest.testWycheproofVectors("../wycheproof/testvectors/rsa_pss_4096_sha512_mgf1_32_test.json");
    }
}

