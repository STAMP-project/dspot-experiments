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
package io.helidon.security.providers.google.login;


import SecurityResponse.SecurityStatus.FAILURE;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.helidon.security.AuthenticationResponse;
import io.helidon.security.EndpointConfig;
import io.helidon.security.OutboundSecurityResponse;
import io.helidon.security.Principal;
import io.helidon.security.ProviderRequest;
import io.helidon.security.SecurityEnvironment;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link GoogleTokenProvider}.
 */
public class GoogleTokenProviderTest {
    private static final String TOKEN_VALUE = "12345safdafa12354asdf24a4sdfasdfasdf";

    private static final String pictureUrl = "http://www.google.com";

    private static final String email = "test@google.com";

    private static final String familyName = "Novak";

    private static final String givenName = "Jarda";

    private static final String fullName = "Jarda Novak";

    private static final String name = "test@google.com";

    private static final String userId = "123456789";

    private static GoogleTokenProvider provider;

    @Test
    public void testInbound() {
        ProviderRequest inboundRequest = createInboundRequest("Authorization", ("bearer " + (GoogleTokenProviderTest.TOKEN_VALUE)));
        AuthenticationResponse response = GoogleTokenProviderTest.provider.syncAuthenticate(inboundRequest);
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(CoreMatchers.not(Optional.empty())));
        response.user().ifPresent(( subject) -> {
            Principal principal = subject.principal();
            assertThat(principal.getName(), is(name));
            assertThat(principal.id(), is(userId));
        });
    }

    @Test
    public void testInboundIncorrectToken() throws InterruptedException, ExecutionException {
        ProviderRequest inboundRequest = createInboundRequest("Authorization", ("tearer " + (GoogleTokenProviderTest.TOKEN_VALUE)));
        AuthenticationResponse response = GoogleTokenProviderTest.provider.authenticate(inboundRequest).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(400));
        MatcherAssert.assertThat(response.responseHeaders().get("WWW-Authenticate"), CoreMatchers.notNullValue());
    }

    @Test
    public void testInboundMissingToken() throws InterruptedException, ExecutionException {
        ProviderRequest inboundRequest = createInboundRequest("OtherHeader", ("tearer " + (GoogleTokenProviderTest.TOKEN_VALUE)));
        AuthenticationResponse response = GoogleTokenProviderTest.provider.authenticate(inboundRequest).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        MatcherAssert.assertThat(response.responseHeaders().get("WWW-Authenticate"), CoreMatchers.notNullValue());
    }

    @Test
    public void testInboundInvalidToken() throws IOException, InterruptedException, GeneralSecurityException, ExecutionException {
        GoogleIdTokenVerifier verifier = Mockito.mock(GoogleIdTokenVerifier.class);
        Mockito.when(verifier.verify(GoogleTokenProviderTest.TOKEN_VALUE)).thenReturn(null);
        GoogleTokenProvider provider = GoogleTokenProvider.builder().clientId("clientId").verifier(verifier).build();
        ProviderRequest inboundRequest = createInboundRequest("Authorization", ("bearer " + (GoogleTokenProviderTest.TOKEN_VALUE)));
        AuthenticationResponse response = provider.authenticate(inboundRequest).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        MatcherAssert.assertThat(response.responseHeaders().get("WWW-Authenticate"), CoreMatchers.notNullValue());
    }

    @Test
    public void testInboundVerificationException() throws IOException, InterruptedException, GeneralSecurityException, ExecutionException {
        GoogleIdTokenVerifier verifier = Mockito.mock(GoogleIdTokenVerifier.class);
        Mockito.when(verifier.verify(GoogleTokenProviderTest.TOKEN_VALUE)).thenThrow(new IOException("Failed to verify token"));
        GoogleTokenProvider provider = GoogleTokenProvider.builder().clientId("clientId").verifier(verifier).build();
        ProviderRequest inboundRequest = createInboundRequest("Authorization", ("bearer " + (GoogleTokenProviderTest.TOKEN_VALUE)));
        AuthenticationResponse response = provider.authenticate(inboundRequest).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.statusCode().orElse(200), CoreMatchers.is(401));
        MatcherAssert.assertThat(response.responseHeaders().get("WWW-Authenticate"), CoreMatchers.notNullValue());
    }

    @Test
    public void testOutbound() {
        ProviderRequest outboundRequest = buildOutboundRequest();
        SecurityEnvironment outboundEnv = SecurityEnvironment.create();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat("Outbound should be supported", GoogleTokenProviderTest.provider.isOutboundSupported(outboundRequest, outboundEnv, outboundEp), CoreMatchers.is(true));
        OutboundSecurityResponse response = GoogleTokenProviderTest.provider.syncOutbound(outboundRequest, outboundEnv, outboundEp);
        List<String> authorization = response.requestHeaders().get("Authorization");
        MatcherAssert.assertThat(authorization, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(authorization.size(), CoreMatchers.is(1));
        String header = authorization.get(0);
        MatcherAssert.assertThat(header.toLowerCase(), CoreMatchers.startsWith("bearer "));
        MatcherAssert.assertThat(header, CoreMatchers.endsWith(GoogleTokenProviderTest.TOKEN_VALUE));
    }
}

