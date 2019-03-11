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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.tplinksmarthome.internal.TPLinkSmartHomeBindingConstants;
import org.openhab.binding.tplinksmarthome.internal.model.ModelTestUtil;


/**
 * Test class for {@link DimmerDevice}.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class DimmerDeviceTest extends DeviceTestBase {
    private static final PercentType BRIGHTNESS_VALUE = new PercentType(50);

    private final DimmerDevice device = new DimmerDevice();

    public DimmerDeviceTest() throws IOException {
        super("hs220_get_sysinfo_response_on");
    }

    @Test
    public void testHandleCommandBrightnessOnOff() throws IOException {
        assertInput("dimmer_set_switch_state_on");
        setSocketReturnAssert("dimmer_set_switch_state_response");
        Assert.assertTrue("Brightness channel as OnOffType type should be handled", device.handleCommand(TPLinkSmartHomeBindingConstants.CHANNEL_BRIGHTNESS, connection, ON, configuration));
    }

    @Test
    public void testHandleCommandBrightnessZero() throws IOException {
        assertInput("dimmer_set_switch_state_off");
        setSocketReturnAssert("dimmer_set_switch_state_response");
        Assert.assertTrue("Brightness channel with percentage 0 should be handled", device.handleCommand(TPLinkSmartHomeBindingConstants.CHANNEL_BRIGHTNESS, connection, ZERO, configuration));
    }

    @Test
    public void testHandleCommandBrightness() throws IOException {
        assertInput("dimmer_set_brightness");
        setSocketReturnAssert("dimmer_set_brightness_response");
        Assert.assertTrue("Brightness channel should be handled", device.handleCommand(TPLinkSmartHomeBindingConstants.CHANNEL_BRIGHTNESS, connection, new PercentType(17), configuration));
    }

    @Test
    public void testUpdateChannelSwitch() throws IOException {
        deviceState = new DeviceState(ModelTestUtil.readJson("hs220_get_sysinfo_response_off"));
        Assert.assertSame("Dimmer device should be off", OFF, as(OnOffType.class));
    }

    @Test
    public void testUpdateChannelBrightness() {
        Assert.assertEquals("Dimmer brightness should be set", DimmerDeviceTest.BRIGHTNESS_VALUE, device.updateChannel(TPLinkSmartHomeBindingConstants.CHANNEL_BRIGHTNESS, deviceState));
    }

    @Test
    public void testUpdateChannelOther() {
        Assert.assertSame("Unknown channel should return UNDEF", UNDEF, device.updateChannel("OTHER", deviceState));
    }
}

