/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.lightwaverf.internal.command;


import org.junit.Assert;
import org.junit.Test;


public class LightwaveRfRequestHeatInfoTest {
    @Test
    public void test() throws Exception {
        String message = "105,!R3F*r\n";
        LightwaveRfRoomMessage command = new LightwaveRfHeatInfoRequest(message);
        Assert.assertEquals("3", command.getRoomId());
        Assert.assertEquals("105,!R3F*r\n", command.getLightwaveRfCommandString());
    }
}

