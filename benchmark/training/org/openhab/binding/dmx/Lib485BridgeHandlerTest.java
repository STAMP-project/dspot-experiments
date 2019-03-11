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
import org.openhab.binding.dmx.internal.handler.Lib485BridgeHandler;


/**
 * Tests cases for {@link Lib485BridgeHandler}.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class Lib485BridgeHandlerTest extends JavaTest {
    private static final String TEST_ADDRESS = "localhost";

    private final ThingUID BRIDGE_UID_LIB485 = new ThingUID(THING_TYPE_LIB485_BRIDGE, "lib485bridge");

    private final ChannelUID CHANNEL_UID_MUTE = new ChannelUID(BRIDGE_UID_LIB485, CHANNEL_MUTE);

    Map<String, Object> bridgeProperties;

    private Bridge bridge;

    private Lib485BridgeHandler bridgeHandler;

    @Test
    public void assertBridgeStatus() {
        waitForAssert(() -> assertEquals(ThingStatus.OFFLINE, bridge.getStatusInfo().getStatus()));
    }
}

