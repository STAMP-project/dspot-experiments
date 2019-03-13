/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.network.internal.discovery;


import NetworkBindingConstants.PARAMETER_HOSTNAME;
import NetworkBindingConstants.PARAMETER_PORT;
import java.util.Collections;
import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.network.internal.PresenceDetectionValue;


/**
 * Tests cases for {@see PresenceDetectionValue}
 *
 * @author David Graeff - Initial contribution
 */
public class DiscoveryTest {
    private final String ip = "127.0.0.1";

    @Mock
    PresenceDetectionValue value;

    @Mock
    DiscoveryListener listener;

    @Test
    public void pingDeviceDetected() {
        NetworkDiscoveryService d = new NetworkDiscoveryService();
        d.addDiscoveryListener(listener);
        ArgumentCaptor<DiscoveryResult> result = ArgumentCaptor.forClass(DiscoveryResult.class);
        // Ping device
        Mockito.when(value.isPingReachable()).thenReturn(true);
        Mockito.when(value.isTCPServiceReachable()).thenReturn(false);
        d.partialDetectionResult(value);
        Mockito.verify(listener).thingDiscovered(ArgumentMatchers.any(), result.capture());
        DiscoveryResult dresult = result.getValue();
        Assert.assertThat(dresult.getThingUID(), CoreMatchers.is(NetworkDiscoveryService.createPingUID(ip)));
        Assert.assertThat(dresult.getProperties().get(PARAMETER_HOSTNAME), CoreMatchers.is(ip));
    }

    @Test
    public void tcpDeviceDetected() {
        NetworkDiscoveryService d = new NetworkDiscoveryService();
        d.addDiscoveryListener(listener);
        ArgumentCaptor<DiscoveryResult> result = ArgumentCaptor.forClass(DiscoveryResult.class);
        // TCP device
        Mockito.when(value.isPingReachable()).thenReturn(false);
        Mockito.when(value.isTCPServiceReachable()).thenReturn(true);
        Mockito.when(value.getReachableTCPports()).thenReturn(Collections.singletonList(1010));
        d.partialDetectionResult(value);
        Mockito.verify(listener).thingDiscovered(ArgumentMatchers.any(), result.capture());
        DiscoveryResult dresult = result.getValue();
        Assert.assertThat(dresult.getThingUID(), CoreMatchers.is(NetworkDiscoveryService.createServiceUID(ip, 1010)));
        Assert.assertThat(dresult.getProperties().get(PARAMETER_HOSTNAME), CoreMatchers.is(ip));
        Assert.assertThat(dresult.getProperties().get(PARAMETER_PORT), CoreMatchers.is(1010));
    }
}

