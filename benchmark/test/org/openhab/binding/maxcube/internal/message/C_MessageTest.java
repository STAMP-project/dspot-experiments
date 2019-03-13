/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
package org.openhab.binding.maxcube.internal.message;


import DeviceType.HeatingThermostat;
import MessageType.C;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andreas Heil (info@aheil.de)
 * @since 1.4.0
 */
public class C_MessageTest {
    public final String rawData = "C:003508,0gA1CAEBFP9JRVEwMTA5MTI1KCg9CQcoAzAM/wBESFUIRSBFIEUgRSBFIEUgRSBFIEUgRSBFIERIVQhFIEUgRSBFIEUgRSBFIEUgRSBFIEUgREhUbETMVRRFIEUgRSBFIEUgRSBFIEUgRSBESFRsRMxVFEUgRSBFIEUgRSBFIEUgRSBFIERIUmxEzFUURSBFIEUgRSBFIEUgRSBFIEUgREhUbETMVRRFIEUgRSBFIEUgRSBFIEUgRSBESFRsRMxVFEUgRSBFIEUgRSBFIEUgRSBFIA==";

    private C_Message message = null;

    @Test
    public void getMessageTypeTest() {
        MessageType messageType = getType();
        Assert.assertEquals(C, messageType);
    }

    @Test
    public void getRFAddressTest() {
        String rfAddress = message.getRFAddress();
        Assert.assertEquals("003508", rfAddress);
    }

    @Test
    public void getDeviceTypeTest() {
        DeviceType deviceType = message.getDeviceType();
        Assert.assertEquals(HeatingThermostat, deviceType);
    }

    @Test
    public void getSerialNumberTes() {
        String serialNumber = message.getSerialNumber();
        Assert.assertEquals("IEQ0109125", serialNumber);
    }
}

