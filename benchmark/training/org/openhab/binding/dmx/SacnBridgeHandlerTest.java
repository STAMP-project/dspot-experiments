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
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.junit.Test;
import org.openhab.binding.dmx.internal.handler.SacnBridgeHandler;


/**
 * Tests cases for {@link SacnBridgeHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class SacnBridgeHandlerTest extends JavaOSGiTest {
    private static final String TEST_ADDRESS = "localhost";

    private static final int TEST_UNIVERSE = 1;

    private final ThingUID BRIDGE_UID_SACN = new ThingUID(THING_TYPE_SACN_BRIDGE, "sacnbridge");

    private final ChannelUID CHANNEL_UID_MUTE = new ChannelUID(BRIDGE_UID_SACN, CHANNEL_MUTE);

    Map<String, Object> bridgeProperties;

    private Bridge bridge;

    private SacnBridgeHandler bridgeHandler;

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
        bridgeProperties.replace(CONFIG_UNIVERSE, SacnBridgeHandlerTest.TEST_UNIVERSE);
        bridgeHandler.handleConfigurationUpdate(bridgeProperties);
        waitForAssert(() -> assertThat(bridgeHandler.getUniverseId(), is(TEST_UNIVERSE)));
    }
}

