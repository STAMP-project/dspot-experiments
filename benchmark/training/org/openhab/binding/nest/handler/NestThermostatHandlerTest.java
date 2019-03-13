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
package org.openhab.binding.nest.handler;


import ThingStatus.OFFLINE;
import ThingStatusDetail.GONE;
import java.io.IOException;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.nest.internal.data.NestDataUtil;
import org.openhab.binding.nest.internal.handler.NestThermostatHandler;


/**
 * Tests for {@link NestThermostatHandler}.
 *
 * @author Wouter Born - Increase test coverage
 */
public class NestThermostatHandlerTest extends NestThingHandlerOSGiTest {
    private static final ThingUID THERMOSTAT_UID = new ThingUID(THING_TYPE_THERMOSTAT, "thermostat1");

    private static final int CHANNEL_COUNT = 25;

    public NestThermostatHandlerTest() {
        super(NestThermostatHandler.class);
    }

    @Test
    public void completeThermostatCelsiusUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestThermostatHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME, CELSIUS));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatItemHasState(CHANNEL_CAN_COOL, OFF);
        assertThatItemHasState(CHANNEL_CAN_HEAT, ON);
        assertThatItemHasState(CHANNEL_ECO_MAX_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(24, CELSIUS));
        assertThatItemHasState(CHANNEL_ECO_MIN_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(12.5, CELSIUS));
        assertThatItemHasState(CHANNEL_FAN_TIMER_ACTIVE, OFF);
        assertThatItemHasState(CHANNEL_FAN_TIMER_DURATION, new org.eclipse.smarthome.core.library.types.QuantityType(15, SmartHomeUnits.MINUTE));
        assertThatItemHasState(CHANNEL_FAN_TIMER_TIMEOUT, NestThingHandlerOSGiTest.parseDateTimeType("1970-01-01T00:00:00.000Z"));
        assertThatItemHasState(CHANNEL_HAS_FAN, ON);
        assertThatItemHasState(CHANNEL_HAS_LEAF, ON);
        assertThatItemHasState(CHANNEL_HUMIDITY, new org.eclipse.smarthome.core.library.types.QuantityType(25, SmartHomeUnits.PERCENT));
        assertThatItemHasState(CHANNEL_LAST_CONNECTION, NestThingHandlerOSGiTest.parseDateTimeType("2017-02-02T21:00:06.000Z"));
        assertThatItemHasState(CHANNEL_LOCKED, OFF);
        assertThatItemHasState(CHANNEL_LOCKED_MAX_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(22, CELSIUS));
        assertThatItemHasState(CHANNEL_LOCKED_MIN_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(20, CELSIUS));
        assertThatItemHasState(CHANNEL_MAX_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(24, CELSIUS));
        assertThatItemHasState(CHANNEL_MIN_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(20, CELSIUS));
        assertThatItemHasState(CHANNEL_MODE, new StringType("HEAT"));
        assertThatItemHasState(CHANNEL_PREVIOUS_MODE, new StringType("HEAT"));
        assertThatItemHasState(CHANNEL_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(15.5, CELSIUS));
        assertThatItemHasState(CHANNEL_STATE, new StringType("OFF"));
        assertThatItemHasState(CHANNEL_SUNLIGHT_CORRECTION_ACTIVE, OFF);
        assertThatItemHasState(CHANNEL_SUNLIGHT_CORRECTION_ENABLED, ON);
        assertThatItemHasState(CHANNEL_TEMPERATURE, new org.eclipse.smarthome.core.library.types.QuantityType(19, CELSIUS));
        assertThatItemHasState(CHANNEL_TIME_TO_TARGET, new org.eclipse.smarthome.core.library.types.QuantityType(0, SmartHomeUnits.MINUTE));
        assertThatItemHasState(CHANNEL_USING_EMERGENCY_HEAT, OFF);
        assertThatAllItemStatesAreNotNull();
    }

    @Test
    public void completeThermostatFahrenheitUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestThermostatHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME, FAHRENHEIT));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatItemHasState(CHANNEL_CAN_COOL, OFF);
        assertThatItemHasState(CHANNEL_CAN_HEAT, ON);
        assertThatItemHasState(CHANNEL_ECO_MAX_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(76, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_ECO_MIN_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(55, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_FAN_TIMER_ACTIVE, OFF);
        assertThatItemHasState(CHANNEL_FAN_TIMER_DURATION, new org.eclipse.smarthome.core.library.types.QuantityType(15, SmartHomeUnits.MINUTE));
        assertThatItemHasState(CHANNEL_FAN_TIMER_TIMEOUT, NestThingHandlerOSGiTest.parseDateTimeType("1970-01-01T00:00:00.000Z"));
        assertThatItemHasState(CHANNEL_HAS_FAN, ON);
        assertThatItemHasState(CHANNEL_HAS_LEAF, ON);
        assertThatItemHasState(CHANNEL_HUMIDITY, new org.eclipse.smarthome.core.library.types.QuantityType(25, SmartHomeUnits.PERCENT));
        assertThatItemHasState(CHANNEL_LAST_CONNECTION, NestThingHandlerOSGiTest.parseDateTimeType("2017-02-02T21:00:06.000Z"));
        assertThatItemHasState(CHANNEL_LOCKED, OFF);
        assertThatItemHasState(CHANNEL_LOCKED_MAX_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(72, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_LOCKED_MIN_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(68, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_MAX_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(75, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_MIN_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(68, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_MODE, new StringType("HEAT"));
        assertThatItemHasState(CHANNEL_PREVIOUS_MODE, new StringType("HEAT"));
        assertThatItemHasState(CHANNEL_SET_POINT, new org.eclipse.smarthome.core.library.types.QuantityType(60, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_STATE, new StringType("OFF"));
        assertThatItemHasState(CHANNEL_SUNLIGHT_CORRECTION_ACTIVE, OFF);
        assertThatItemHasState(CHANNEL_SUNLIGHT_CORRECTION_ENABLED, ON);
        assertThatItemHasState(CHANNEL_TEMPERATURE, new org.eclipse.smarthome.core.library.types.QuantityType(66, FAHRENHEIT));
        assertThatItemHasState(CHANNEL_TIME_TO_TARGET, new org.eclipse.smarthome.core.library.types.QuantityType(0, SmartHomeUnits.MINUTE));
        assertThatItemHasState(CHANNEL_USING_EMERGENCY_HEAT, OFF);
        assertThatAllItemStatesAreNotNull();
    }

    @Test
    public void incompleteThermostatUpdate() throws IOException {
        Assert.assertThat(thing.getChannels().size(), Is.is(NestThermostatHandlerTest.CHANNEL_COUNT));
        Assert.assertThat(thing.getStatus(), Is.is(OFFLINE));
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatAllItemStatesAreNotNull();
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.INCOMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.UNKNOWN)));
        assertThatAllItemStatesAreNull();
    }

    @Test
    public void thermostatGone() throws IOException {
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.EMPTY_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.OFFLINE)));
        Assert.assertThat(thing.getStatusInfo().getStatusDetail(), Is.is(GONE));
    }

    @Test
    public void channelRefresh() throws IOException {
        waitForAssert(() -> assertThat(bridge.getStatus(), is(ThingStatus.ONLINE)));
        putStreamingEventData(NestDataUtil.fromFile(NestDataUtil.COMPLETE_DATA_FILE_NAME));
        waitForAssert(() -> assertThat(thing.getStatus(), is(ThingStatus.ONLINE)));
        assertThatAllItemStatesAreNotNull();
        updateAllItemStatesToNull();
        assertThatAllItemStatesAreNull();
        refreshAllChannels();
        assertThatAllItemStatesAreNotNull();
    }

    @Test
    public void handleFanTimerActiveCommands() throws IOException {
        handleCommand(CHANNEL_FAN_TIMER_ACTIVE, ON);
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "fan_timer_active", "true");
        handleCommand(CHANNEL_FAN_TIMER_ACTIVE, OFF);
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "fan_timer_active", "false");
        handleCommand(CHANNEL_FAN_TIMER_ACTIVE, ON);
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "fan_timer_active", "true");
    }

    @Test
    public void handleFanTimerDurationCommands() throws IOException {
        int[] durations = new int[]{ 15, 30, 45, 60, 120, 240, 480, 960, 15 };
        for (int duration : durations) {
            handleCommand(CHANNEL_FAN_TIMER_DURATION, new org.eclipse.smarthome.core.library.types.QuantityType(duration, SmartHomeUnits.MINUTE));
            assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "fan_timer_duration", String.valueOf(duration));
        }
    }

    @Test
    public void handleMaxSetPointCelsiusCommands() throws IOException {
        celsiusCommandsTest(CHANNEL_MAX_SET_POINT, "target_temperature_high_c");
    }

    @Test
    public void handleMaxSetPointFahrenheitCommands() throws IOException {
        fahrenheitCommandsTest(CHANNEL_MAX_SET_POINT, "target_temperature_high_f");
    }

    @Test
    public void handleMinSetPointCelsiusCommands() throws IOException {
        celsiusCommandsTest(CHANNEL_MIN_SET_POINT, "target_temperature_low_c");
    }

    @Test
    public void handleMinSetPointFahrenheitCommands() throws IOException {
        fahrenheitCommandsTest(CHANNEL_MIN_SET_POINT, "target_temperature_low_f");
    }

    @Test
    public void handleChannelModeCommands() throws IOException {
        handleCommand(CHANNEL_MODE, new StringType("HEAT"));
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "hvac_mode", "heat");
        handleCommand(CHANNEL_MODE, new StringType("COOL"));
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "hvac_mode", "cool");
        handleCommand(CHANNEL_MODE, new StringType("HEAT_COOL"));
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "hvac_mode", "heat-cool");
        handleCommand(CHANNEL_MODE, new StringType("ECO"));
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "hvac_mode", "eco");
        handleCommand(CHANNEL_MODE, new StringType("OFF"));
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "hvac_mode", "off");
        handleCommand(CHANNEL_MODE, new StringType("HEAT"));
        assertNestApiPropertyState(NestDataUtil.THERMOSTAT1_DEVICE_ID, "hvac_mode", "heat");
    }

    @Test
    public void handleSetPointCelsiusCommands() throws IOException {
        celsiusCommandsTest(CHANNEL_SET_POINT, "target_temperature_c");
    }

    @Test
    public void handleSetPointFahrenheitCommands() throws IOException {
        fahrenheitCommandsTest(CHANNEL_SET_POINT, "target_temperature_f");
    }
}

