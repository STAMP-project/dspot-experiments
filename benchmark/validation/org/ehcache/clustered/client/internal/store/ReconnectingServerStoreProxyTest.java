/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.client.internal.store;


import java.nio.ByteBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.terracotta.exception.ConnectionClosedException;


public class ReconnectingServerStoreProxyTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    ServerStoreProxy proxy;

    @Mock
    Runnable runnable;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final ServerStoreProxyException storeProxyException = new ServerStoreProxyException(new ConnectionClosedException("Connection Closed"));

    @InjectMocks
    ReconnectingServerStoreProxy serverStoreProxy;

    @Test
    public void testAppend() throws Exception {
        Mockito.doThrow(storeProxyException).when(proxy).append(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        exception.expect(ReconnectInProgressException.class);
        serverStoreProxy.append(0, ByteBuffer.allocate(2));
    }

    @Test
    public void testGetAndAppend() throws Exception {
        Mockito.doThrow(storeProxyException).when(proxy).getAndAppend(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ByteBuffer.class));
        exception.expect(ReconnectInProgressException.class);
        serverStoreProxy.getAndAppend(0, ByteBuffer.allocate(2));
    }

    @Test
    public void testGet() throws Exception {
        Mockito.doThrow(storeProxyException).when(proxy).get(ArgumentMatchers.anyLong());
        exception.expect(ReconnectInProgressException.class);
        serverStoreProxy.get(0);
    }

    @Test
    public void testIterator() throws Exception {
        Mockito.doThrow(storeProxyException).when(proxy).iterator();
        exception.expect(ReconnectInProgressException.class);
        serverStoreProxy.iterator();
    }
}

