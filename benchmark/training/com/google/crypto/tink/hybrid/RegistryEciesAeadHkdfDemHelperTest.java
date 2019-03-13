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
import SignatureKeyTemplates.ECDSA_P256;
import com.google.crypto.tink.Aead;
import com.google.crypto.tink.TestUtil;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.signature.SignatureKeyTemplates;
import com.google.crypto.tink.subtle.Random;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for RegistryEciesAeadHkdfDemHelper.
 */
@RunWith(JUnit4.class)
public class RegistryEciesAeadHkdfDemHelperTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private KeyTemplate[] keyTemplates;

    @Test
    public void testConstructorWith128BitCiphers() throws Exception {
        RegistryEciesAeadHkdfDemHelper helper;
        // Supported templates.
        helper = new RegistryEciesAeadHkdfDemHelper(AeadKeyTemplates.AES128_GCM);
        Assert.assertEquals(16, helper.getSymmetricKeySizeInBytes());
        helper = new RegistryEciesAeadHkdfDemHelper(AeadKeyTemplates.AES128_CTR_HMAC_SHA256);
        Assert.assertEquals(48, helper.getSymmetricKeySizeInBytes());
    }

    @Test
    public void testConstructorWith256BitCiphers() throws Exception {
        if ((Cipher.getMaxAllowedKeyLength("AES")) < 256) {
            System.out.println(("Unlimited Strength Jurisdiction Policy Files are required" + " but not installed. Skip tests with keys larger than 128 bits."));
            return;
        }
        // Supported templates.
        RegistryEciesAeadHkdfDemHelper helper = new RegistryEciesAeadHkdfDemHelper(AeadKeyTemplates.AES256_GCM);
        Assert.assertEquals(32, helper.getSymmetricKeySizeInBytes());
        helper = new RegistryEciesAeadHkdfDemHelper(AeadKeyTemplates.AES256_CTR_HMAC_SHA256);
        Assert.assertEquals(64, helper.getSymmetricKeySizeInBytes());
    }

    @Test
    public void testConstructorWithUnsupportedTemplates() throws Exception {
        RegistryEciesAeadHkdfDemHelper unusedHelper;
        // Unsupported templates.
        int templateCount = 4;
        KeyTemplate[] templates = new KeyTemplate[templateCount];
        templates[0] = AeadKeyTemplates.AES128_EAX;
        templates[1] = AeadKeyTemplates.AES256_EAX;
        templates[2] = AeadKeyTemplates.CHACHA20_POLY1305;
        templates[3] = SignatureKeyTemplates.ECDSA_P256;
        int count = 0;
        for (KeyTemplate template : templates) {
            try {
                unusedHelper = new RegistryEciesAeadHkdfDemHelper(template);
                Assert.fail(("DEM type not supported, should have thrown exception:\n" + (template.toString())));
            } catch (GeneralSecurityException e) {
                // Expected.
                TestUtil.assertExceptionContains(e, "unsupported AEAD DEM key type");
                TestUtil.assertExceptionContains(e, template.getTypeUrl());
            }
            count++;
        }
        Assert.assertEquals(templateCount, count);
        // An inconsistent template.
        KeyTemplate template = KeyTemplate.newBuilder().setTypeUrl(AES128_CTR_HMAC_SHA256.getTypeUrl()).setValue(ECDSA_P256.getValue()).build();
        try {
            unusedHelper = new RegistryEciesAeadHkdfDemHelper(template);
            Assert.fail(("Inconsistent template, should have thrown exception:\n" + (template.toString())));
        } catch (GeneralSecurityException e) {
            // Expected.
        }
    }

    @Test
    public void testGetAead() throws Exception {
        byte[] plaintext = "some plaintext string".getBytes(RegistryEciesAeadHkdfDemHelperTest.UTF_8);
        byte[] associatedData = "some associated data".getBytes(RegistryEciesAeadHkdfDemHelperTest.UTF_8);
        int count = 0;
        for (KeyTemplate template : keyTemplates) {
            RegistryEciesAeadHkdfDemHelper helper = new RegistryEciesAeadHkdfDemHelper(template);
            byte[] symmetricKey = Random.randBytes(helper.getSymmetricKeySizeInBytes());
            Aead aead = helper.getAead(symmetricKey);
            byte[] ciphertext = aead.encrypt(plaintext, associatedData);
            byte[] decrypted = aead.decrypt(ciphertext, associatedData);
            Assert.assertArrayEquals(plaintext, decrypted);
            // Try using a symmetric key that is too short.
            symmetricKey = Random.randBytes(((helper.getSymmetricKeySizeInBytes()) - 1));
            try {
                aead = helper.getAead(symmetricKey);
                Assert.fail(("Symmetric key too short, should have thrown exception:\n" + (template.toString())));
            } catch (GeneralSecurityException e) {
                // Expected.
                TestUtil.assertExceptionContains(e, "incorrect length");
            }
            // Try using a symmetric key that is too long.
            symmetricKey = Random.randBytes(((helper.getSymmetricKeySizeInBytes()) + 1));
            try {
                aead = helper.getAead(symmetricKey);
                Assert.fail(("Symmetric key too long, should have thrown exception:\n" + (template.toString())));
            } catch (GeneralSecurityException e) {
                // Expected.
                TestUtil.assertExceptionContains(e, "incorrect length");
            }
            count++;
        }
        Assert.assertEquals(keyTemplates.length, count);
    }
}

