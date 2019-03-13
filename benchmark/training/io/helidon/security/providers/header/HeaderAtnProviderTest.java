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
package io.helidon.security.providers.header;


import SecurityResponse.SecurityStatus.ABSTAIN;
import SecurityResponse.SecurityStatus.FAILURE;
import SecurityResponse.SecurityStatus.SUCCESS;
import io.helidon.security.AuthenticationResponse;
import io.helidon.security.EndpointConfig;
import io.helidon.security.OutboundSecurityResponse;
import io.helidon.security.Principal;
import io.helidon.security.ProviderRequest;
import io.helidon.security.SecurityContext;
import io.helidon.security.SecurityEnvironment;
import io.helidon.security.Subject;
import java.util.List;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link HeaderAtnProvider}.
 */
public abstract class HeaderAtnProviderTest {
    @Test
    public void testExtraction() {
        String username = "username";
        HeaderAtnProvider provider = getFullProvider();
        SecurityEnvironment env = SecurityEnvironment.builder().header("Authorization", ("bearer " + username)).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(CoreMatchers.not(Optional.empty())));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
        response.user().map(Subject::principal).map(Principal::getName).ifPresent(( name) -> assertThat(name, is(username)));
    }

    @Test
    public void testExtractionNoHeader() {
        HeaderAtnProvider provider = getFullProvider();
        SecurityEnvironment env = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(ABSTAIN));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void testOutbound() {
        HeaderAtnProvider provider = getFullProvider();
        SecurityEnvironment env = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        SecurityContext sc = Mockito.mock(SecurityContext.class);
        Mockito.when(sc.user()).thenReturn(Optional.of(Subject.builder().addPrincipal(Principal.create("username")).build()));
        Mockito.when(sc.service()).thenReturn(Optional.empty());
        Mockito.when(request.securityContext()).thenReturn(sc);
        SecurityEnvironment outboundEnv = SecurityEnvironment.create();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat("Outbound should be supported", provider.isOutboundSupported(request, outboundEnv, outboundEp), CoreMatchers.is(true));
        OutboundSecurityResponse response = provider.syncOutbound(request, outboundEnv, outboundEp);
        List<String> custom = response.requestHeaders().get("Custom");
        MatcherAssert.assertThat(custom, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(custom.size(), CoreMatchers.is(1));
        String token = custom.get(0);
        MatcherAssert.assertThat(token, CoreMatchers.is("bearer username"));
    }

    @Test
    public void testServiceExtraction() {
        HeaderAtnProvider provider = getServiceProvider();
        String username = "service";
        SecurityEnvironment env = SecurityEnvironment.builder().header("Authorization", ("bearer " + username)).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(CoreMatchers.not(Optional.empty())));
        response.service().map(Subject::principal).map(Principal::getName).ifPresent(( name) -> assertThat(name, is(username)));
    }

    @Test
    public void testServiceNoHeaderExtraction() {
        HeaderAtnProvider provider = getServiceProvider();
        SecurityEnvironment env = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void testServiceOutbound() {
        HeaderAtnProvider provider = getServiceProvider();
        String username = "service";
        SecurityEnvironment env = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        SecurityContext sc = Mockito.mock(SecurityContext.class);
        Mockito.when(sc.service()).thenReturn(Optional.of(Subject.builder().addPrincipal(Principal.create(username)).build()));
        Mockito.when(sc.user()).thenReturn(Optional.empty());
        Mockito.when(request.securityContext()).thenReturn(sc);
        SecurityEnvironment outboundEnv = SecurityEnvironment.create();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat("Outbound should be supported", provider.isOutboundSupported(request, outboundEnv, outboundEp), CoreMatchers.is(true));
        OutboundSecurityResponse response = provider.syncOutbound(request, outboundEnv, outboundEp);
        List<String> custom = response.requestHeaders().get("Authorization");
        MatcherAssert.assertThat(custom, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(custom.size(), CoreMatchers.is(1));
        String token = custom.get(0);
        MatcherAssert.assertThat(token, CoreMatchers.is(("bearer " + username)));
    }

    @Test
    public void testNoAtn() {
        String username = "username";
        HeaderAtnProvider provider = getNoSecurityProvider();
        SecurityEnvironment env = SecurityEnvironment.builder().header("Authorization", ("bearer " + username)).build();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        AuthenticationResponse response = provider.syncAuthenticate(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(ABSTAIN));
        MatcherAssert.assertThat(response.user(), CoreMatchers.is(Optional.empty()));
        MatcherAssert.assertThat(response.service(), CoreMatchers.is(Optional.empty()));
    }

    @Test
    public void testNoOutbound() {
        String username = "username";
        HeaderAtnProvider provider = getNoSecurityProvider();
        SecurityEnvironment env = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(env);
        SecurityContext sc = Mockito.mock(SecurityContext.class);
        Mockito.when(sc.user()).thenReturn(Optional.of(Subject.builder().addPrincipal(Principal.create(username)).build()));
        Mockito.when(sc.service()).thenReturn(Optional.empty());
        Mockito.when(request.securityContext()).thenReturn(sc);
        SecurityEnvironment outboundEnv = SecurityEnvironment.create();
        EndpointConfig outboundEp = EndpointConfig.create();
        MatcherAssert.assertThat("Outbound should not be supported", provider.isOutboundSupported(request, outboundEnv, outboundEp), CoreMatchers.is(false));
    }
}

