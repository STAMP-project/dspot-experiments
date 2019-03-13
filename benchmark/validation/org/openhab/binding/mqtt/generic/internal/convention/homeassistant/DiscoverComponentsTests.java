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
package org.openhab.binding.mqtt.generic.internal.convention.homeassistant;


import com.google.gson.Gson;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.smarthome.io.transport.mqtt.MqttBrokerConnection;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.mqtt.generic.internal.convention.homeassistant.DiscoverComponents.ComponentDiscovered;
import org.openhab.binding.mqtt.generic.internal.handler.ThingChannelConstants;


/**
 * Tests the {@link DiscoverComponents} class.
 *
 * @author David Graeff - Initial contribution
 */
public class DiscoverComponentsTests extends JavaOSGiTest {
    @Mock
    MqttBrokerConnection connection;

    @Mock
    ComponentDiscovered discovered;

    @Test
    public void discoveryTimeTest() throws InterruptedException, ExecutionException, TimeoutException {
        // Create a scheduler
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        DiscoverComponents discover = Mockito.spy(new DiscoverComponents(ThingChannelConstants.testHomeAssistantThing, scheduler, null, new Gson()));
        discover.startDiscovery(connection, 50, new HaID("homeassistant", "object", "node", "component"), discovered).get(100, TimeUnit.MILLISECONDS);
    }
}

