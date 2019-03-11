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
package org.openhab.binding.mqtt.handler;


import MqttConnectionState.CONNECTED;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.eclipse.smarthome.io.transport.mqtt.MqttException;
import org.eclipse.smarthome.io.transport.mqtt.MqttService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.service.cm.ConfigurationException;


/**
 * Tests cases for {@link AbstractBrokerHandler}.
 *
 * @author David Graeff - Initial contribution
 */
public class AbstractBrokerHandlerTest {
    private final String HOST = "tcp://123.1.2.3";

    private final int PORT = 80;

    private SystemBrokerHandler handler;

    int stateChangeCounter = 0;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Bridge thing;

    @Mock
    private MqttService service;

    @Test
    public void brokerAddedWrongID() throws MqttException, ConfigurationException {
        MqttBrokerConnection brokerConnection = Mockito.mock(MqttBrokerConnection.class);
        Mockito.when(brokerConnection.connectionState()).thenReturn(CONNECTED);
        handler.brokerAdded("nonsense_id", brokerConnection);
        Assert.assertNull(handler.connection);
        // We do not expect a status change, because brokerAdded will do nothing with invalid connections.
        Mockito.verify(callback, Mockito.times(0)).statusUpdated(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void brokerRemovedBroker() throws MqttException, ConfigurationException {
        MqttBrokerConnectionEx connection = Mockito.spy(new MqttBrokerConnectionEx("10.10.0.10", 80, false, "BrokerHandlerTest"));
        handler.brokerAdded(handler.brokerID, connection);
        Assert.assertThat(handler.connection, CoreMatchers.is(connection));
        handler.brokerRemoved("something", connection);
        Assert.assertNull(handler.connection);
    }

    @Test
    public void brokerAdded() throws MqttException, ConfigurationException {
        MqttBrokerConnectionEx connection = Mockito.spy(new MqttBrokerConnectionEx("10.10.0.10", 80, false, "BrokerHandlerTest"));
        Mockito.doReturn(connection).when(service).getBrokerConnection(ArgumentMatchers.eq(handler.brokerID));
        Mockito.verify(callback, Mockito.times(0)).statusUpdated(ArgumentMatchers.any(), ArgumentMatchers.any());
        handler.brokerAdded(handler.brokerID, connection);
        Assert.assertThat(handler.connection, CoreMatchers.is(connection));
        start();
        // First connecting then connected and another connected after the future completes
        Mockito.verify(callback, Mockito.times(3)).statusUpdated(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

