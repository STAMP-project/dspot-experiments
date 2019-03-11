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
package io.helidon.security.jwt;


import io.helidon.common.Errors;
import io.helidon.security.jwt.jwk.JwkKeys;
import java.util.logging.Logger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link SignedJwt}.
 */
public class SignedJwtTest {
    private static final Logger LOGGER = Logger.getLogger(SignedJwtTest.class.getName());

    private static final String AUTH_0_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IlF6QkNNRE0xUVRJMk1qUkZNVEZETkRCRFJUWXdSa1U0UkRkRU16VTVSVGN3TkRSQk5qaENOUSJ9.eyJpc3MiOiJodHRwczovL2xhbmdvc2guZXUuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDU5NGEzNzJkNDUxN2FmMTA0ZjFiZGUzMCIsImF1ZCI6InliZlBpcnBwUTZaT094U0dLa2pnTWUxa2ZGTGZkbXlQIiwiZXhwIjoxNDk4MDgzOTI2LCJpYXQiOjE0OTgwNDc5MjZ9.nyMs5VfDV7Njd6hnQmbrSp_xVbIdvdP3ChzEtdffH2FWMqeW34gZT7dKJfgirdcBfXD4cDNDF2yjKZTe9-yCLWCFYtrfpvS_nlbt1hVM5ZR2HsGFSKdws0gOTsKCOTnD0SmfiQHCP-tzu87qWcVIwQcm-7AuLSfQ3WPxHAGPcQDOZiJBfcpBN4OGPKF0qq7PdNzBDDHmzpt2TbSsHmnSW-QbWZ1QHr52jsRCl1O_UTYHo2HE3ShE3WWBgYcJdJhgXNkhvJJh95oqmq_bfH5Saw-REmg-roU1bAh_yzFkmVSnhKmzHff432glRxcgDgF87kqJNodMD6UN6wRVt9vAPg";

    private static JwkKeys auth0Keys;

    private static JwkKeys customKeys;

    @Test
    public void testParsing() {
        SignedJwt signedJwt = SignedJwt.parseToken(SignedJwtTest.AUTH_0_TOKEN);
        MatcherAssert.assertThat(signedJwt.headerJson(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signedJwt.payloadJson(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signedJwt.getSignature(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signedJwt.getSignedBytes(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signedJwt.tokenContent(), CoreMatchers.is(SignedJwtTest.AUTH_0_TOKEN));
    }

    @Test
    public void testVerify() {
        SignedJwt signedJwt = SignedJwt.parseToken(SignedJwtTest.AUTH_0_TOKEN);
        Errors errors = signedJwt.verifySignature(SignedJwtTest.auth0Keys);
        MatcherAssert.assertThat(errors, CoreMatchers.notNullValue());
        errors.checkValid();
    }

    @Test
    public void testToJwt() {
        SignedJwt signedJwt = SignedJwt.parseToken(SignedJwtTest.AUTH_0_TOKEN);
        Jwt jwt = signedJwt.getJwt();
        MatcherAssert.assertThat(jwt, CoreMatchers.notNullValue());
        // todo make sure everything is valid except for exp time
    }

    @Test
    public void testSignature() {
        Jwt jwt = Jwt.builder().algorithm("RS256").keyId("cc34c0a0-bd5a-4a3c-a50d-a2a7db7643df").issuer("unit-test").build();
        SignedJwt signed = SignedJwt.sign(jwt, SignedJwtTest.customKeys);
        MatcherAssert.assertThat(signed, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signed.getSignature(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signed.getSignature(), CoreMatchers.not(new byte[0]));
        Errors errors = signed.verifySignature(SignedJwtTest.customKeys);
        MatcherAssert.assertThat(errors, CoreMatchers.notNullValue());
        errors.checkValid();
    }

    @Test
    public void testSignatureNone() {
        Jwt jwt = Jwt.builder().algorithm("none").issuer("unit-test").build();
        SignedJwt signed = SignedJwt.sign(jwt, SignedJwtTest.customKeys);
        MatcherAssert.assertThat(signed, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signed.getSignature(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signed.getSignature(), CoreMatchers.is(new byte[0]));
        Errors errors = signed.verifySignature(SignedJwtTest.customKeys);
        MatcherAssert.assertThat(errors, CoreMatchers.notNullValue());
        errors.log(SignedJwtTest.LOGGER);
        errors.checkValid();
    }

    @Test
    public void testSingatureNoAlgNoKid() {
        Jwt jwt = Jwt.builder().build();
        SignedJwt signed = SignedJwt.sign(jwt, SignedJwtTest.customKeys);
        MatcherAssert.assertThat(signed, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signed.getSignature(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(signed.getSignature(), CoreMatchers.is(new byte[0]));
        Errors errors = signed.verifySignature(SignedJwtTest.customKeys);
        MatcherAssert.assertThat(errors, CoreMatchers.notNullValue());
        errors.log(SignedJwtTest.LOGGER);
        errors.checkValid();
    }
}

