/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.jwt.jwk;


import Jwk.EMPTY_BYTES;
import Jwk.OPERATION_DECRYPT;
import Jwk.OPERATION_ENCRYPT;
import Jwk.OPERATION_SIGN;
import Jwk.OPERATION_VERIFY;
import Jwk.USE_ENCRYPTION;
import Jwk.USE_SIGNATURE;
import JwkEC.ALG_ES256;
import JwkEC.ALG_ES384;
import JwkEC.ALG_ES512;
import JwkOctet.ALG_HS384;
import JwkOctet.ALG_HS512;
import JwkRSA.ALG_RS256;
import JwkRSA.ALG_RS384;
import JwkRSA.ALG_RS512;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.OptionalHelper;
import io.helidon.security.jwt.JwtException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link JwkKeys}.
 */
public class JwkKeysTest {
    private static JwkKeys customKeys;

    private static JwkKeys googleKeys;

    private static JwkKeys auth0Keys;

    @Test
    public void testGoogleJwkDocument() {
        Optional<Jwk> key = JwkKeysTest.googleKeys.forKeyId("eb29843dd7334cf989e1db6a2b0c6e07a10a9cd3");
        MatcherAssert.assertThat(key.isPresent(), CoreMatchers.is(true));
        Jwk jwkKey = key.get();
    }

    @Test
    public void testUseSig() {
        Jwk jwk = JwkKeysTest.customKeys.forKeyId("HS_512").get();
        MatcherAssert.assertThat(jwk.supports(USE_SIGNATURE, OPERATION_SIGN), CoreMatchers.is(true));
        MatcherAssert.assertThat(jwk.supports(USE_SIGNATURE, OPERATION_VERIFY), CoreMatchers.is(true));
        MatcherAssert.assertThat(jwk.supports(USE_ENCRYPTION, OPERATION_ENCRYPT), CoreMatchers.is(false));
        MatcherAssert.assertThat(jwk.supports(USE_ENCRYPTION, OPERATION_DECRYPT), CoreMatchers.is(false));
    }

    @Test
    public void testKeyOpsSig() {
        Jwk jwk = JwkKeysTest.customKeys.forKeyId("hmac-secret-001").get();
        MatcherAssert.assertThat(jwk.supports(USE_SIGNATURE, OPERATION_SIGN), CoreMatchers.is(true));
        MatcherAssert.assertThat(jwk.supports(USE_SIGNATURE, OPERATION_VERIFY), CoreMatchers.is(true));
        MatcherAssert.assertThat(jwk.supports(USE_ENCRYPTION, OPERATION_DECRYPT), CoreMatchers.is(false));
        MatcherAssert.assertThat(jwk.supports(USE_ENCRYPTION, OPERATION_ENCRYPT), CoreMatchers.is(false));
    }

    @Test
    public void testUseEnc() {
        Jwk jwk = JwkKeysTest.customKeys.forKeyId("1").get();
        MatcherAssert.assertThat(jwk.supports(USE_SIGNATURE, OPERATION_SIGN), CoreMatchers.is(false));
        MatcherAssert.assertThat(jwk.supports(USE_SIGNATURE, OPERATION_VERIFY), CoreMatchers.is(false));
        MatcherAssert.assertThat(jwk.supports(USE_ENCRYPTION, OPERATION_ENCRYPT), CoreMatchers.is(true));
        MatcherAssert.assertThat(jwk.supports(USE_ENCRYPTION, OPERATION_DECRYPT), CoreMatchers.is(true));
    }

    @Test
    public void testAuth0JwkDocument() {
        String keyId = "QzBCMDM1QTI2MjRFMTFDNDBDRTYwRkU4RDdEMzU5RTcwNDRBNjhCNQ";
        OptionalHelper.from(JwkKeysTest.auth0Keys.forKeyId(keyId)).ifPresentOrElse(( key) -> {
            assertThat(key.algorithm(), is(JwkRSA.ALG_RS256));
            assertThat(key.keyType(), is(Jwk.KEY_TYPE_RSA));
            assertThat(key.usage(), is(Optional.of(Jwk.USE_SIGNATURE)));
            assertThat(key.keyId(), is(keyId));
            assertThat(key, instanceOf(.class));
            assertThat(key, instanceOf(.class));
            JwkPki pki = ((JwkPki) (key));
            assertThat(pki.certificateChain(), not(Optional.empty()));
            assertThat(pki.privateKey(), is(Optional.empty()));
            assertThat(pki.publicKey(), not(Optional.empty()));
            assertThat(pki.sha1Thumbprint(), not(Optional.empty()));
            assertThat(Base64.getUrlEncoder().encodeToString(pki.sha1Thumbprint().get()), is("QzBCMDM1QTI2MjRFMTFDNDBDRTYwRkU4RDdEMzU5RTcwNDRBNjhCNQ=="));
            assertThat(pki.sha256Thumbprint(), is(Optional.empty()));
        }, () -> fail((("Key with id \"" + keyId) + "\" should be presenit in auth0-jwk.json file")));
    }

