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
package com.google.crypto.tink.apps.webpush;


import WebPushConstants.NIST_P256_CURVE_TYPE;
import com.google.crypto.tink.HybridDecrypt;
import com.google.crypto.tink.HybridEncrypt;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.subtle.EllipticCurves;
import com.google.crypto.tink.subtle.Random;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static WebPushConstants.CIPHERTEXT_OVERHEAD;
import static WebPushConstants.MAX_CIPHERTEXT_SIZE;


/**
 * Unit tests for {@code WebPushHybridDecrypt}.
 */
@RunWith(JUnit4.class)
public class WebPushHybridDecryptTest {
    // Copied from https://tools.ietf.org/html/rfc8291#section-5.
    private static final String PLAINTEXT = "V2hlbiBJIGdyb3cgdXAsIEkgd2FudCB0byBiZSBhIHdhdGVybWVsb24";

    private static final String RECEIVER_PRIVATE_KEY = "q1dXpw3UpT5VOmu_cf_v6ih07Aems3njxI-JWgLcM94";

    private static final String RECEIVER_PUBLIC_KEY = "BCVxsr7N_eNgVRqvHtD0zTZsEc6-VV-JvLexhqUzORcxaOzi6-AYWXvTBHm4bjyPjs7Vd8pZGH6SRpkNtoIAiw4";

    private static final String AUTH_SECRET = "BTBZMqHH6r4Tts7J_aSIgg";

    private static final String CIPHERTEXT = "DGv6ra1nlYgDCS1FRnbzlwAAEABBBP4z9KsN6nGRTbVYI_c7VJSPQTBtkgcy27ml" + ("mlMoZIIgDll6e3vCYLocInmYWAmS6TlzAC8wEqKK6PBru3jl7A_yl95bQpu6cVPT" + "pK4Mqgkf1CXztLVBSt2Ks3oZwbuwXPXLWyouBWLVWGNWQexSgSxsj_Qulcy4a-fN");

    private static final int RECORD_SIZE = 4096;

    @Test
    public void testWithRfc8291TestVector() throws Exception {
        byte[] plaintext = Base64.urlSafeDecode(WebPushHybridDecryptTest.PLAINTEXT);
        byte[] recipientPrivateKey = Base64.urlSafeDecode(WebPushHybridDecryptTest.RECEIVER_PRIVATE_KEY);
        byte[] recipientPublicKey = Base64.urlSafeDecode(WebPushHybridDecryptTest.RECEIVER_PUBLIC_KEY);
        byte[] authSecret = Base64.urlSafeDecode(WebPushHybridDecryptTest.AUTH_SECRET);
        byte[] ciphertext = Base64.urlSafeDecode(WebPushHybridDecryptTest.CIPHERTEXT);
        HybridDecrypt hybridDecrypt = new WebPushHybridDecrypt.Builder().withRecordSize(WebPushHybridDecryptTest.RECORD_SIZE).withAuthSecret(authSecret).withRecipientPublicKey(recipientPublicKey).withRecipientPrivateKey(recipientPrivateKey).build();
        Assert.assertArrayEquals(plaintext, /* contextInfo */
        hybridDecrypt.decrypt(ciphertext, null));
    }

