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
import io.helidon.security.providers.ProviderForTesting;
import java.net.URI;
import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Main unit test for {@link Security}.
 */
public class SecurityTest {
    /**
     * System subject.
     * A special subject declaring that this is run "as system", rather under user's
     * or service's subject.
     */
    public static final Subject SYSTEM = Subject.builder().principal(Principal.create("<SYSTEM>")).build();

    private static Security security;

    private static ProviderForTesting firstProvider;

    private static ProviderForTesting secondProvider;

    @Test
    public void testSecurityProviderAuthn() {
        SecurityContext context = SecurityTest.security.contextBuilder("unitTest").env(SecurityEnvironment.builder(SecurityTest.security.serverTime()).method("GET").path("/test").targetUri(URI.create("http://localhost/test")).addAttribute("resourceType", "TEST").build()).build();
        MatcherAssert.assertThat(context.isAuthenticated(), CoreMatchers.is(false));
        AuthenticationResponse authenticate = context.authenticate();
        // current thread should have the correct subject
        MatcherAssert.assertThat(context.isAuthenticated(), CoreMatchers.is(true));
        MatcherAssert.assertThat(authenticate.user(), CoreMatchers.is(Optional.of(SecurityTest.SYSTEM)));
        // should work from context
        MatcherAssert.assertThat(context.user(), CoreMatchers.is(Optional.of(SecurityTest.SYSTEM)));
        context.runAs(ANONYMOUS, () -> assertThat(context.isAuthenticated(), is(false)));
        // and correct once again
        MatcherAssert.assertThat(context.user(), CoreMatchers.is(Optional.of(SecurityTest.SYSTEM)));
    }

    @Test
    public void testSecurityProviderAuthz() {
        SecurityContext context = SecurityTest.security.contextBuilder("unitTest").build();
        SecurityEnvironment.Builder envBuilder = context.env().derive().method("GET").path("/test").targetUri(URI.create("http://localhost/test"));
        context.env(envBuilder.addAttribute("resourceType", "SECOND_DENY"));
        // default authorizationClient
        AuthorizationResponse response = context.atzClientBuilder().buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        context.env(envBuilder.addAttribute("resourceType", "PERMIT"));
        response = context.atzClientBuilder().buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(true));
        // non-default authorizationClient
        context.env(envBuilder.addAttribute("resourceType", "DENY"));
        response = context.atzClientBuilder().explicitProvider("FirstInstance").buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(false));
        context.env(envBuilder.addAttribute("resourceType", "SECOND_DENY"));
        response = context.atzClientBuilder().explicitProvider("FirstInstance").buildAndGet();
        MatcherAssert.assertThat(response.status().isSuccess(), CoreMatchers.is(true));
    }
}

