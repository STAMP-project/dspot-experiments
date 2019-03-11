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


import UnDefType.UNDEF;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.types.State;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openhab.binding.tplinksmarthome.internal.model.ModelTestUtil;


/**
 * Test class for {@link EnergySwitchDevice} class.
 *
 * @author Hilbrand Bouwkamp - Initial contribution
 */
@RunWith(Parameterized.class)
public class EnergySwitchDeviceTest {
    private static final List<Object[]> TESTS = Arrays.asList(new Object[][]{ new Object[]{ "plug_get_realtime_response" }, new Object[]{ "plug_get_realtime_response_v2" } });

    private final EnergySwitchDevice device = new EnergySwitchDevice();

    @NonNull
    private final DeviceState deviceState;

    public EnergySwitchDeviceTest(String name) throws IOException {
        deviceState = new DeviceState(ModelTestUtil.readJson(name));
    }

    @Test
    public void testUpdateChannelEnergyCurrent() {
        Assert.assertEquals("Energy current should have valid state value", 1, intValue());
    }

    @Test
    public void testUpdateChannelEnergyTotal() {
        Assert.assertEquals("Energy total should have valid state value", 10, intValue());
    }

    @Test
    public void testUpdateChannelEnergyVoltage() {
        State state = device.updateChannel(CHANNEL_ENERGY_VOLTAGE, deviceState);
        Assert.assertEquals("Energy voltage should have valid state value", 230, intValue());
        Assert.assertEquals("Channel patten to display as int", "230 V", state.format("%.0f V"));
    }

    @Test
    public void testUpdateChanneEnergyPowerl() {
        Assert.assertEquals("Energy power should have valid state value", 20, intValue());
    }

    @Test
    public void testUpdateChannelOther() {
        Assert.assertSame("Unknown channel should return UNDEF", UNDEF, device.updateChannel("OTHER", deviceState));
    }
}

