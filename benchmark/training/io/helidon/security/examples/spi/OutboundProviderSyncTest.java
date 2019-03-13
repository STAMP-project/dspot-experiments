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
import SecurityResponse.SecurityStatus.SUCCESS;
import io.helidon.common.CollectionsHelper;
import io.helidon.security.EndpointConfig;
import io.helidon.security.OutboundSecurityResponse;
import io.helidon.security.Principal;
import io.helidon.security.ProviderRequest;
import io.helidon.security.SecurityContext;
import io.helidon.security.SecurityEnvironment;
import io.helidon.security.Subject;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Unit test for {@link OutboundProviderSync}.
 */
public class OutboundProviderSyncTest {
    @Test
    public void testAbstain() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.empty());
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        OutboundProviderSync ops = new OutboundProviderSync();
        OutboundSecurityResponse response = ops.syncOutbound(request, SecurityEnvironment.create(), EndpointConfig.create());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(ABSTAIN));
    }

    @Test
    public void testSuccess() {
        String username = "aUser";
        Subject subject = Subject.create(Principal.create(username));
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.user()).thenReturn(Optional.of(subject));
        Mockito.when(context.service()).thenReturn(Optional.empty());
        SecurityEnvironment se = SecurityEnvironment.create();
        ProviderRequest request = Mockito.mock(ProviderRequest.class);
        Mockito.when(request.securityContext()).thenReturn(context);
        Mockito.when(request.env()).thenReturn(se);
        OutboundProviderSync ops = new OutboundProviderSync();
        OutboundSecurityResponse response = ops.syncOutbound(request, SecurityEnvironment.create(), EndpointConfig.create());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        MatcherAssert.assertThat(response.requestHeaders().get("X-AUTH-USER"), CoreMatchers.is(CollectionsHelper.listOf(username)));
    }
}

