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
package org.openhab.binding.tplinksmarthome.internal.handler;


import OnOffType.ON;
import RefreshType.REFRESH;
import ThingStatus.UNKNOWN;
import java.io.IOException;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerCallback;
import org.eclipse.smarthome.core.types.State;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openhab.binding.tplinksmarthome.internal.Commands;
import org.openhab.binding.tplinksmarthome.internal.Connection;
import org.openhab.binding.tplinksmarthome.internal.TPLinkSmartHomeDiscoveryService;
import org.openhab.binding.tplinksmarthome.internal.device.SmartHomeDevice;
import org.openhab.binding.tplinksmarthome.internal.model.ModelTestUtil;


/**
 * Tests cases for {@link SmartHomeHandler} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class SmartHomeHandlerTest {
    private static final String CHANNEL_PREFIX = "binding:tplinksmarthome:1234:";

    private SmartHomeHandler handler;

    @Mock
    private Connection connection;

    @Mock
    private ThingHandlerCallback callback;

    @Mock
    private Thing thing;

    @Mock
    private SmartHomeDevice smartHomeDevice;

    @Mock
    private TPLinkSmartHomeDiscoveryService discoveryService;

    @NonNull
    private final Configuration configuration = new Configuration();

    @Test
    public void testInitializeShouldCallTheCallback() throws InterruptedException {
        handler.initialize();
        ArgumentCaptor<ThingStatusInfo> statusInfoCaptor = ArgumentCaptor.forClass(ThingStatusInfo.class);
        Mockito.verify(callback).statusUpdated(ArgumentMatchers.eq(thing), statusInfoCaptor.capture());
        ThingStatusInfo thingStatusInfo = statusInfoCaptor.getValue();
        Assert.assertEquals("Device should be unknown", UNKNOWN, thingStatusInfo.getStatus());
    }

    @Test
    public void testHandleCommandRefreshType() {
        handler.initialize();
        assertHandleCommandRefreshType((-53));
    }

    @Test
    public void testHandleCommandRefreshTypeRangeExtender() throws IOException {
        Mockito.when(connection.sendCommand(Commands.getSysinfo())).thenReturn(ModelTestUtil.readJson("rangeextender_get_sysinfo_response"));
        handler.initialize();
        assertHandleCommandRefreshType((-70));
    }

    @Test
    public void testHandleCommandOther() throws InterruptedException {
        handler.initialize();
        ChannelUID channelUID = new ChannelUID(((SmartHomeHandlerTest.CHANNEL_PREFIX) + (CHANNEL_SWITCH)));
        Mockito.doReturn(ON).when(smartHomeDevice).updateChannel(ArgumentMatchers.eq(channelUID.getId()), ArgumentMatchers.any());
        handler.handleCommand(channelUID, REFRESH);
        ArgumentCaptor<State> stateCaptor = ArgumentCaptor.forClass(State.class);
        Mockito.verify(callback).stateUpdated(ArgumentMatchers.eq(channelUID), stateCaptor.capture());
        Assert.assertSame("State of channel switch should be set", ON, stateCaptor.getValue());
    }

    @Test
    public void testRefreshChannels() {
        handler.initialize();
        handler.refreshChannels();
    }
}

