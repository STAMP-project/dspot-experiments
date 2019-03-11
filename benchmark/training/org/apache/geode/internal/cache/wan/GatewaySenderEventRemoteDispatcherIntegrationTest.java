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
package org.apache.geode.internal.cache.wan;


import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.geode.cache.client.internal.Connection;
import org.apache.geode.cache.client.internal.Endpoint;
import org.apache.geode.cache.client.internal.EndpointManager;
import org.apache.geode.cache.client.internal.PoolImpl;
import org.apache.geode.distributed.internal.ServerLocation;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.Mockito;


public class GatewaySenderEventRemoteDispatcherIntegrationTest {
    /* Sometimes hostname lookup is flaky. We don't want such a failure to cripple our
    event processor.

    This test assumes hostname lookup (of IP number) succeeds when establishing the initial
    connection, but fails when constructing the InternalDistributedSystem object in response to a
    remote server crash.
     */
    @Test
    public void canProcessesEventAfterHostnameLookupFailsInNotifyServerCrashed() throws Exception {
        final PoolImpl pool = getPool();
        final ServerLocation serverLocation = Mockito.mock(ServerLocation.class);
        final AbstractGatewaySenderEventProcessor eventProcessor = getMockedAbstractGatewaySenderEventProcessor(pool, serverLocation);
        final Endpoint endpoint = getMockedEndpoint(serverLocation);
        final Connection connection = getMockedConnection(serverLocation, endpoint);
        /* In order for listeners to be notified, the endpoint must be referenced by the
        endpointManager so that it can be removed when the RuntimeException() is thrown by the
        connection
         */
        final EndpointManager endpointManager = pool.getEndpointManager();
        endpointManager.referenceEndpoint(serverLocation, Mockito.mock(InternalDistributedMember.class));
        final GatewaySenderEventRemoteDispatcher dispatcher = new GatewaySenderEventRemoteDispatcher(eventProcessor, connection);
        /* Set a HostnameResolver which simulates a failed
        hostname lookup resulting in an UnknownHostException
         */
        InternalDistributedMember.setHostnameResolver(( ignored) -> {
            throw new UnknownHostException("a.b.c");
        });
        /* We have mocked our connection to throw a RuntimeException when readAcknowledgement() is
        called, then in the exception handling for that RuntimeException, the UnknownHostException
        will be thrown when trying to notify listeners of the crash.
         */
        dispatcher.readAcknowledgement();
        /* Need to reset the hostname resolver to a real InetAddress resolver as it is static state and
        we do not want it to throw an UnknownHostException in subsequent test runs.
         */
        InternalDistributedMember.setHostnameResolver(( location) -> InetAddress.getByName(location.getHostName()));
        /* The handling of the UnknownHostException should not result in the event processor being
        stopped, so assert that setIsStopped(true) was never called.
         */
        Mockito.verify(eventProcessor, Mockito.times(0)).setIsStopped(true);
    }
}

