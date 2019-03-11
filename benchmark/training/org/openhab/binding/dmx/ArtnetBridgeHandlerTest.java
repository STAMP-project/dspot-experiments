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
package org.openhab.binding.dmx;


import java.util.Map;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.test.java.JavaTest;
import org.junit.Test;
import org.openhab.binding.dmx.internal.handler.ArtnetBridgeHandler;


/**
 * Tests cases for {@link ArtnetBridgeHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class ArtnetBridgeHandlerTest extends JavaTest {
    private static final String TEST_ADDRESS = "localhost";

    private static final int TEST_UNIVERSE = 1;

    private final ThingUID BRIDGE_UID_ARTNET = new ThingUID(THING_TYPE_ARTNET_BRIDGE, "artnetbridge");

    private final ChannelUID CHANNEL_UID_MUTE = new ChannelUID(BRIDGE_UID_ARTNET, CHANNEL_MUTE);

    Map<String, Object> bridgeProperties;

    private Bridge bridge;

    private ArtnetBridgeHandler bridgeHandler;

    @Test
    public void assertBridgeStatus() {
        waitForAssert(() -> assertEquals(ThingStatus.ONLINE, bridge.getStatusInfo().getStatus()));
    }

    @Test
    public void renamingOfUniverses() {
        waitForAssert(() -> assertThat(bridgeHandler.getUniverseId(), is(TEST_UNIVERSE)));
        bridgeProperties.replace(CONFIG_UNIVERSE, 2);
        bridgeHandler.handleConfigurationUpdate(bridgeProperties);
        waitForAssert(() -> assertThat(bridgeHandler.getUniverseId(), is(2)));
        bridgeProperties.replace(CONFIG_UNIVERSE, ArtnetBridgeHandlerTest.TEST_UNIVERSE);
        bridgeHandler.handleConfigurationUpdate(bridgeProperties);
        waitForAssert(() -> assertThat(bridgeHandler.getUniverseId(), is(TEST_UNIVERSE)));
    }
}

