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
package org.openhab.binding.max.internal.command;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases for {@link FCommand}.
 *
 * @author Marcel Verpaalen - Initial contribution
 */
public class FCommandTest {
    @Test
    public void PrefixTest() {
        FCommand scmd = new FCommand();
        String commandStr = scmd.getCommandString();
        String prefix = commandStr.substring(0, 2);
        Assert.assertEquals("f:", prefix);
        Assert.assertEquals((("f:" + '\r') + '\n'), commandStr);
    }

    @Test
    public void BaseCommandTest() {
        FCommand scmd = new FCommand("ntp.homematic.com", "nl.ntp.pool.org");
        String commandStr = scmd.getCommandString();
        Assert.assertEquals((("f:ntp.homematic.com,nl.ntp.pool.org" + '\r') + '\n'), commandStr);
    }

    @Test
    public void FCommandNullTest() {
        FCommand scmd = new FCommand("ntp.homematic.com", null);
        String commandStr = scmd.getCommandString();
        Assert.assertEquals((("f:ntp.homematic.com" + '\r') + '\n'), commandStr);
        scmd = new FCommand(null, "nl.ntp.pool.org");
        commandStr = scmd.getCommandString();
        Assert.assertEquals((("f:nl.ntp.pool.org" + '\r') + '\n'), commandStr);
        scmd = new FCommand(null, null);
        commandStr = scmd.getCommandString();
        Assert.assertEquals((("f:" + '\r') + '\n'), commandStr);
    }
}

