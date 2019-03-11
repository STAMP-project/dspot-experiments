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
package org.openhab.binding.mqtt.internal.discovery;


import MqttBindingConstants.BRIDGE_TYPE_SYSTEMBROKER;
import java.util.List;
import javax.naming.ConfigurationException;
import org.eclipse.smarthome.config.discovery.DiscoveryListener;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.eclipse.smarthome.io.transport.mqtt.MqttService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests cases for {@link MqttServiceDiscoveryService}.
 *
 * @author David Graeff - Initial contribution
 */
public class ServiceDiscoveryServiceTest {
    @Mock
    private MqttService service;

    @Mock
    private DiscoveryListener discoverListener;

    @Test
    public void testDiscovery() throws ConfigurationException {
        // Setting the MqttService will enable the background scanner
        MqttServiceDiscoveryService d = new MqttServiceDiscoveryService();
        d.addDiscoveryListener(discoverListener);
        d.setMqttService(service);
        d.startScan();
        // We expect 3 discoveries. An embedded thing, a textual configured one, a non-textual one
        ArgumentCaptor<DiscoveryResult> discoveryCapture = ArgumentCaptor.forClass(DiscoveryResult.class);
        Mockito.verify(discoverListener, Mockito.times(2)).thingDiscovered(ArgumentMatchers.eq(d), discoveryCapture.capture());
        List<DiscoveryResult> discoveryResults = discoveryCapture.getAllValues();
        Assert.assertThat(discoveryResults.size(), CoreMatchers.is(2));
        Assert.assertThat(discoveryResults.get(0).getThingTypeUID(), CoreMatchers.is(BRIDGE_TYPE_SYSTEMBROKER));
        Assert.assertThat(discoveryResults.get(1).getThingTypeUID(), CoreMatchers.is(BRIDGE_TYPE_SYSTEMBROKER));
        // Add another thing
        d.brokerAdded("anotherone", new MqttBrokerConnection("tcp://123.123.123.123", null, false, null));
        discoveryCapture = ArgumentCaptor.forClass(DiscoveryResult.class);
        Mockito.verify(discoverListener, Mockito.times(3)).thingDiscovered(ArgumentMatchers.eq(d), discoveryCapture.capture());
        discoveryResults = discoveryCapture.getAllValues();
        Assert.assertThat(discoveryResults.size(), CoreMatchers.is(3));
        Assert.assertThat(discoveryResults.get(2).getThingTypeUID(), CoreMatchers.is(BRIDGE_TYPE_SYSTEMBROKER));
    }
}

