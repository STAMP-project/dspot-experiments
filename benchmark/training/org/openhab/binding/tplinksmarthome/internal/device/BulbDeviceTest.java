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
package org.openhab.binding.tplinksmarthome.internal.device;


import OnOffType.OFF;
import OnOffType.ON;
import PercentType.ZERO;
import UnDefType.UNDEF;
import java.io.IOException;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.tplinksmarthome.internal.model.ModelTestUtil;


/**
 * Test class for {@link BulbDevice} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class BulbDeviceTest extends DeviceTestBase {
    private static final String DEVICE_OFF = "bulb_get_sysinfo_response_off";

    private final BulbDevice device = new BulbDevice(new org.eclipse.smarthome.core.thing.ThingTypeUID(BINDING_ID, "lb130"), COLOR_TEMPERATURE_LB130_MIN, COLOR_TEMPERATURE_LB130_MAX);

    public BulbDeviceTest() throws IOException {
        super("bulb_get_sysinfo_response_on");
    }

    @Test
    public void testHandleCommandBrightness() throws IOException {
        assertInput("bulb_transition_light_state_brightness");
        Assert.assertTrue("Brightness channel should be handled", device.handleCommand(CHANNEL_BRIGHTNESS, connection, new PercentType(33), configuration));
    }

    @Test
    public void testHandleCommandBrightnessOnOff() throws IOException {
        assertInput("bulb_transition_light_state_on");
        Assert.assertTrue("Brightness channel with OnOff state should be handled", device.handleCommand(CHANNEL_BRIGHTNESS, connection, ON, configuration));
    }

    @Test
    public void testHandleCommandColor() throws IOException {
        assertInput("bulb_transition_light_state_color");
        Assert.assertTrue("Color channel should be handled", device.handleCommand(CHANNEL_COLOR, connection, new HSBType("55,44,33"), configuration));
    }

    @Test
    public void testHandleCommandColorTemperature() throws IOException {
        assertInput("bulb_transition_light_state_color_temp");
        Assert.assertTrue("Color temperature channel should be handled", device.handleCommand(CHANNEL_COLOR_TEMPERATURE, connection, new PercentType(40), configuration));
    }

    @Test
    public void testHandleCommandColorTemperatureOnOff() throws IOException {
        assertInput("bulb_transition_light_state_on");
        Assert.assertTrue("Color temperature channel with OnOff state should be handled", device.handleCommand(CHANNEL_COLOR_TEMPERATURE, connection, ON, configuration));
    }

    @Test
    public void testHandleCommandSwitch() throws IOException {
        assertInput("bulb_transition_light_state_on");
        Assert.assertTrue("Switch channel should be handled", device.handleCommand(CHANNEL_SWITCH, connection, ON, configuration));
    }

    @Test
    public void testUpdateChannelBrightnessOn() {
        Assert.assertEquals("Brightness should be on", new PercentType(92), device.updateChannel(CHANNEL_BRIGHTNESS, deviceState));
    }

    @Test
    public void testUpdateChannelBrightnessOff() throws IOException {
        deviceState = new DeviceState(ModelTestUtil.readJson(BulbDeviceTest.DEVICE_OFF));
        Assert.assertEquals("Brightness should be off", ZERO, device.updateChannel(CHANNEL_BRIGHTNESS, deviceState));
    }

    @Test
    public void testUpdateChannelColorOn() {
        Assert.assertEquals("Color should be on", new HSBType("7,44,92"), device.updateChannel(CHANNEL_COLOR, deviceState));
    }

    @Test
    public void testUpdateChannelColorOff() throws IOException {
        deviceState = new DeviceState(ModelTestUtil.readJson(BulbDeviceTest.DEVICE_OFF));
        Assert.assertEquals("Color should be off", new HSBType("7,44,0"), device.updateChannel(CHANNEL_COLOR, deviceState));
    }

    @Test
    public void testUpdateChannelSwitchOn() {
        Assert.assertSame("Switch should be on", ON, device.updateChannel(CHANNEL_SWITCH, deviceState));
    }

    @Test
    public void testUpdateChannelSwitchOff() throws IOException {
        deviceState = new DeviceState(ModelTestUtil.readJson(BulbDeviceTest.DEVICE_OFF));
        Assert.assertSame("Switch should be off", OFF, device.updateChannel(CHANNEL_SWITCH, deviceState));
    }

    @Test
    public void testUpdateChannelColorTemperature() {
        Assert.assertEquals("Color temperature should be set", new PercentType(3), device.updateChannel(CHANNEL_COLOR_TEMPERATURE, deviceState));
    }

    @Test
    public void testUpdateChannelOther() {
        Assert.assertSame("Unknown channel should return UNDEF", UNDEF, device.updateChannel("OTHER", deviceState));
    }

    @Test
    public void testUpdateChannelPower() {
        Assert.assertEquals("Power values should be set", new DecimalType(10.8), device.updateChannel(CHANNEL_ENERGY_POWER, deviceState));
    }
}

