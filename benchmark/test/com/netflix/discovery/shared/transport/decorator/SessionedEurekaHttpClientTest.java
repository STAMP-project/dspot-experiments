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


import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpClientFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Tomasz Bak
 */
public class SessionedEurekaHttpClientTest {
    private final EurekaHttpClient firstClient = Mockito.mock(EurekaHttpClient.class);

    private final EurekaHttpClient secondClient = Mockito.mock(EurekaHttpClient.class);

    private final EurekaHttpClientFactory factory = Mockito.mock(EurekaHttpClientFactory.class);

    @Test
    public void testReconnectIsEnforcedAtConfiguredInterval() throws Exception {
        final AtomicReference<EurekaHttpClient> clientRef = new AtomicReference(firstClient);
        Mockito.when(factory.newClient()).thenAnswer(new Answer<EurekaHttpClient>() {
            @Override
            public EurekaHttpClient answer(InvocationOnMock invocation) throws Throwable {
                return clientRef.get();
            }
        });
        SessionedEurekaHttpClient httpClient = null;
        try {
            httpClient = new SessionedEurekaHttpClient("test", factory, 1);
            httpClient.getApplications();
            Mockito.verify(firstClient, Mockito.times(1)).getApplications();
            clientRef.set(secondClient);
            Thread.sleep(2);
            httpClient.getApplications();
            Mockito.verify(secondClient, Mockito.times(1)).getApplications();
        } finally {
            if (httpClient != null) {
                httpClient.shutdown();
            }
        }
    }
}

