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


import SecurityEnvironment.Builder;
import SecurityResponse.SecurityStatus.FAILURE;
import SecurityResponse.SecurityStatus.SUCCESS;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link CompositeOutboundProvider}.
 */
public abstract class CompositePolicyTest {
    // test with full security
    @Test
    public void testSuccessSecurity() {
        SecurityContext context = getSecurity().contextBuilder("testSuccessSecurity").build();
        SecurityEnvironment.Builder envBuilder = context.env().derive().path("/jack").addAttribute("resourceType", "service");
        context.env(envBuilder);
        AuthenticationResponse atnResponse = context.authenticate();
        MatcherAssert.assertThat(atnResponse, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(atnResponse.status(), CoreMatchers.is(SUCCESS));
        Subject subject = atnResponse.service().get();
        MatcherAssert.assertThat(subject.principal().getName(), CoreMatchers.is("resource-aService"));
        OutboundSecurityResponse outboundResponse = context.outboundClientBuilder().outboundEnvironment(envBuilder).outboundEndpointConfig(EndpointConfig.create()).buildAndGet();
        MatcherAssert.assertThat(outboundResponse.status(), CoreMatchers.is(SUCCESS));
        Map<String, List<String>> headers = outboundResponse.requestHeaders();
        MatcherAssert.assertThat(headers.size(), CoreMatchers.is(2));
        List<String> path = headers.get("path");
        List<String> resource = headers.get("resource");
        MatcherAssert.assertThat(path, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(path.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(resource, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resource.size(), CoreMatchers.is(1));
        String pathHeader = path.iterator().next();
        String resourceHeader = resource.iterator().next();
        MatcherAssert.assertThat(pathHeader, CoreMatchers.is("path-jack"));
        MatcherAssert.assertThat(resourceHeader, CoreMatchers.is("resource-aService"));
    }

    @Test
    public void testAtz() {
        AuthorizationResponse response = SecurityResponse.get(getAuthorization().authorize(context("/atz/permit", "atz/permit")));
        MatcherAssert.assertThat(response, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        response = SecurityResponse.get(getAuthorization().authorize(context("/atz/abstain", "atz/permit")));
        MatcherAssert.assertThat(response, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        response = SecurityResponse.get(getAuthorization().authorize(context("/atz/abstain", "atz/abstain")));
        MatcherAssert.assertThat(response, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(FAILURE));
    }

    @Test
    public void testAtnAllSuccess() throws InterruptedException, ExecutionException {
        AuthenticationResponse response = getAuthentication().authenticate(context("/jack", "service")).toCompletableFuture().get();
        MatcherAssert.assertThat(response, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        Subject user = response.user().get();
        MatcherAssert.assertThat(user.principal().getName(), CoreMatchers.is("path-jack"));
        Subject service = response.service().get();
        MatcherAssert.assertThat(service.principal().getName(), CoreMatchers.is("resource-aService"));
    }

    @Test
    public void testAtnAllSuccessServiceFirst() throws InterruptedException, ExecutionException {
        AuthenticationResponse response = getAuthentication().authenticate(context("/service", "jack")).toCompletableFuture().get();
        MatcherAssert.assertThat(response, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        Subject user = response.user().get();
        MatcherAssert.assertThat(user.principal().getName(), CoreMatchers.is("resource-jack"));
        Subject service = response.service().get();
        MatcherAssert.assertThat(service.principal().getName(), CoreMatchers.is("path-aService"));
    }

    @Test
    public void testOutboundSuccess() throws InterruptedException, ExecutionException {
        ProviderRequest context = context("/jack", "service");
        MatcherAssert.assertThat(getOutbound().isOutboundSupported(context, context.env(), context.endpointConfig()), CoreMatchers.is(true));
        OutboundSecurityResponse response = getOutbound().outboundSecurity(context, context.env(), context.endpointConfig()).toCompletableFuture().get();
        MatcherAssert.assertThat(response.status(), CoreMatchers.is(SUCCESS));
        Map<String, List<String>> headers = response.requestHeaders();
        MatcherAssert.assertThat(headers.size(), CoreMatchers.is(2));
        List<String> path = headers.get("path");
        List<String> resource = headers.get("resource");
        MatcherAssert.assertThat(path, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(path.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(resource, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(resource.size(), CoreMatchers.is(1));
        String pathHeader = path.iterator().next();
        String resourceHeader = resource.iterator().next();
        MatcherAssert.assertThat(pathHeader, CoreMatchers.is("path-jack"));
        MatcherAssert.assertThat(resourceHeader, CoreMatchers.is("resource-aService"));
    }
}

