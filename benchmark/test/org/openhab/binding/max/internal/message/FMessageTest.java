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


import MessageType.F;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases for {@link FMessage}.
 *
 * @author Marcel Verpaalen - Initial contribution
 */
public class FMessageTest {
    public static final String RAW_DATA = "F:nl.ntp.pool.org,ntp.homematic.com";

    private FMessage message;

    @Test
    public void getMessageTypeTest() {
        MessageType messageType = getType();
        Assert.assertEquals(F, messageType);
    }

    @Test
    public void getServer1Test() {
        String ntpServer1 = message.getNtpServer1();
        Assert.assertEquals("nl.ntp.pool.org", ntpServer1);
    }

    @Test
    public void getServer2Test() {
        String ntpServer1 = message.getNtpServer2();
        Assert.assertEquals("ntp.homematic.com", ntpServer1);
    }
}

