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
package io.helidon.security.providers.httpauth;


import HttpBasicAuthProvider.HEADER_AUTHENTICATION;
import HttpBasicAuthProvider.HEADER_AUTHENTICATION_REQUIRED;
import HttpDigest.Qop.AUTH;
import HttpDigest.Qop.NONE;
import SecurityResponse.SecurityStatus.FAILURE;
import SecurityResponse.SecurityStatus.SUCCESS;
import io.helidon.security.AuthenticationResponse;
import io.helidon.security.Security;
import io.helidon.security.SecurityContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link HttpBasicAuthProvider} and {@link HttpDigestAuthProvider}.
 */
public class HttpAuthProviderBuilderTest {
    private static final String QUOTE = "\"";

    private static Security security;

    private static volatile int counter = 0;

    private final Random random = new Random();

    private SecurityContext context;

    @Test
    public void basicTestFail() {
        AuthenticationResponse response = context.atnClientBuilder().buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        String authHeader = response.responseHeaders().get(HEADER_AUTHENTICATION_REQUIRED).get(0);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.is("basic realm=\"mic\""));
    }

    @Test
    public void basicTestJack() {
        setHeader(context, HEADER_AUTHENTICATION, buildBasic("jack", "jackIsGreat"));
        AuthenticationResponse response = context.authenticate();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(200));
        MatcherAssert.assertThat(context.user().map(( sub) -> sub.principal().getName()).orElse(null), CoreMatchers.is("jack"));
        MatcherAssert.assertThat(context.isUserInRole("admin"), CoreMatchers.is(true));
        MatcherAssert.assertThat(context.isUserInRole("user"), CoreMatchers.is(true));
    }

    @Test
    public void basicTestInvalidUser() {
        setHeader(context, HEADER_AUTHENTICATION, buildBasic("wrong", "user"));
        AuthenticationResponse response = context.authenticate();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid username or password"));
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        String authHeader = response.responseHeaders().get(HEADER_AUTHENTICATION_REQUIRED).get(0);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.is("basic realm=\"mic\""));
        setHeader(context, HEADER_AUTHENTICATION, buildBasic("jack", "invalid_passworrd"));
        response = context.authenticate();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid username or password"));
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void sendInvalidTypeTest() {
        setHeader(context, HEADER_AUTHENTICATION, "bearer token=\"adfasfaf\"");
        AuthenticationResponse response = context.authenticate();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void sendInvalidBasicTest() {
        setHeader(context, HEADER_AUTHENTICATION, "basic wrong_header_value");
        AuthenticationResponse response = context.authenticate();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        // not base64 encoded and invalid
        setHeader(context, HEADER_AUTHENTICATION, ("basic " + (Base64.getEncoder().encodeToString("Hello".getBytes()))));
        response = context.authenticate();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void sendDigestNotBasicTest() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "jackIsGreat"));
        AuthenticationResponse response = context.authenticate();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void basicTestJill() {
        setHeader(context, HEADER_AUTHENTICATION, buildBasic("jill", "password"));
        AuthenticationResponse response = context.authenticate();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(200));
        MatcherAssert.assertThat(getUsername(context), CoreMatchers.is("jill"));
        MatcherAssert.assertThat(context.isUserInRole("admin"), CoreMatchers.is(false));
        MatcherAssert.assertThat(context.isUserInRole("user"), CoreMatchers.is(true));
    }

    @Test
    public void digestTest401() {
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        String authHeader = response.responseHeaders().get(HEADER_AUTHENTICATION_REQUIRED).get(0);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.startsWith("digest realm=\"mic\""));
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.containsString("qop="));
    }

    @Test
    public void digestOldTest401() {
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest_old").buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        String authHeader = response.responseHeaders().get(HEADER_AUTHENTICATION_REQUIRED).get(0);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.startsWith("digest realm=\"mic\""));
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.not(CoreMatchers.containsString("qop=")));
    }

    @Test
    public void digestTestJack() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "jackIsGreat"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse("No description"), response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(200));
        MatcherAssert.assertThat(getUsername(context), CoreMatchers.is("jack"));
        MatcherAssert.assertThat(context.isUserInRole("admin"), CoreMatchers.is(true));
        MatcherAssert.assertThat(context.isUserInRole("user"), CoreMatchers.is(true));
    }

    @Test
    public void digestTestInvalidUser() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "wrong", "user"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid username or password"));
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        String authHeader = response.responseHeaders().get(HEADER_AUTHENTICATION_REQUIRED).get(0);
        MatcherAssert.assertThat(authHeader, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authHeader.toLowerCase(), CoreMatchers.startsWith("digest realm=\"mic\""));
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "wrong password"));
        response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid username or password"));
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void sendBasicNotDigestTest() {
        setHeader(context, HEADER_AUTHENTICATION, buildBasic("jack", "jackIsGreat"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void sendInvalidDigestTest() {
        setHeader(context, HEADER_AUTHENTICATION, "digest wrong_header_value");
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void digestTestJill() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jill", "password"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse("No description"), response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(200));
        MatcherAssert.assertThat(getUsername(context), CoreMatchers.is("jill"));
        MatcherAssert.assertThat(context.isUserInRole("admin"), CoreMatchers.is(false));
        MatcherAssert.assertThat(context.isUserInRole("user"), CoreMatchers.is(true));
    }

    @Test
    public void digestOldTestJack() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(NONE, "jack", "jackIsGreat"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest_old").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse("No description"), response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(200));
        MatcherAssert.assertThat(getUsername(context), CoreMatchers.is("jack"));
        MatcherAssert.assertThat(context.isUserInRole("admin"), CoreMatchers.is(true));
        MatcherAssert.assertThat(context.isUserInRole("user"), CoreMatchers.is(true));
    }

    @Test
    public void digestOldTestJill() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(NONE, "jill", "password"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest_old").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse("No description"), response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(200));
        MatcherAssert.assertThat(getUsername(context), CoreMatchers.is("jill"));
        MatcherAssert.assertThat(context.isUserInRole("admin"), CoreMatchers.is(false));
        MatcherAssert.assertThat(context.isUserInRole("user"), CoreMatchers.is(true));
    }

    @Test
    public void digestTestNonceTimeout() {
        Instant in = Instant.now().minus(100, ChronoUnit.DAYS);
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "jackIsGreat", HttpDigestAuthProvider.nonce(in.toEpochMilli(), random, "pwd".toCharArray()), "mic"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Nonce timeout"));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void digestTestNonceNotB64() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "jackIsGreat", "Not a base64 encoded $tring", "mic"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Nonce must be base64 encoded"));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void digestTestNonceTooShort() {
        setHeader(context, HEADER_AUTHENTICATION, // must be base64 encoded string of less than 17 bytes
        buildDigest(AUTH, "jack", "jackIsGreat", "wrongNonce", "mic"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid nonce length"));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void digestTestNonceNotEncrypted() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "jackIsGreat", Base64.getEncoder().encodeToString("4444444444444444444444444444444444444444444444".getBytes()), "mic"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid nonce value"));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }

    @Test
    public void digestTestWrongRealm() {
        setHeader(context, HEADER_AUTHENTICATION, buildDigest(AUTH, "jack", "jackIsGreat", HttpDigestAuthProvider.nonce(System.currentTimeMillis(), random, "pwd".toCharArray()), "wrongRealm"));
        AuthenticationResponse response = context.atnClientBuilder().explicitProvider("digest").buildAndGet();
        MatcherAssert.assertThat(response.description().orElse(""), CoreMatchers.is("Invalid realm"));
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
    }
}

