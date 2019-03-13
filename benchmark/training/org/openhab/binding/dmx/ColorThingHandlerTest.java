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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.junit.Test;
import org.openhab.binding.dmx.internal.handler.ColorThingHandler;


/**
 * Tests cases for {@link DimmerThingHandler} in RGB mode.
 *
 * @author Jan N. Klug - Initial contribution
 */
public class ColorThingHandlerTest extends AbstractDmxThingTest {
    private static final String TEST_CHANNEL_CONFIG = "100/3";

    private static final int TEST_FADE_TIME = 1500;

    private static final HSBType TEST_COLOR = new HSBType(new DecimalType(280), new PercentType(100), new PercentType(100));

    private Map<String, Object> thingProperties;

    private Thing dimmerThing;

    private ColorThingHandler dimmerThingHandler;

    private final ThingUID THING_UID_DIMMER = new ThingUID(THING_TYPE_COLOR, "testdimmer");

    private final ChannelUID CHANNEL_UID_COLOR = new ChannelUID(THING_UID_DIMMER, CHANNEL_COLOR);

    private final ChannelUID CHANNEL_UID_BRIGHTNESS_R = new ChannelUID(THING_UID_DIMMER, CHANNEL_BRIGHTNESS_R);

    private final ChannelUID CHANNEL_UID_BRIGHTNESS_G = new ChannelUID(THING_UID_DIMMER, CHANNEL_BRIGHTNESS_G);

    private final ChannelUID CHANNEL_UID_BRIGHTNESS_B = new ChannelUID(THING_UID_DIMMER, CHANNEL_BRIGHTNESS_B);

    @Test
    public void testThingStatus() {
        assertThingStatus(dimmerThing);
    }

    @Test
    public void testThingStatus_noBridge() {
        // check that thing is offline if no bridge found
        ColorThingHandler dimmerHandlerWithoutBridge = new ColorThingHandler(dimmerThing) {
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
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, ON);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertEquals(OnOffType.ON, state.as(.class)));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_R, ( state) -> assertEquals(PercentType.HUNDRED, state));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_G, ( state) -> assertThat(((PercentType) (state)).doubleValue(), is(closeTo(50.0, 0.5))));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_B, ( state) -> assertEquals(PercentType.ZERO, state));
        });
        // off
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, OFF);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertEquals(OnOffType.OFF, state.as(.class)));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_R, ( state) -> assertEquals(PercentType.ZERO, state));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_G, ( state) -> assertEquals(PercentType.ZERO, state));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_B, ( state) -> assertEquals(PercentType.ZERO, state));
        });
    }

    @Test
    public void testDynamicTurnOnValue() {
        long currentTime = System.currentTimeMillis();
        // turn on with arbitrary value
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, ColorThingHandlerTest.TEST_COLOR);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getHue().doubleValue(), is(closeTo(280, 2))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getSaturation().doubleValue(), is(closeTo(100.0, 1))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getBrightness().doubleValue(), is(closeTo(100.0, 1))));
        });
        // turn off and hopefully store last value
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, OFF);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getBrightness().doubleValue(), is(closeTo(0.0, 1))));
        });
        // turn on and restore value
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, ON);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getHue().doubleValue(), is(closeTo(280, 2))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getSaturation().doubleValue(), is(closeTo(100.0, 1))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getBrightness().doubleValue(), is(closeTo(100.0, 1))));
        });
    }

    @Test
    public void testPercentTypeCommand() {
        assertPercentTypeCommands(dimmerThingHandler, CHANNEL_UID_COLOR, ColorThingHandlerTest.TEST_FADE_TIME);
    }

    @Test
    public void testColorCommand() {
        // setting of color
        long currentTime = System.currentTimeMillis();
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, ColorThingHandlerTest.TEST_COLOR);
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getHue().doubleValue(), is(closeTo(280, 1))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getSaturation().doubleValue(), is(closeTo(100.0, 0.5))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getBrightness().doubleValue(), is(closeTo(100.0, 0.5))));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_R, ( state) -> assertThat(((PercentType) (state)).doubleValue(), is(closeTo(66.5, 0.5))));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_G, ( state) -> assertEquals(PercentType.ZERO, state));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_B, ( state) -> assertEquals(PercentType.HUNDRED, state));
        });
        // color dimming
        dimmerThingHandler.handleCommand(CHANNEL_UID_COLOR, new PercentType(30));
        currentTime = dmxBridgeHandler.calcBuffer(currentTime, ColorThingHandlerTest.TEST_FADE_TIME);
        waitForAssert(() -> {
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getHue().doubleValue(), is(closeTo(280, 2))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getSaturation().doubleValue(), is(closeTo(100.0, 1))));
            assertChannelStateUpdate(CHANNEL_UID_COLOR, ( state) -> assertThat(((HSBType) (state)).getBrightness().doubleValue(), is(closeTo(30.0, 1))));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_R, ( state) -> assertThat(((PercentType) (state)).doubleValue(), is(closeTo(19.2, 0.5))));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_G, ( state) -> assertEquals(PercentType.ZERO, state));
            assertChannelStateUpdate(CHANNEL_UID_BRIGHTNESS_B, ( state) -> assertThat(((PercentType) (state)).doubleValue(), is(closeTo(29.8, 0.5))));
        });
    }
}

