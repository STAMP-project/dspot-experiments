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
package io.helidon.security.examples.spi;


import SecurityResponse.SecurityStatus.ABSTAIN;
import SecurityResponse.SecurityStatus.FAILURE;
import SecurityResponse.SecurityStatus.SUCCESS;
import io.helidon.security.AuthorizationResponse;
import io.helidon.security.EndpointConfig;
import io.helidon.security.ProviderRequest;
import io.helidon.security.SecurityContext;
import io.helidon.security.SecurityEnvironment;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link AtzProviderSync}.
 */
public class AtzProviderSyncTest {
    @Test
    public void testPublic() {
        SecurityEnvironment se = SecurityEnvironment.builder().path("/public/some/path").build();
        EndpointConfig ep = EndpointConfig.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        AtzProviderSync provider = new AtzProviderSync();
        AuthorizationResponse response = provider.syncAuthorize(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
    }

    @Test
    public void testAbstain() {
        SecurityEnvironment se = SecurityEnvironment.create();
        EndpointConfig ep = EndpointConfig.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        AtzProviderSync provider = new AtzProviderSync();
        AuthorizationResponse response = provider.syncAuthorize(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(ABSTAIN));
    }

    @Test
    public void testDenied() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.isAuthenticated()).thenReturn(false);
        SecurityEnvironment se = SecurityEnvironment.builder().path("/private/some/path").build();
        EndpointConfig ep = EndpointConfig.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        AtzProviderSync provider = new AtzProviderSync();
        AuthorizationResponse response = provider.syncAuthorize(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
    }

    @Test
    public void testPermitted() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.isAuthenticated()).thenReturn(true);
        SecurityEnvironment se = SecurityEnvironment.builder().path("/private/some/path").build();
        EndpointConfig ep = EndpointConfig.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        Mockito.when(request.endpointConfig()).thenReturn(ep);
        AtzProviderSync provider = new AtzProviderSync();
        AuthorizationResponse response = provider.syncAuthorize(request);
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
    }
}

