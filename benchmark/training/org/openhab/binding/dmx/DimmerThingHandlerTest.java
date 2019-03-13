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


import OnOffType.OFF;
import OnOffType.ON;
import java.util.Map;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.junit.Test;
import org.openhab.binding.dmx.internal.handler.DimmerThingHandler;


/**
 * Tests cases for {@link DimmerThingHandler} in normal mode.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class DimmerThingHandlerTest extends AbstractDmxThingTest {
    private static final String TEST_CHANNEL_CONFIG = "100";

    private static final int TEST_FADE_TIME = 1500;

    private Map<String, Object> thingProperties;

    private Thing dimmerThing;

    private DimmerThingHandler dimmerThingHandler;

    private final ThingUID THING_UID_DIMMER = new ThingUID(THING_TYPE_DIMMER, "testdimmer");

    private final ChannelUID CHANNEL_UID_BRIGHTNESS = new ChannelUID(THING_UID_DIMMER, CHANNEL_BRIGHTNESS);

    @Test
    public void testThingStatus() {
        assertThingStatus(dimmerThing);
    }

    @Test
    public void testThingStatus_noBridge() {
        // check that thing is offline if no bridge found
        DimmerThingHandler dimmerHandlerWithoutBridge = new DimmerThingHandler(dimmerThing) {
            @Override
            @Nullable
            protected Bridge getBridge() {
                return null;
            }
        };
        assertThingStatusWithoutBridge(dimmerHandlerWithoutBridge);
    }

    @Test
    public void testOnOffCommand() {
        // on
        long currentTime = System.currentTimeMillis();
        dimmerThingHandler.handleCommand(CHANNEL_UID_BRIGHTNESS, ON);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, DimmerThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS, ( state) -> assertEquals(OnOffType.ON, state.as(.class)));
        });
        // off
        dimmerThingHandler.handleCommand(CHANNEL_UID_BRIGHTNESS, OFF);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, DimmerThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS, ( state) -> assertEquals(OnOffType.OFF, state.as(.class)));
        });
    }

    @Test
    public void testDynamicTurnOnValue() {
        long currentTime = System.currentTimeMillis();
        int testValue = 75;
        // turn on with arbitrary value
        dimmerThingHandler.handleCommand(CHANNEL_UID_BRIGHTNESS, new PercentType(testValue));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, DimmerThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS, ( state) -> assertThat(((PercentType) (state)).doubleValue(), is(closeTo(testValue, 1.0))));
        });
        // turn off and hopefully store last value
        dimmerThingHandler.handleCommand(CHANNEL_UID_BRIGHTNESS, OFF);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, DimmerThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS, ( state) -> assertEquals(OnOffType.OFF, state.as(.class)));
        });
        // turn on and restore value
        dimmerThingHandler.handleCommand(CHANNEL_UID_BRIGHTNESS, ON);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, DimmerThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS, ( state) -> assertThat(((PercentType) (state)).doubleValue(), is(closeTo(testValue, 1.0))));
        });
    }

    @Test
    public void testPercentTypeCommand() {
        assertPercentTypeCommands(dimmerThingHandler, CHANNEL_UID_BRIGHTNESS, DimmerThingHandlerTest.TEST_FADE_TIME);
    }
}

