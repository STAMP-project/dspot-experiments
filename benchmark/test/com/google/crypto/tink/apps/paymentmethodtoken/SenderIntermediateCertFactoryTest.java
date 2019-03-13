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
package com.google.crypto.tink.apps.paymentmethodtoken;


import PaymentMethodTokenConstants.PROTOCOL_VERSION_EC_V1;
import PaymentMethodTokenConstants.PROTOCOL_VERSION_EC_V2;
import PaymentMethodTokenConstants.PROTOCOL_VERSION_EC_V2_SIGNING_ONLY;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SenderIntermediateCertFactory}.
 */
@RunWith(JUnit4.class)
public class SenderIntermediateCertFactoryTest {
    /**
     * Sample Google private signing key for the ECv2 protocolVersion.
     *
     * <p>Base64 version of the private key encoded in PKCS8 format.
     */
    private static final String GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64 = "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgKvEdSS8f0mjTCNKev" + ("aKXIzfNC5b4A104gJWI9TsLIMqhRANCAAT/X7ccFVJt2/6Ps1oCt2AzKhIAz" + "jfJHJ3Op2DVPGh1LMD3oOPgxzUSIqujHG6dq9Ui93Eacl4VWJPMW/MVHHIL");

    /**
     * Sample Google intermediate public signing key for the ECv2 protocolVersion.
     *
     * <p>Base64 version of the public key encoded in ASN.1 type SubjectPublicKeyInfo defined in the
     * X.509 standard.
     */
    private static final String GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE/1+3HBVSbdv+j7NaArdgMyoSAM43yR" + "ydzqdg1TxodSzA96Dj4Mc1EiKroxxunavVIvdxGnJeFViTzFvzFRxyCw==";

    @Test
    public void shouldProduceSenderIntermediateCertJson() throws Exception {
        String encoded = new SenderIntermediateCertFactory.Builder().protocolVersion(PROTOCOL_VERSION_EC_V2).senderIntermediateSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64).addSenderSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64).expiration(123456).build().create();
        JSONObject decodedSignedIntermediateSigningKey = new JSONObject(encoded);
        Assert.assertTrue(((decodedSignedIntermediateSigningKey.get("signedKey")) instanceof String));
        Assert.assertTrue(((decodedSignedIntermediateSigningKey.get("signatures")) instanceof JSONArray));
        Assert.assertEquals(2, decodedSignedIntermediateSigningKey.length());
        JSONObject signedKey = new JSONObject(decodedSignedIntermediateSigningKey.getString("signedKey"));
        Assert.assertTrue(((signedKey.get("keyValue")) instanceof String));
        Assert.assertTrue(((signedKey.get("keyExpiration")) instanceof String));
        Assert.assertEquals(2, signedKey.length());
        Assert.assertEquals(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64, signedKey.getString("keyValue"));
        Assert.assertEquals("123456", signedKey.getString("keyExpiration"));
        Assert.assertEquals(1, decodedSignedIntermediateSigningKey.getJSONArray("signatures").length());
        Assert.assertNotEquals(0, decodedSignedIntermediateSigningKey.getJSONArray("signatures").getString(0).length());
    }

    @Test
    public void shouldThrowIfExpirationNotSet() throws Exception {
        try {
            // no expiration
            new SenderIntermediateCertFactory.Builder().protocolVersion(PROTOCOL_VERSION_EC_V2).senderIntermediateSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64).addSenderSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64).build();
            Assert.fail("Should have thrown!");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("must set expiration using Builder.expiration", expected.getMessage());
        }
    }

    @Test
    public void shouldThrowIfExpirationIsNegative() throws Exception {
        try {
            new SenderIntermediateCertFactory.Builder().protocolVersion(PROTOCOL_VERSION_EC_V2).senderIntermediateSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64).addSenderSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64).expiration((-1)).build();
            Assert.fail("Should have thrown!");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("invalid negative expiration", expected.getMessage());
        }
    }

    @Test
    public void shouldThrowIfNoSenderSigningKeyAdded() throws Exception {
        try {
            // no call to addSenderSigningKey
            new SenderIntermediateCertFactory.Builder().protocolVersion(PROTOCOL_VERSION_EC_V2).senderIntermediateSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64).expiration(Instant.now().plus(Duration.standardDays(1)).getMillis()).build();
            Assert.fail("Should have thrown!");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("must add at least one sender's signing key using Builder.addSenderSigningKey", expected.getMessage());
        }
    }

    @Test
    public void shouldSupportECV2SigningOnly() throws Exception {
        new SenderIntermediateCertFactory.Builder().protocolVersion(PROTOCOL_VERSION_EC_V2_SIGNING_ONLY).senderIntermediateSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_INTERMEDIATE_PUBLIC_KEY_X509_BASE64).addSenderSigningKey(SenderIntermediateCertFactoryTest.GOOGLE_SIGNING_EC_V2_PRIVATE_KEY_PKCS8_BASE64).expiration(123456).build();
    }

    @Test
    public void shouldThrowIfInvalidProtocolVersionSet() throws Exception {
        try {
            new SenderIntermediateCertFactory.Builder().protocolVersion(PROTOCOL_VERSION_EC_V1).build();
            Assert.fail("Should have thrown!");
        } catch (IllegalArgumentException expected) {
            Assert.assertEquals("invalid version: ECv1", expected.getMessage());
        }
    }
}

