/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.maxcube.internal.message;


import MessageType.L;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dominic Lerbs
 * @since 1.8.0
 */
public class L_MessageTest {
    private static final String rawData = "L:BgVPngkSEAsLhBkJEhkLJQDAAAsLhwwJEhkRJwDKAAYO8ZIJEhAGBU+kCRIQCwxuRPEaGQMmAMcACwxuQwkSGQgnAM8ACwQd5t0SGQ0oAMsA";

    private final Map<String, Device> testDevices = new HashMap<String, Device>();

    private L_Message message;

    private final List<Configuration> configurations = new ArrayList<Configuration>();

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
        Collection<? extends Device> devices = message.getDevices(configurations);
        for (Device device : devices) {
            Device testDevice = testDevices.get(device.getRFAddress());
            Assert.assertEquals("Error set incorrectly in Device", testDevice.isError(), device.isError());
        }
    }
}

