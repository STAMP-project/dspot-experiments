package org.apereo.cas.util;


import java.nio.charset.StandardCharsets;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link EncodingUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class EncodingUtilsTests {
    @Test
    public void verifyAesKeyForJwtSigning() {
        val secret = EncodingUtils.generateJsonWebKey(512);
        val key = new org.jose4j.keys.AesKey(secret.getBytes(StandardCharsets.UTF_8));
        val value = "ThisValue";
        val signed = EncodingUtils.signJwsHMACSha512(key, value.getBytes(StandardCharsets.UTF_8));
        val jwt = EncodingUtils.verifyJwsSignature(key, signed);
        val result = new String(jwt, StandardCharsets.UTF_8);
        Assertions.assertTrue(result.equals(value));
    }

    @Test
    public void verifyRsaKeyForJwtSigning() {
        val value = "ThisValue";
        val signed = EncodingUtils.signJwsRSASha512(EncodingUtilsTests.getPrivateKey(), value.getBytes(StandardCharsets.UTF_8));
        val jwt = EncodingUtils.verifyJwsSignature(EncodingUtilsTests.getPublicKey(), signed);
        val result = new String(jwt, StandardCharsets.UTF_8);
        Assertions.assertTrue(result.equals(value));
    }

    @Test
    public void verifyAesKeyForJwtEncryption() {
        val secret = EncodingUtils.generateJsonWebKey(256);
        val key = EncodingUtils.generateJsonWebKey(secret);
        val value = "ThisValue";
        val found = EncodingUtils.encryptValueAsJwtDirectAes128Sha256(key, value);
        val jwt = EncodingUtils.decryptJwtValue(key, found);
        Assertions.assertTrue(jwt.equals(value));
    }

    @Test
    public void verifyRsaKeyForJwtEncryption() {
        val value = "ThisValue";
        val found = EncodingUtils.encryptValueAsJwtRsaOeap256Aes256Sha512(EncodingUtilsTests.getPublicKey(), value);
        val jwt = EncodingUtils.decryptJwtValue(EncodingUtilsTests.getPrivateKey(), found);
        Assertions.assertTrue(jwt.equals(value));
    }
}

