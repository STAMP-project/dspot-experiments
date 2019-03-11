/**
 * Copyright (c) 2018,2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.encryption;


import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Encryption utility test.
 */
public class EncryptionUtilTest {
    private static final String TEST_SECRET = "Jaja uzh :( berglengele";

    private static final char[] MASTER_PASSWORD = "myComplicatePassword".toCharArray();

    private static PrivateKey privateKey;

    private static PublicKey publicKey;

    @Test
    public void testEncryptAndDecryptRsaPrivate() {
        testPki(EncryptionUtilTest.privateKey, EncryptionUtilTest.publicKey, false);
    }

    @Test
    public void testEncryptAndDecryptRsaPublic() {
        testPki(EncryptionUtilTest.publicKey, EncryptionUtilTest.privateKey, true);
    }

    @Test
    public void testEncryptWrongKey() throws NoSuchAlgorithmException {
        PrivateKey privateKey = generateDsaPrivateKey();
        try {
            EncryptionUtil.encryptRsa(privateKey, EncryptionUtilTest.TEST_SECRET);
            Assertions.fail("We have fed DSA private key to RSA decryption. This should have failed");
        } catch (ConfigEncryptionException e) {
            Throwable cause = e.getCause();
            // our message
            Assertions.assertEquals("Failed to encrypt using RSA key", e.getMessage());
            Assertions.assertSame(InvalidKeyException.class, cause.getClass());
        }
    }

    @Test
    public void testEncryptedRsaPrivate() {
        Assertions.assertThrows(ConfigEncryptionException.class, () -> testPki(EncryptionUtilTest.privateKey, EncryptionUtilTest.privateKey, false));
    }

    @Test
    public void testEncryptedRsaPublic() {
        Assertions.assertThrows(ConfigEncryptionException.class, () -> testPki(EncryptionUtilTest.publicKey, EncryptionUtilTest.publicKey, true));
    }

    @Test
    public void testEncryptAndDecryptAes() {
        String encryptedBase64 = EncryptionUtil.encryptAes(EncryptionUtilTest.MASTER_PASSWORD, EncryptionUtilTest.TEST_SECRET);
        String decrypted = EncryptionUtil.decryptAes(EncryptionUtilTest.MASTER_PASSWORD, encryptedBase64);
        Assertions.assertEquals(EncryptionUtilTest.TEST_SECRET, decrypted);
        String encryptedAgain = EncryptionUtil.encryptAes(EncryptionUtilTest.MASTER_PASSWORD, EncryptionUtilTest.TEST_SECRET);
        Assertions.assertNotEquals(encryptedBase64, encryptedAgain);
        decrypted = EncryptionUtil.decryptAes(EncryptionUtilTest.MASTER_PASSWORD, encryptedAgain);
        Assertions.assertEquals(EncryptionUtilTest.TEST_SECRET, decrypted);
    }

    @Test
    public void testEncryptedAes() {
        String encryptedBase64 = EncryptionUtil.encryptAes(EncryptionUtilTest.MASTER_PASSWORD, EncryptionUtilTest.TEST_SECRET);
        String encryptedString = new String(Base64.getDecoder().decode(encryptedBase64), StandardCharsets.UTF_8);
        // must not be just base64 encoded
        Assertions.assertNotEquals(EncryptionUtilTest.TEST_SECRET, encryptedString);
        try {
            String decrypted = EncryptionUtil.decryptAes("anotherPassword".toCharArray(), encryptedBase64);
            MatcherAssert.assertThat(decrypted, CoreMatchers.is(CoreMatchers.not(EncryptionUtilTest.TEST_SECRET)));
        } catch (Exception e) {
            // this is OK
        }
    }
}

