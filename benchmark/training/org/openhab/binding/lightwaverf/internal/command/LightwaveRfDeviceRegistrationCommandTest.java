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


public class LightwaveRfDeviceRegistrationCommandTest {
    @Test
    public void test() throws Exception {
        String message = "10,!F*p\n";
        LightwaveRFCommand command = new LightwaveRfDeviceRegistrationCommand(message);
        Assert.assertEquals("010,!F*p\n", command.getLightwaveRfCommandString());
    }
}

