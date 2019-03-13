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
package org.openhab.binding.hue.internal.handler;


import ColorMode.XY;
import HSBType.BLACK;
import HSBType.BLUE;
import HSBType.GREEN;
import HSBType.RED;
import HSBType.WHITE;
import IncreaseDecreaseType.DECREASE;
import IncreaseDecreaseType.INCREASE;
import OnOffType.OFF;
import OnOffType.ON;
import com.google.gson.Gson;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.junit.Test;
import org.openhab.binding.hue.test.HueLightState;


/**
 * Tests for {@link HueLightHandler}.
 *
 * @author Oliver Libutzki - Initial contribution
 * @author Michael Grammling - Initial contribution
 * @author Markus Mazurczak - Added test for OSRAM Par16 50 TW bulbs
 * @author Andre Fuechsel - modified tests after introducing the generic thing types
 * @author Denis Dudnik - switched to internally integrated source of Jue library
 * @author Simon Kaufmann - migrated to plain Java test
 * @author Christoph Weitkamp - Added support for bulbs using CIE XY colormode only
 */
public class HueLightHandlerTest {
    private static final int MIN_COLOR_TEMPERATURE = 153;

    private static final int MAX_COLOR_TEMPERATURE = 500;

    private static final int COLOR_TEMPERATURE_RANGE = (HueLightHandlerTest.MAX_COLOR_TEMPERATURE) - (HueLightHandlerTest.MIN_COLOR_TEMPERATURE);

    private static final String OSRAM_MODEL_TYPE = "PAR16 50 TW";

    private static final String OSRAM_MODEL_TYPE_ID = "PAR16_50_TW";

    private Gson gson;

    @Test
    public void assertCommandForOsramPar16_50ForColorTemperatureChannelOn() {
        String expectedReply = "{\"on\" : true, \"bri\" : 254}";
        assertSendCommandForColorTempForPar16(ON, new HueLightState(HueLightHandlerTest.OSRAM_MODEL_TYPE), expectedReply);
    }

