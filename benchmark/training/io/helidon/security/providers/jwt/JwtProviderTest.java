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
package io.helidon.security.providers.jwt;


import JwkEC.ALG_ES256;
import JwkOctet.ALG_HS256;
import JwkRSA.ALG_RS256;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.OptionalHelper;
import io.helidon.config.Config;
import io.helidon.security.AuthenticationResponse;
import io.helidon.security.EndpointConfig;
import io.helidon.security.OutboundSecurityResponse;
import io.helidon.security.Principal;
import io.helidon.security.ProviderRequest;
import io.helidon.security.SecurityContext;
import io.helidon.security.SecurityEnvironment;
import io.helidon.security.Subject;
import io.helidon.security.jwt.Jwt;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.security.jwt.jwk.JwkKeys;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link JwtProvider}.
 */
public class JwtProviderTest {
    private static JwkKeys verifyKeys;

    @Test
    public void testEcBothWays() {
        String username = "user1";
        String userId = "user1-id";
        String email = "user1@example.org";
        String familyName = "Novak";
        String givenName = "Standa";
        String fullName = "Standa Novak";
        Locale locale = Locale.CANADA_FRENCH;
        Principal principal = Principal.builder().name(username).id(userId).addAttribute("email", email).addAttribute("email_verified", true).addAttribute("family_name", familyName).addAttribute("given_name", givenName).addAttribute("full_name", fullName).addAttribute("locale", locale).build();
        Subject subject = Subject.create(principal);
        JwtProvider provider = JwtProvider.create(Config.create().get("security.providers.0.jwt"));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.of(subject));
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        SecurityEnvironment outboundEnv = SecurityEnvironment.builder().path("/ec").transport("http").targetUri(URI.create("http://localhost:8080/ec")).build();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat(provider.isOutboundSupported(request, outboundEnv, outboundEp), CoreMatchers.is(true));
        OutboundSecurityResponse response = provider.syncOutbound(request, outboundEnv, outboundEp);
        String signedToken = response.requestHeaders().get("Authorization").get(0);
        signedToken = signedToken.substring("bearer ".length());
        // now I want to validate it to prove it was correctly signed
        SignedJwt signedJwt = SignedJwt.parseToken(signedToken);
        signedJwt.verifySignature(JwtProviderTest.verifyKeys).checkValid();
        Jwt jwt = signedJwt.getJwt();
        MatcherAssert.assertThat(jwt.subject(), CoreMatchers.is(Optional.of(userId)));
        MatcherAssert.assertThat(jwt.preferredUsername(), CoreMatchers.is(Optional.of(username)));
        MatcherAssert.assertThat(jwt.email(), CoreMatchers.is(Optional.of(email)));
        MatcherAssert.assertThat(jwt.emailVerified(), CoreMatchers.is(Optional.of(true)));
        MatcherAssert.assertThat(jwt.familyName(), CoreMatchers.is(Optional.of(familyName)));
        MatcherAssert.assertThat(jwt.givenName(), CoreMatchers.is(Optional.of(givenName)));
        MatcherAssert.assertThat(jwt.fullName(), CoreMatchers.is(Optional.of(fullName)));
        MatcherAssert.assertThat(jwt.locale(), CoreMatchers.is(Optional.of(locale)));
        MatcherAssert.assertThat(jwt.audience(), CoreMatchers.is(Optional.of(CollectionsHelper.listOf("audience.application.id"))));
        MatcherAssert.assertThat(jwt.issuer(), CoreMatchers.is(Optional.of("jwt.example.com")));
        MatcherAssert.assertThat(jwt.algorithm(), CoreMatchers.is(Optional.of(ALG_ES256)));
        Instant instant = jwt.issueTime().get();
        boolean compareResult = (Instant.now().minusSeconds(10).compareTo(instant)) < 0;
        MatcherAssert.assertThat("Issue time must not be older than 10 seconds", compareResult, CoreMatchers.is(true));
        Instant expectedNotBefore = instant.minus(5, ChronoUnit.SECONDS);
        MatcherAssert.assertThat(jwt.notBefore(), CoreMatchers.is(Optional.of(expectedNotBefore)));
        Instant expectedExpiry = instant.plus(((60 * 60) * 24), ChronoUnit.SECONDS);
        MatcherAssert.assertThat(jwt.expirationTime(), CoreMatchers.is(Optional.of(expectedExpiry)));
        // now we need to use the same token to invoke authentication
        ProviderRequest atnRequest = Mockito.mock(ProviderRequest.class);
        SecurityEnvironment se = SecurityEnvironment.builder().header("Authorization", ("bearer " + signedToken)).build();
        Mockito.when(atnRequest.env()).thenReturn(se);
        AuthenticationResponse authenticationResponse = provider.syncAuthenticate(atnRequest);
        OptionalHelper.from(authenticationResponse.user().map(Subject::principal)).ifPresentOrElse(( atnPrincipal) -> {
            assertThat(atnPrincipal.id(), is(userId));
            assertThat(atnPrincipal.getName(), is(username));
            assertThat(atnPrincipal.abacAttribute("email"), is(Optional.of(email)));
            assertThat(atnPrincipal.abacAttribute("email_verified"), is(Optional.of(true)));
            assertThat(atnPrincipal.abacAttribute("family_name"), is(Optional.of(familyName)));
            assertThat(atnPrincipal.abacAttribute("given_name"), is(Optional.of(givenName)));
            assertThat(atnPrincipal.abacAttribute("full_name"), is(Optional.of(fullName)));
            assertThat(atnPrincipal.abacAttribute("locale"), is(Optional.of(locale)));
        }, () -> fail("User must be present in response"));
    }

    @Test
    public void testOctBothWays() {
        String userId = "user1-id";
        Principal tp = Principal.create(userId);
        Subject subject = Subject.create(tp);
        JwtProvider provider = JwtProvider.create(Config.create().get("security.providers.0.jwt"));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.of(subject));
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        SecurityEnvironment outboundEnv = SecurityEnvironment.builder().path("/oct").transport("http").targetUri(URI.create("http://localhost:8080/oct")).build();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat(provider.isOutboundSupported(request, outboundEnv, outboundEp), CoreMatchers.is(true));
        OutboundSecurityResponse response = provider.syncOutbound(request, outboundEnv, outboundEp);
        String signedToken = response.requestHeaders().get("Authorization").get(0);
        signedToken = signedToken.substring("bearer ".length());
        // now I want to validate it to prove it was correctly signed
        SignedJwt signedJwt = SignedJwt.parseToken(signedToken);
        signedJwt.verifySignature(JwtProviderTest.verifyKeys).checkValid();
        Jwt jwt = signedJwt.getJwt();
        MatcherAssert.assertThat(jwt.subject(), CoreMatchers.is(Optional.of(userId)));
        MatcherAssert.assertThat(jwt.preferredUsername(), CoreMatchers.is(Optional.of(userId)));
        MatcherAssert.assertThat(jwt.email(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(jwt.emailVerified(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(jwt.familyName(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(jwt.givenName(), CoreMatchers.is(Optional.empty()));
        // stored as "name" attribute on principal, full name is stored as "name" in JWT
        MatcherAssert.assertThat(jwt.fullName(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(jwt.locale(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(jwt.audience(), CoreMatchers.is(Optional.of(CollectionsHelper.listOf("audience.application.id"))));
        MatcherAssert.assertThat(jwt.issuer(), CoreMatchers.is(Optional.of("jwt.example.com")));
        MatcherAssert.assertThat(jwt.algorithm(), CoreMatchers.is(Optional.of(ALG_HS256)));
        Instant instant = jwt.issueTime().get();
        boolean compareResult = (Instant.now().minusSeconds(10).compareTo(instant)) < 0;
        MatcherAssert.assertThat("Issue time must not be older than 10 seconds", compareResult, CoreMatchers.is(true));
        Instant expectedNotBefore = instant.minus(5, ChronoUnit.SECONDS);
        MatcherAssert.assertThat(jwt.notBefore(), CoreMatchers.is(Optional.of(expectedNotBefore)));
        Instant expectedExpiry = instant.plus(((60 * 60) * 24), ChronoUnit.SECONDS);
        MatcherAssert.assertThat(jwt.expirationTime(), CoreMatchers.is(Optional.of(expectedExpiry)));
        // now we need to use the same token to invoke authentication
        ProviderRequest atnRequest = Mockito.mock(ProviderRequest.class);
        SecurityEnvironment se = SecurityEnvironment.builder().header("Authorization", ("bearer " + signedToken)).build();
        Mockito.when(atnRequest.env()).thenReturn(se);
        AuthenticationResponse authenticationResponse = provider.syncAuthenticate(atnRequest);
        OptionalHelper.from(authenticationResponse.user().map(Subject::principal)).ifPresentOrElse(( atnPrincipal) -> {
            assertThat(atnPrincipal.id(), is(userId));
            assertThat(atnPrincipal.getName(), is(userId));
            assertThat(atnPrincipal.abacAttribute("email"), is(Optional.empty()));
            assertThat(atnPrincipal.abacAttribute("email_verified"), is(Optional.empty()));
            assertThat(atnPrincipal.abacAttribute("family_name"), is(Optional.empty()));
            assertThat(atnPrincipal.abacAttribute("given_name"), is(Optional.empty()));
            assertThat(atnPrincipal.abacAttribute("full_name"), is(Optional.empty()));
            assertThat(atnPrincipal.abacAttribute("locale"), is(Optional.empty()));
        }, () -> fail("User must be present in response"));
    }

    @Test
    public void testRsaBothWays() {
        String username = "user1";
        String userId = "user1-id";
        String email = "user1@example.org";
        String familyName = "Novak";
        String givenName = "Standa";
        String fullName = "Standa Novak";
        Locale locale = Locale.CANADA_FRENCH;
        Principal principal = Principal.builder().name(username).id(userId).addAttribute("email", email).addAttribute("email_verified", true).addAttribute("family_name", familyName).addAttribute("given_name", givenName).addAttribute("full_name", fullName).addAttribute("locale", locale).build();
        Subject subject = Subject.create(principal);
        JwtProvider provider = JwtProvider.create(Config.create().get("security.providers.0.jwt"));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.of(subject));
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        SecurityEnvironment outboundEnv = SecurityEnvironment.builder().path("/rsa").transport("http").targetUri(URI.create("http://localhost:8080/rsa")).build();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat(provider.isOutboundSupported(request, outboundEnv, outboundEp), CoreMatchers.is(true));
        OutboundSecurityResponse response = provider.syncOutbound(request, outboundEnv, outboundEp);
        String signedToken = response.requestHeaders().get("Authorization").get(0);
        signedToken = signedToken.substring("bearer ".length());
        // now I want to validate it to prove it was correctly signed
        SignedJwt signedJwt = SignedJwt.parseToken(signedToken);
        signedJwt.verifySignature(JwtProviderTest.verifyKeys).checkValid();
        Jwt jwt = signedJwt.getJwt();
        MatcherAssert.assertThat(jwt.subject(), CoreMatchers.is(Optional.of(userId)));
        MatcherAssert.assertThat(jwt.preferredUsername(), CoreMatchers.is(Optional.of(username)));
        MatcherAssert.assertThat(jwt.email(), CoreMatchers.is(Optional.of(email)));
        MatcherAssert.assertThat(jwt.emailVerified(), CoreMatchers.is(Optional.of(true)));
        MatcherAssert.assertThat(jwt.familyName(), CoreMatchers.is(Optional.of(familyName)));
        MatcherAssert.assertThat(jwt.givenName(), CoreMatchers.is(Optional.of(givenName)));
        MatcherAssert.assertThat(jwt.fullName(), CoreMatchers.is(Optional.of(fullName)));
        MatcherAssert.assertThat(jwt.locale(), CoreMatchers.is(Optional.of(locale)));
        MatcherAssert.assertThat(jwt.audience(), CoreMatchers.is(Optional.of(CollectionsHelper.listOf("audience.application.id"))));
        MatcherAssert.assertThat(jwt.issuer(), CoreMatchers.is(Optional.of("jwt.example.com")));
        MatcherAssert.assertThat(jwt.algorithm(), CoreMatchers.is(Optional.of(ALG_RS256)));
        MatcherAssert.assertThat(jwt.issueTime(), CoreMatchers.is(CoreMatchers.not(Optional.empty())));
        jwt.issueTime().ifPresent(( instant) -> {
            boolean compareResult = (Instant.now().minusSeconds(10).compareTo(instant)) < 0;
            assertThat("Issue time must not be older than 10 seconds", compareResult, is(true));
            Instant expectedNotBefore = instant.minus(60, ChronoUnit.SECONDS);
            assertThat(jwt.notBefore(), is(Optional.of(expectedNotBefore)));
            Instant expectedExpiry = instant.plus(3600, ChronoUnit.SECONDS);
            assertThat(jwt.expirationTime(), is(Optional.of(expectedExpiry)));
        });
        // now we need to use the same token to invoke authentication
        ProviderRequest atnRequest = Mockito.mock(ProviderRequest.class);
        SecurityEnvironment se = SecurityEnvironment.builder().header("Authorization", ("bearer " + signedToken)).build();
        Mockito.when(atnRequest.env()).thenReturn(se);
        AuthenticationResponse authenticationResponse = provider.syncAuthenticate(atnRequest);
        OptionalHelper.from(authenticationResponse.user().map(Subject::principal)).ifPresentOrElse(( atnPrincipal) -> {
            assertThat(atnPrincipal.id(), is(userId));
            assertThat(atnPrincipal.getName(), is(username));
            assertThat(atnPrincipal.abacAttribute("email"), is(Optional.of(email)));
            assertThat(atnPrincipal.abacAttribute("email_verified"), is(Optional.of(true)));
            assertThat(atnPrincipal.abacAttribute("family_name"), is(Optional.of(familyName)));
            assertThat(atnPrincipal.abacAttribute("given_name"), is(Optional.of(givenName)));
            assertThat(atnPrincipal.abacAttribute("full_name"), is(Optional.of(fullName)));
            assertThat(atnPrincipal.abacAttribute("locale"), is(Optional.of(locale)));
        }, () -> fail("User must be present in response"));
    }
}

