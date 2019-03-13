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


import DeviceType.HeatingThermostatPlus;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.binding.max.internal.device.DeviceConfiguration;
import org.openhab.binding.max.internal.device.DeviceType;


/**
 * Tests cases for {@link DeviceConfiguration}.
 *
 * @author Andreas Heil (info@aheil.de) - Initial contribution
 * @author Marcel Verpaalen - OH2 Version and updates
 */
public class ConfigurationTest {
    public static final String RAW_DATA = "C:0b0da3,0gsNowIBEABLRVEwNTQ0MjQyLCQ9CQcYAzAM/wBIYViRSP1ZFE0gTSBNIEUgRSBFIEUgRSBFIEhhWJFQ/VkVUSBRIFEgRSBFIEUgRSBFIEUgSFBYWkj+WRRNIE0gTSBFIEUgRSBFIEUgRSBIUFhaSP5ZFE0gTSBNIEUgRSBFIEUgRSBFIEhQWFpI/lkUTSBNIE0gRSBFIEUgRSBFIEUgSFBYWkj+WRRNIE0gTSBFIEUgRSBFIEUgRSBIUFhaSP5ZFE0gTSBNIEUgRSBFIEUgRSBFIA==";

    private CMessage message;

    private DeviceConfiguration configuration;

    @Test
    public void createTest() {
        Assert.assertNotNull(configuration);
    }

    @Test
    public void getRfAddressTest() {
        String rfAddress = configuration.getRFAddress();
        Assert.assertEquals("0b0da3", rfAddress);
    }

    @Test
    public void getDeviceTypeTest() {
        DeviceType deviceType = configuration.getDeviceType();
        Assert.assertEquals(HeatingThermostatPlus, deviceType);
    }

    @Test
    public void getSerialNumberTest() {
        String serialNumber = configuration.getSerialNumber();
        Assert.assertEquals("KEQ0544242", serialNumber);
    }
}

