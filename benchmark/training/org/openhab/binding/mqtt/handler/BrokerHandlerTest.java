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


import ThingStatus.ONLINE;
import java.util.concurrent.ScheduledExecutorService;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.io.transport.mqtt.MqttException;
import org.eclipse.smarthome.io.transport.mqtt.MqttService;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.service.cm.ConfigurationException;


/**
 * Test cases for {@link BrokerHandler}.
 *
 * @author David Graeff - Initial contribution
 */
public class BrokerHandlerTest {
    private ScheduledExecutorService scheduler;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Bridge thing;

    @Mock
    private MqttService service;

    private MqttBrokerConnectionEx connection;

    private BrokerHandler handler;

    @Test(expected = IllegalArgumentException.class)
    public void handlerInitWithoutUrl() throws IllegalArgumentException, InterruptedException, MqttException, ConfigurationException {
        // Assume it is a real handler and not a mock as defined above
        handler = new BrokerHandler(thing);
        Assert.assertThat(initializeHandlerWaitForTimeout(), CoreMatchers.is(true));
    }

    @Test
    public void createBrokerConnection() {
        Configuration config = new Configuration();
        config.put("host", "10.10.0.10");
        config.put("port", 80);
        Mockito.when(thing.getConfiguration()).thenReturn(config);
        handler.initialize();
        Mockito.verify(handler).createBrokerConnection();
    }

    @Test
    public void handlerInit() throws IllegalArgumentException, InterruptedException, MqttException, ConfigurationException {
        Assert.assertThat(initializeHandlerWaitForTimeout(), CoreMatchers.is(true));
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        Mockito.verify(callback, Mockito.atLeast(3)).statusUpdated(ArgumentMatchers.eq(thing), statusInfoCaptor.capture());
        Assert.assertThat(statusInfoCaptor.getValue().getStatus(), CoreMatchers.is(ONLINE));
    }
}

