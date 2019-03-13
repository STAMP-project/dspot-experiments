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


import GatewayReceiver.DEFAULT_BIND_ADDRESS;
import GatewayReceiver.DEFAULT_END_PORT;
import GatewayReceiver.DEFAULT_HOSTNAME_FOR_SENDERS;
import GatewayReceiver.DEFAULT_MAXIMUM_TIME_BETWEEN_PINGS;
import GatewayReceiver.DEFAULT_SOCKET_BUFFER_SIZE;
import GatewayReceiver.DEFAULT_START_PORT;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.cache.wan.GatewayReceiver;
import org.apache.geode.cache.wan.GatewayTransportFilter;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.test.junit.runners.CategoryWithParameterizedRunnerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(CategoryWithParameterizedRunnerFactory.class)
public class GatewayReceiverFactoryImplJUnitTest {
    @Parameterized.Parameter
    public static InternalCache cache;

    private GatewayReceiverFactoryImpl gatewayReceiverFactory;

    @Test
    public void createWithDefaultAttributes() {
        GatewayReceiver receiver = gatewayReceiverFactory.create();
        assertThat(receiver.isManualStart()).isTrue();
        assertThat(receiver.getGatewayTransportFilters()).isEmpty();
        assertThat(receiver.getEndPort()).isEqualTo(DEFAULT_END_PORT);
        assertThat(receiver.getStartPort()).isEqualTo(DEFAULT_START_PORT);
        assertThat(receiver.getBindAddress()).isEqualTo(DEFAULT_BIND_ADDRESS);
        assertThat(receiver.getSocketBufferSize()).isEqualTo(DEFAULT_SOCKET_BUFFER_SIZE);
        assertThat(receiver.getHostnameForSenders()).isEqualTo(DEFAULT_HOSTNAME_FOR_SENDERS);
        assertThat(receiver.getMaximumTimeBetweenPings()).isEqualTo(DEFAULT_MAXIMUM_TIME_BETWEEN_PINGS);
        Mockito.verify(GatewayReceiverFactoryImplJUnitTest.cache, Mockito.times(1)).addGatewayReceiver(receiver);
    }

    @Test
    public void createWithCustomAttributes() {
        int endPort = 2500;
        int startPort = 1500;
        int socketBufferSize = 128;
        int timeoutBetweenPings = 1;
        String bindAddress = "kaos";
        String hostnameForSenders = "kaos.com";
        GatewayTransportFilter gatewayTransportFilter = Mockito.mock(GatewayTransportFilter.class);
        gatewayReceiverFactory.setEndPort(endPort);
        gatewayReceiverFactory.setStartPort(startPort);
        gatewayReceiverFactory.setBindAddress(bindAddress);
        gatewayReceiverFactory.setSocketBufferSize(socketBufferSize);
        gatewayReceiverFactory.setHostnameForSenders(hostnameForSenders);
        gatewayReceiverFactory.setMaximumTimeBetweenPings(timeoutBetweenPings);
        gatewayReceiverFactory.addGatewayTransportFilter(gatewayTransportFilter);
        GatewayReceiver receiver = gatewayReceiverFactory.create();
        assertThat(receiver.isManualStart()).isTrue();
        assertThat(receiver.getEndPort()).isEqualTo(endPort);
        assertThat(receiver.getStartPort()).isEqualTo(startPort);
        assertThat(receiver.getBindAddress()).isEqualTo(bindAddress);
        assertThat(receiver.getGatewayTransportFilters()).isNotEmpty();
        assertThat(receiver.getSocketBufferSize()).isEqualTo(socketBufferSize);
        assertThat(receiver.getHostnameForSenders()).isEqualTo(hostnameForSenders);
        assertThat(receiver.getMaximumTimeBetweenPings()).isEqualTo(timeoutBetweenPings);
        assertThat(receiver.getGatewayTransportFilters()).contains(gatewayTransportFilter);
        Mockito.verify(GatewayReceiverFactoryImplJUnitTest.cache, Mockito.times(1)).addGatewayReceiver(receiver);
    }

    @Test(expected = IllegalStateException.class)
    public void createShouldThrowExceptionWhenPortRangeIsInvalid() {
        gatewayReceiverFactory.setEndPort(1400);
        gatewayReceiverFactory.setStartPort(1500);
        gatewayReceiverFactory.create();
        Assert.fail("Exception should have been thrown: endPort < startPort.");
    }

    @Test(expected = IllegalStateException.class)
    public void createShouldThrownExceptionWhenGatewayReceiverAlreadyExists() {
        Set mockReceivers = new HashSet();
        mockReceivers.add(Mockito.mock(GatewayReceiver.class));
        Mockito.when(GatewayReceiverFactoryImplJUnitTest.cache.getGatewayReceivers()).thenReturn(mockReceivers);
        gatewayReceiverFactory.create();
        Assert.fail("Exception should have been thrown: a GatewayReceiver already exists on this cache.");
    }
}

