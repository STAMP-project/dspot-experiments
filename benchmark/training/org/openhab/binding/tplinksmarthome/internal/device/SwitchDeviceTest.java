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


import OnOffType.ON;
import UnDefType.UNDEF;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for {@link SwitchDevice} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
public class SwitchDeviceTest extends DeviceTestBase {
    private final SwitchDevice device = new SwitchDevice();

    public SwitchDeviceTest() throws IOException {
        super("plug_get_sysinfo_response");
    }

    @Test
    public void testHandleCommandSwitch() throws IOException {
        assertInput("plug_set_relay_state_on");
        setSocketReturnAssert("plug_set_relay_state_on");
        Assert.assertTrue("Switch channel should be handled", device.handleCommand(CHANNEL_SWITCH, connection, ON, configuration));
    }

    @Test
    public void testHandleCommandLed() throws IOException {
        assertInput("plug_set_led_on");
        setSocketReturnAssert("plug_set_led_on");
        Assert.assertTrue("Led channel should be handled", device.handleCommand(CHANNEL_LED, connection, ON, configuration));
    }

    @Test
    public void testUpdateChannelSwitch() {
        Assert.assertSame("Switch should be on", ON, device.updateChannel(CHANNEL_SWITCH, deviceState));
    }

    @Test
    public void testUpdateChannelLed() {
        Assert.assertSame("Led should be on", ON, device.updateChannel(CHANNEL_LED, deviceState));
    }

    @Test
    public void testUpdateChannelOther() {
        Assert.assertSame("Unknown channel should return UNDEF", UNDEF, device.updateChannel("OTHER", deviceState));
    }
}

