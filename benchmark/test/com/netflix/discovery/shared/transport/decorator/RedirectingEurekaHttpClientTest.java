/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.discovery.shared.transport.decorator;


import MediaType.APPLICATION_JSON_TYPE;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.dns.DnsService;
import com.netflix.discovery.shared.resolver.EurekaEndpoint;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.TransportClientFactory;
import com.netflix.discovery.shared.transport.TransportException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Tomasz Bak
 */
public class RedirectingEurekaHttpClientTest {
    private static final String SERVICE_URL = "http://mydiscovery.test";

    private final TransportClientFactory factory = Mockito.mock(TransportClientFactory.class);

    private final EurekaHttpClient sourceClient = Mockito.mock(EurekaHttpClient.class);

    private final EurekaHttpClient redirectedClient = Mockito.mock(EurekaHttpClient.class);

    private final DnsService dnsService = Mockito.mock(DnsService.class);

    @Test
    public void testNonRedirectedRequestsAreServedByFirstClient() throws Exception {
        Mockito.when(factory.newClient(Matchers.<EurekaEndpoint>anyVararg())).thenReturn(sourceClient);
        Mockito.when(sourceClient.getApplications()).thenReturn(EurekaHttpResponse.anEurekaHttpResponse(200, new Applications()).type(APPLICATION_JSON_TYPE).build());
        RedirectingEurekaHttpClient httpClient = new RedirectingEurekaHttpClient(RedirectingEurekaHttpClientTest.SERVICE_URL, factory, dnsService);
        httpClient.getApplications();
        Mockito.verify(factory, Mockito.times(1)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(sourceClient, Mockito.times(1)).getApplications();
    }

    @Test
    public void testRedirectsAreFollowedAndClientIsPinnedToTheLastServer() throws Exception {
        setupRedirect();
        RedirectingEurekaHttpClient httpClient = new RedirectingEurekaHttpClient(RedirectingEurekaHttpClientTest.SERVICE_URL, factory, dnsService);
        // First call pins client to resolved IP
        httpClient.getApplications();
        Mockito.verify(factory, Mockito.times(2)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(sourceClient, Mockito.times(1)).getApplications();
        Mockito.verify(dnsService, Mockito.times(1)).resolveIp("another.discovery.test");
        Mockito.verify(redirectedClient, Mockito.times(1)).getApplications();
        // Second call goes straight to the same address
        httpClient.getApplications();
        Mockito.verify(factory, Mockito.times(2)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(sourceClient, Mockito.times(1)).getApplications();
        Mockito.verify(dnsService, Mockito.times(1)).resolveIp("another.discovery.test");
        Mockito.verify(redirectedClient, Mockito.times(2)).getApplications();
    }

    @Test
    public void testOnConnectionErrorPinnedClientIsDestroyed() throws Exception {
        setupRedirect();
        RedirectingEurekaHttpClient httpClient = new RedirectingEurekaHttpClient(RedirectingEurekaHttpClientTest.SERVICE_URL, factory, dnsService);
        // First call pins client to resolved IP
        httpClient.getApplications();
        Mockito.verify(redirectedClient, Mockito.times(1)).getApplications();
        // Trigger connection error
        Mockito.when(redirectedClient.getApplications()).thenThrow(new TransportException("simulated network error"));
        try {
            httpClient.getApplications();
            Assert.fail("Expected transport error");
        } catch (Exception ignored) {
        }
        // Subsequent connection shall create new httpClient
        Mockito.reset(factory, sourceClient, dnsService, redirectedClient);
        setupRedirect();
        httpClient.getApplications();
        Mockito.verify(factory, Mockito.times(2)).newClient(Matchers.<EurekaEndpoint>anyVararg());
        Mockito.verify(sourceClient, Mockito.times(1)).getApplications();
        Mockito.verify(dnsService, Mockito.times(1)).resolveIp("another.discovery.test");
        Mockito.verify(redirectedClient, Mockito.times(1)).getApplications();
    }
}

