/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache.client.internal;


import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.geode.CancelCriterion;
import org.apache.geode.CancelException;
import org.apache.geode.distributed.PoolCancelledException;
import org.apache.geode.distributed.internal.ServerLocation;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConnectionFactoryJUnitTest {
    @Test(expected = CancelException.class)
    public void connectionFactoryThrowsCancelException() throws IOException, CancelException {
        ServerLocation serverLocation = Mockito.mock(ServerLocation.class);
        Mockito.doReturn(false).when(serverLocation).getRequiresCredentials();
        ConnectionConnector connector = Mockito.mock(ConnectionConnector.class);
        Mockito.doReturn(Mockito.mock(ConnectionImpl.class)).when(connector).connectClientToServer(serverLocation, false);
        ConnectionSource connectionSource = Mockito.mock(ConnectionSource.class);
        Mockito.doReturn(serverLocation).when(connectionSource).findServer(ArgumentMatchers.any(Set.class));
        // mocks don't seem to work well with CancelCriterion so let's create a real one
        CancelCriterion cancelCriterion = new CancelCriterion() {
            @Override
            public String cancelInProgress() {
                return "shutting down for test";
            }

            @Override
            public RuntimeException generateCancelledException(Throwable throwable) {
                return new PoolCancelledException(cancelInProgress(), throwable);
            }
        };
        ConnectionFactoryImpl connectionFactory = new ConnectionFactoryImpl(connector, connectionSource, 60000, Mockito.mock(PoolImpl.class), cancelCriterion);
        connectionFactory.createClientToServerConnection(Collections.emptySet());
    }
}

