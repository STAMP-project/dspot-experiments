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
package org.openhab.binding.max.internal.message;


import MessageType.L;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.max.internal.device.Device;
import org.openhab.binding.max.internal.device.DeviceConfiguration;


/**
 * Tests cases for {@link LMessage}.
 *
 * @author Dominic Lerbs - Initial contribution
 * @author Christoph Weitkamp - OH2 Version and updates
 */
public class LMessageTest {
    private static final String rawData = "L:BgVPngkSEAsLhBkJEhkLJQDAAAsLhwwJEhkRJwDKAAYO8ZIJEhAGBU+kCRIQCwxuRPEaGQMmAMcACwxuQwkSGQgnAM8ACwQd5t0SGQ0oAMsA";

    private final Map<String, Device> testDevices = new HashMap<>();

    private LMessage message;

    private final List<DeviceConfiguration> configurations = new ArrayList<>();

    @Test
    public void isCorrectMessageType() {
        MessageType messageType = getType();
        Assert.assertEquals(L, messageType);
    }

    @Test
    public void allDevicesCreatedFromMessage() {
        Collection<? extends Device> devices = message.getDevices(configurations);
        Assert.assertEquals("Incorrect number of devices created", testDevices.size(), devices.size());
        for (Device device : devices) {
            Assert.assertTrue(("Unexpected device created: " + (device.getRFAddress())), testDevices.containsKey(device.getRFAddress()));
        }
    }

    @Test
    public void isCorrectErrorState() {
        for (Device device : message.getDevices(configurations)) {
            Device testDevice = testDevices.get(device.getRFAddress());
            Assert.assertEquals("Error set incorrectly in Device", testDevice.isError(), device.isError());
        }
    }
}