    @Test
    public void testEncryptDecryptWithInvalidRecordSizes() throws Exception {
        KeyPair uaKeyPair = EllipticCurves.generateKeyPair(NIST_P256_CURVE_TYPE);
        ECPrivateKey uaPrivateKey = ((ECPrivateKey) (uaKeyPair.getPrivate()));
        ECPublicKey uaPublicKey = ((ECPublicKey) (uaKeyPair.getPublic()));
        byte[] authSecret = Random.randBytes(16);
        // Test with out of range record sizes.
        {
            try {
                new WebPushHybridDecrypt.Builder().withRecordSize(((MAX_CIPHERTEXT_SIZE) + 1)).withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).withRecipientPrivateKey(uaPrivateKey).build();
                Assert.fail("Expected IllegalArgumentException");
            } catch (IllegalArgumentException ex) {
                // expected.
            }
            try {
                new WebPushHybridDecrypt.Builder().withRecordSize(((CIPHERTEXT_OVERHEAD) - 1)).withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).withRecipientPrivateKey(uaPrivateKey).build();
            } catch (IllegalArgumentException ex) {
                // expected.
            }
        }
        // Test with random mismatched record size.
        {
            for (int i = 0; i < 50; i++) {
                int recordSize = (CIPHERTEXT_OVERHEAD) + (Random.randInt((((MAX_CIPHERTEXT_SIZE) - (CIPHERTEXT_OVERHEAD)) - 1)));
                HybridEncrypt hybridEncrypt = new WebPushHybridEncrypt.Builder().withRecordSize(recordSize).withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).build();
                HybridDecrypt hybridDecrypt = new WebPushHybridDecrypt.Builder().withRecordSize((recordSize + 1)).withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).withRecipientPrivateKey(uaPrivateKey).build();
                byte[] plaintext = Random.randBytes((recordSize - (CIPHERTEXT_OVERHEAD)));
                byte[] ciphertext = /* contextInfo */
                hybridEncrypt.encrypt(plaintext, null);
                try {
                    /* contextInfo */
                    hybridDecrypt.decrypt(ciphertext, null);
                    Assert.fail("Expected GeneralSecurityException");
                } catch (GeneralSecurityException ex) {
                    // expected.
                }
            }
        }
    }

    @Test
    public void testNonNullContextInfo() throws Exception {
        KeyPair uaKeyPair = EllipticCurves.generateKeyPair(NIST_P256_CURVE_TYPE);
        ECPrivateKey uaPrivateKey = ((ECPrivateKey) (uaKeyPair.getPrivate()));
        ECPublicKey uaPublicKey = ((ECPublicKey) (uaKeyPair.getPublic()));
        byte[] authSecret = Random.randBytes(16);
        HybridEncrypt hybridEncrypt = new WebPushHybridEncrypt.Builder().withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).build();
        HybridDecrypt hybridDecrypt = new WebPushHybridDecrypt.Builder().withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).withRecipientPrivateKey(uaPrivateKey).build();
        byte[] plaintext = Random.randBytes(20);
        byte[] ciphertext = /* contextInfo */
        hybridEncrypt.encrypt(plaintext, null);
        try {
            byte[] contextInfo = new byte[0];
            hybridDecrypt.decrypt(ciphertext, contextInfo);
            Assert.fail("Expected GeneralSecurityException");
        } catch (GeneralSecurityException ex) {
            // expected;
        }
    }

    @Test
    public void testModifyCiphertext() throws Exception {
        KeyPair uaKeyPair = EllipticCurves.generateKeyPair(NIST_P256_CURVE_TYPE);
        ECPrivateKey uaPrivateKey = ((ECPrivateKey) (uaKeyPair.getPrivate()));
        ECPublicKey uaPublicKey = ((ECPublicKey) (uaKeyPair.getPublic()));
        byte[] authSecret = Random.randBytes(16);
        HybridEncrypt hybridEncrypt = new WebPushHybridEncrypt.Builder().withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).build();
        HybridDecrypt hybridDecrypt = new WebPushHybridDecrypt.Builder().withAuthSecret(authSecret).withRecipientPublicKey(uaPublicKey).withRecipientPrivateKey(uaPrivateKey).build();
        byte[] plaintext = Random.randBytes(20);
        byte[] ciphertext = /* contextInfo */
        hybridEncrypt.encrypt(plaintext, null);
        // Flipping bits.
        for (int b = 0; b < (ciphertext.length); b++) {
            for (int bit = 0; bit < 8; bit++) {
                byte[] modified = Arrays.copyOf(ciphertext, ciphertext.length);
                modified[b] ^= ((byte) (1 << bit));
                try {
                    byte[] unused = /* contextInfo */
                    hybridDecrypt.decrypt(modified, null);
                    Assert.fail("Decrypting modified ciphertext should fail");
                } catch (GeneralSecurityException ex) {
                    // This is expected.
                }
            }
        }
        // Truncate the message.
        for (int length = 0; length < (ciphertext.length); length++) {
            byte[] modified = Arrays.copyOf(ciphertext, length);
            try {
                byte[] unused = /* contextInfo */
                hybridDecrypt.decrypt(modified, null);
                Assert.fail("Decrypting modified ciphertext should fail");
            } catch (GeneralSecurityException ex) {
                // This is expected.
            }
        }
    }
}