    @Test
    public void assertCommandForOsramPar16_50ForColorTemperatureChannelOff() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 0}";
        assertSendCommandForColorTempForPar16(OFF, new HueLightState(HueLightHandlerTest.OSRAM_MODEL_TYPE), expectedReply);
    }

    @Test
    public void assertCommandForOsramPar16_50ForBrightnessChannelOn() {
        String expectedReply = "{\"on\" : true, \"bri\" : 254}";
        assertSendCommandForBrightnessForPar16(ON, new HueLightState(HueLightHandlerTest.OSRAM_MODEL_TYPE), expectedReply);
    }

    @Test
    public void assertCommandForOsramPar16_50ForBrightnessChannelOff() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 0}";
        assertSendCommandForBrightnessForPar16(OFF, new HueLightState(HueLightHandlerTest.OSRAM_MODEL_TYPE), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelOn() {
        String expectedReply = "{\"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForColor(ON, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorTemperatureChannelOn() {
        String expectedReply = "{\"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForColorTemp(ON, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelOff() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForColor(OFF, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorTemperatureChannelOff() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForColorTemp(OFF, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorTemperatureChannel0Percent() {
        String expectedReply = "{\"ct\" : 153, \"transitiontime\" : 4}";
        assertSendCommandForColorTemp(new PercentType(0), new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorTemperatureChannel50Percent() {
        String expectedReply = "{\"ct\" : 327, \"transitiontime\" : 4}";
        assertSendCommandForColorTemp(new PercentType(50), new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorTemperatureChannel1000Percent() {
        String expectedReply = "{\"ct\" : 500, \"transitiontime\" : 4}";
        assertSendCommandForColorTemp(new PercentType(100), new HueLightState(), expectedReply);
    }

    @Test
    public void assertPercentageValueOfColorTemperatureWhenCt153() {
        int expectedReply = 0;
        asserttoColorTemperaturePercentType(153, expectedReply);
    }

    @Test
    public void assertPercentageValueOfColorTemperatureWhenCt326() {
        int expectedReply = 50;
        asserttoColorTemperaturePercentType(326, expectedReply);
    }

    @Test
    public void assertPercentageValueOfColorTemperatureWhenCt500() {
        int expectedReply = 100;
        asserttoColorTemperaturePercentType(500, expectedReply);
    }

    @Test
    public void assertCommandForColorChannel0Percent() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForColor(new PercentType(0), new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannel50Percent() {
        String expectedReply = "{\"bri\" : 127, \"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForColor(new PercentType(50), new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannel100Percent() {
        String expectedReply = "{\"bri\" : 254, \"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForColor(new PercentType(100), new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelBlack() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForColor(BLACK, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelRed() {
        String expectedReply = "{\"bri\" : 254, \"sat\" : 254, \"hue\" : 0, \"transitiontime\" : 4}";
        assertSendCommandForColor(RED, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelGreen() {
        String expectedReply = "{\"bri\" : 254, \"sat\" : 254, \"hue\" : 21845, \"transitiontime\" : 4}";
        assertSendCommandForColor(GREEN, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelBlue() {
        String expectedReply = "{\"bri\" : 254, \"sat\" : 254, \"hue\" : 43690, \"transitiontime\" : 4}";
        assertSendCommandForColor(BLUE, new HueLightState(), expectedReply);
    }

    @Test
    public void assertCommandForColorChannelWhite() {
        String expectedReply = "{\"bri\" : 254, \"sat\" : 0, \"hue\" : 0, \"transitiontime\" : 4}";
        assertSendCommandForColor(WHITE, new HueLightState(), expectedReply);
    }

    @Test
    public void assertXYCommandForColorChannelBlack() {
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForColor(BLACK, new HueLightState().colormode(XY), expectedReply);
    }

    @Test
    public void assertXYCommandForColorChannelWhite() {
        String expectedReply = "{\"xy\" : [ 0.31271592 , 0.32900152 ], \"bri\" : 254, \"transitiontime\" : 4}";
        assertSendCommandForColor(WHITE, new HueLightState().colormode(XY), expectedReply);
    }

    @Test
    public void assertXYCommandForColorChannelColorful() {
        String expectedReply = "{\"xy\" : [ 0.16969365 , 0.12379659 ], \"bri\" : 127, \"transitiontime\" : 4}";
        assertSendCommandForColor(new HSBType("220,90,50"), new HueLightState().colormode(XY), expectedReply);
    }

    @Test
    public void asserCommandForColorChannelIncrease() {
        HueLightState currentState = new HueLightState().bri(1).on(false);
        String expectedReply = "{\"bri\" : 30, \"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForColor(INCREASE, currentState, expectedReply);
        currentState.bri(200).on(true);
        expectedReply = "{\"bri\" : 230, \"transitiontime\" : 4}";
        assertSendCommandForColor(INCREASE, currentState, expectedReply);
        currentState.bri(230);
        expectedReply = "{\"bri\" : 254, \"transitiontime\" : 4}";
        assertSendCommandForColor(INCREASE, currentState, expectedReply);
    }

    @Test
    public void asserCommandForColorChannelDecrease() {
        HueLightState currentState = new HueLightState().bri(200);
        String expectedReply = "{\"bri\" : 170, \"transitiontime\" : 4}";
        assertSendCommandForColor(DECREASE, currentState, expectedReply);
        currentState.bri(20);
        expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForColor(DECREASE, currentState, expectedReply);
    }

    @Test
    public void assertCommandForBrightnessChannel50Percent() {
        HueLightState currentState = new HueLightState();
        String expectedReply = "{\"bri\" : 127, \"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(new PercentType(50), currentState, expectedReply);
    }

    @Test
    public void assertCommandForBrightnessChannelIncrease() {
        HueLightState currentState = new HueLightState().bri(1).on(false);
        String expectedReply = "{\"bri\" : 30, \"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(INCREASE, currentState, expectedReply);
        currentState.bri(200).on(true);
        expectedReply = "{\"bri\" : 230, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(INCREASE, currentState, expectedReply);
        currentState.bri(230);
        expectedReply = "{\"bri\" : 254, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(INCREASE, currentState, expectedReply);
    }

    @Test
    public void assertCommandForBrightnessChannelDecrease() {
        HueLightState currentState = new HueLightState().bri(200);
        String expectedReply = "{\"bri\" : 170, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(DECREASE, currentState, expectedReply);
        currentState.bri(20);
        expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(DECREASE, currentState, expectedReply);
    }

    @Test
    public void assertCommandForBrightnessChannelOff() {
        HueLightState currentState = new HueLightState();
        String expectedReply = "{\"on\" : false, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(OFF, currentState, expectedReply);
    }

    @Test
    public void assertCommandForBrightnessChannelOn() {
        HueLightState currentState = new HueLightState();
        String expectedReply = "{\"on\" : true, \"transitiontime\" : 4}";
        assertSendCommandForBrightness(ON, currentState, expectedReply);
    }

    @Test
    public void assertCommandForAlertChannel() {
        HueLightState currentState = new HueLightState().alert("NONE");
        String expectedReply = "{\"alert\" : \"none\"}";
        assertSendCommandForAlert(new StringType("NONE"), currentState, expectedReply);
        currentState.alert("NONE");
        expectedReply = "{\"alert\" : \"select\"}";
        assertSendCommandForAlert(new StringType("SELECT"), currentState, expectedReply);
        currentState.alert("LSELECT");
        expectedReply = "{\"alert\" : \"lselect\"}";
        assertSendCommandForAlert(new StringType("LSELECT"), currentState, expectedReply);
    }

    @Test
    public void assertCommandForEffectChannel() {
        HueLightState currentState = new HueLightState().effect("ON");
        String expectedReply = "{\"effect\" : \"colorloop\"}";
        assertSendCommandForEffect(ON, currentState, expectedReply);
        currentState.effect("OFF");
        expectedReply = "{\"effect\" : \"none\"}";
        assertSendCommandForEffect(OFF, currentState, expectedReply);
    }
}

