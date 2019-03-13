/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.channel;


import org.junit.Test;
import org.kaaproject.kaa.client.bootstrap.BootstrapManager;
import org.kaaproject.kaa.client.channel.impl.transports.DefaultRedirectionTransport;
import org.kaaproject.kaa.common.endpoint.gen.RedirectSyncResponse;
import org.mockito.Mockito;


public class DefaultRedirectionTransportTest {
    @Test
    public void testOnRedirectionResponse() {
        BootstrapManager manager = Mockito.mock(BootstrapManager.class);
        RedirectionTransport transport = new DefaultRedirectionTransport();
        RedirectSyncResponse response = new RedirectSyncResponse();
        transport.onRedirectionResponse(response);
        transport.setBootstrapManager(manager);
        transport.onRedirectionResponse(response);
        response.setAccessPointId(1);
        transport.onRedirectionResponse(response);
        response.setAccessPointId(2);
        transport.onRedirectionResponse(response);
        Mockito.verify(manager, Mockito.times(1)).useNextOperationsServerByAccessPointId(1);
    }
}

