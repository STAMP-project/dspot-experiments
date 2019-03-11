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
package io.helidon.security;


import SecurityContext.ANONYMOUS;
import SecurityEnvironment.Builder;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Test security from configuration.
 */
public class SecurityFromConfigTest {
    private Security security;

    @Test
    public void testSecurityProviderAuthn() {
        SecurityContext context = security.contextBuilder("unitTest").build();
        MatcherAssert.assertThat(context.isAuthenticated(), CoreMatchers.is(false));
        SecurityEnvironment.Builder envBuilder = context.env().derive().method("GET").path("/test").addAttribute("resourceType", "TEST").targetUri(URI.create("http://localhost/test"));
        context.env(envBuilder);
        AuthenticationResponse authenticate = context.atnClientBuilder().buildAndGet();
        // current thread should have the correct subject
        MatcherAssert.assertThat(authenticate.user(), CoreMatchers.is(Optional.of(SecurityTest.SYSTEM)));
        // should work from context
        MatcherAssert.assertThat(context.user(), CoreMatchers.is(Optional.of(SecurityTest.SYSTEM)));
        context.runAs(ANONYMOUS, () -> assertThat(context.isAuthenticated(), is(false)));
        // and correct once again
        MatcherAssert.assertThat(context.user(), CoreMatchers.is(Optional.of(SecurityTest.SYSTEM)));
    }

    @Test
    public void testSecurityProviderAuthz() throws InterruptedException, ExecutionException {
        SecurityContext context = security.contextBuilder("unitTest").build();
        SecurityEnvironment.Builder envBuilder = context.env().derive().method("GET").path("/test").targetUri(URI.create("http://localhost/test"));
        context.env(envBuilder.addAttribute("resourceType", "SECOND_DENY"));
        // default authorizationClient
        AuthorizationResponse response = context.atzClientBuilder().buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        context.env(envBuilder.addAttribute("resourceType", "PERMIT"));
        response = context.atzClientBuilder().buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(true));
        context.env(envBuilder.addAttribute("resourceType", "DENY"));
        // non-default authorizationClient
        response = context.atzClientBuilder().explicitProvider("FirstInstance").buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        context.env(envBuilder.addAttribute("resourceType", "SECOND_DENY"));
        response = context.atzClientBuilder().explicitProvider("FirstInstance").buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(true));
    }
}