    @Test
    public void testSignFailsForEncUse() {
        Jwk jwk = JwkKeysTest.customKeys.forKeyId("1").get();
        try {
            jwk.sign("someBytes".getBytes());
            Assertions.fail("Signing should have failed as key is for encryption only");
        } catch (JwtException e) {
            // expected
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.endsWith("does not support signing of requests"));
        }
    }

    @Test
    public void testNone() {
        Jwk jwk = JwkKeysTest.customKeys.forKeyId("none").get();
        boolean signed = jwk.verifySignature("test".getBytes(), EMPTY_BYTES);
        MatcherAssert.assertThat(signed, CoreMatchers.is(true));
    }

    @Test
    public void testRsa() {
        testRsa("cc34c0a0-bd5a-4a3c-a50d-a2a7db7643df", ALG_RS256);
        testRsa("RS_384", ALG_RS384);
        testRsa("RS_512", ALG_RS512);
    }

    @Test
    public void testEc() {
        testEc("ec-secret-001", ALG_ES256);
        testEc("ES_384", ALG_ES384);
        testEc("ES_512", ALG_ES512);
    }

    @Test
    public void testOct() {
        testOct("HS_384", ALG_HS384);
        testOct("HS_512", ALG_HS512);
    }

    @Test
    public void testCustomOct() {
        String keyId = "hmac-secret-001";
        OptionalHelper.from(JwkKeysTest.customKeys.forKeyId(keyId)).ifPresentOrElse(( key) -> {
            assertThat(key.algorithm(), is(JwkOctet.ALG_HS256));
            assertThat(key.keyType(), is(Jwk.KEY_TYPE_OCT));
            assertThat(key.usage(), is(Optional.empty()));
            assertThat(key.keyId(), is(keyId));
            assertThat(key.operations(), is(Optional.of(CollectionsHelper.listOf(Jwk.OPERATION_SIGN, Jwk.OPERATION_VERIFY))));
            assertThat(key, instanceOf(.class));
            assertThat(key, not(instanceOf(.class)));
            JwkOctet oct = ((JwkOctet) (key));
            assertThat(Base64.getUrlEncoder().encodeToString(oct.getKeyBytes()), is("FdFYFzERwC2uCBB46pZQi4GG85LujR8obt-KWRBICVQ="));
            // now test sign/verify
            byte[] bytes = "someTextToSign 3232".getBytes(StandardCharsets.UTF_8);
            byte[] sig = key.sign(bytes);
            assertThat(sig, notNullValue());
            assertThat(sig.length, not(0));
            assertThat(key.verifySignature(bytes, sig), is(true));
        }, () -> fail(((("Octet Key with kid \"" + keyId) + "\" should be present in ") + "jwk_data.json file")));
    }

    @Test
    public void testECUsingBuilder() {
        // hack this a bit, so I do not have to create a key pair in java
        String fileKeyid = "ec-secret-001";
        OptionalHelper.from(JwkKeysTest.customKeys.forKeyId(fileKeyid)).ifPresentOrElse(( keyFromFile) -> {
            String keyId = "some_key_id";
            // the key must be an EC key
            JwkEC ec = ((JwkEC) (keyFromFile));
            JwkEC ecKey = JwkEC.builder().publicKey(((ECPublicKey) (ec.publicKey()))).privateKey(((ECPrivateKey) (ec.privateKey().get()))).algorithm(JwkEC.ALG_ES256).keyId(keyId).usage(Jwk.USE_SIGNATURE).build();
            JwkKeys keys = JwkKeys.builder().addKey(ecKey).build();
            OptionalHelper.from(keys.forKeyId(keyId)).ifPresentOrElse(( key) -> {
                assertThat(key.algorithm(), is(JwkEC.ALG_ES256));
                assertThat(key.keyType(), is(Jwk.KEY_TYPE_EC));
                assertThat(key.usage(), is(Optional.of(Jwk.USE_SIGNATURE)));
                assertThat(key.keyId(), is(keyId));
                assertThat(key.operations(), is(Optional.empty()));
                assertThat(key, instanceOf(.class));
                assertThat(key, instanceOf(.class));
                JwkPki pki = ((JwkPki) (key));
                assertThat(pki.certificateChain(), is(Optional.empty()));
                assertThat(pki.privateKey(), not(Optional.empty()));
                assertThat(pki.publicKey(), not(Optional.empty()));
                assertThat(pki.sha1Thumbprint(), is(Optional.empty()));
                assertThat(pki.sha256Thumbprint(), is(Optional.empty()));
                // now test sign/verify
                byte[] bytes = "someTextToSign 3232".getBytes(StandardCharsets.UTF_8);
                byte[] sig = key.sign(bytes);
                assertThat(sig, notNullValue());
                assertThat(sig.length, not(0));
                assertThat(key.verifySignature(bytes, sig), is(true));
            }, () -> fail("The key should be present in built keys"));
        }, () -> fail((("Key \"" + fileKeyid) + "\" should be present in jwk_data.json")));
    }
}

