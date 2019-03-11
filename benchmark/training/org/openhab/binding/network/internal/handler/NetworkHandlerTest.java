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
package org.openhab.binding.network.internal.handler;


import OnOffType.ON;
import ThingStatus.OFFLINE;
import ThingStatus.ONLINE;
import ThingStatusDetail.CONFIGURATION_ERROR;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.test.java.JavaTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.network.internal.NetworkBindingConfiguration;
import org.openhab.binding.network.internal.NetworkBindingConstants;
import org.openhab.binding.network.internal.PresenceDetection;
import org.openhab.binding.network.internal.PresenceDetectionValue;


/**
 * Tests cases for {@link NetworkHandler}.
 *
 * @author David Graeff - Initial contribution
 */
public class NetworkHandlerTest extends JavaTest {
    private ThingUID thingUID = new ThingUID("network", "ttype", "ping");

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Thing thing;

    @Test
    public void checkAllConfigurations() {
        NetworkBindingConfiguration config = new NetworkBindingConfiguration();
        NetworkHandler handler = Mockito.spy(new NetworkHandler(thing, true, config));
        handler.setCallback(callback);
        // Provide all possible configuration
        Mockito.when(thing.getConfiguration()).thenAnswer(( a) -> {
            Configuration conf = new Configuration();
            conf.put(NetworkBindingConstants.PARAMETER_RETRY, 10);
            conf.put(NetworkBindingConstants.PARAMETER_HOSTNAME, "127.0.0.1");
            conf.put(NetworkBindingConstants.PARAMETER_PORT, 8080);
            conf.put(NetworkBindingConstants.PARAMETER_REFRESH_INTERVAL, 101010);
            conf.put(NetworkBindingConstants.PARAMETER_TIMEOUT, 1234);
            return conf;
        });
        PresenceDetection presenceDetection = Mockito.spy(new PresenceDetection(handler, 2000));
        // Mock start/stop automatic refresh
        Mockito.doNothing().when(presenceDetection).startAutomaticRefresh(ArgumentMatchers.any());
        Mockito.doNothing().when(presenceDetection).stopAutomaticRefresh();
        handler.initialize(presenceDetection);
        Assert.assertThat(handler.retries, CoreMatchers.is(10));
        Assert.assertThat(presenceDetection.getHostname(), CoreMatchers.is("127.0.0.1"));
        Assert.assertThat(presenceDetection.getServicePorts().iterator().next(), CoreMatchers.is(8080));
        Assert.assertThat(presenceDetection.getRefreshInterval(), CoreMatchers.is(101010L));
        Assert.assertThat(presenceDetection.getTimeout(), CoreMatchers.is(1234));
    }

    @Test
    public void tcpDeviceInitTests() {
        NetworkBindingConfiguration config = new NetworkBindingConfiguration();
        NetworkHandler handler = Mockito.spy(new NetworkHandler(thing, true, config));
        Assert.assertThat(handler.isTCPServiceDevice(), CoreMatchers.is(true));
        handler.setCallback(callback);
        // Port is missing, should make the device OFFLINE
        Mockito.when(thing.getConfiguration()).thenAnswer(( a) -> {
            Configuration conf = new Configuration();
            conf.put(NetworkBindingConstants.PARAMETER_HOSTNAME, "127.0.0.1");
            return conf;
        });
        handler.initialize(new PresenceDetection(handler, 2000));
        // Check that we are offline
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), statusInfoCaptor.capture());
        Assert.assertThat(statusInfoCaptor.getValue().getStatus(), CoreMatchers.is(CoreMatchers.equalTo(OFFLINE)));
        Assert.assertThat(statusInfoCaptor.getValue().getStatusDetail(), CoreMatchers.is(CoreMatchers.equalTo(CONFIGURATION_ERROR)));
    }

    @Test
    public void pingDeviceInitTests() {
        NetworkBindingConfiguration config = new NetworkBindingConfiguration();
        NetworkHandler handler = Mockito.spy(new NetworkHandler(thing, false, config));
        handler.setCallback(callback);
        // Provide minimal configuration
        Mockito.when(thing.getConfiguration()).thenAnswer(( a) -> {
            Configuration conf = new Configuration();
            conf.put(NetworkBindingConstants.PARAMETER_HOSTNAME, "127.0.0.1");
            return conf;
        });
        PresenceDetection presenceDetection = Mockito.spy(new PresenceDetection(handler, 2000));
        // Mock start/stop automatic refresh
        Mockito.doNothing().when(presenceDetection).startAutomaticRefresh(ArgumentMatchers.any());
        Mockito.doNothing().when(presenceDetection).stopAutomaticRefresh();
        handler.initialize(presenceDetection);
        // Check that we are online
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), statusInfoCaptor.capture());
        Assert.assertEquals(ONLINE, statusInfoCaptor.getValue().getStatus());
        // Mock result value
        PresenceDetectionValue value = Mockito.mock(PresenceDetectionValue.class);
        Mockito.when(value.getLowestLatency()).thenReturn(10.0);
        Mockito.when(value.isReachable()).thenReturn(true);
        Mockito.when(value.getSuccessfulDetectionTypes()).thenReturn("TESTMETHOD");
        // Partial result from the PresenceDetection object should affect the
        // ONLINE and LATENCY channel
        handler.partialDetectionResult(value);
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(thingUID, NetworkBindingConstants.CHANNEL_ONLINE)), ArgumentMatchers.eq(ON));
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(thingUID, NetworkBindingConstants.CHANNEL_LATENCY)), ArgumentMatchers.eq(new DecimalType(10.0)));
        // Final result affects the LASTSEEN channel
        Mockito.when(value.isFinished()).thenReturn(true);
        handler.finalDetectionResult(value);
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.eq(new org.eclipse.smarthome.core.thing.ChannelUID(thingUID, NetworkBindingConstants.CHANNEL_LASTSEEN)), ArgumentMatchers.any());
    }
}

