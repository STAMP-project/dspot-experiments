package com.pi4j.component.potentiometer.microchip.impl;


import com.pi4j.io.i2c.I2CDevice;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/* #%L
**********************************************************************
ORGANIZATION  :  Pi4J
PROJECT       :  Pi4J :: Device Abstractions
FILENAME      :  MicrochipPotentiometerDeviceControllerStaticTest.java

This file is part of the Pi4J project. More information about
this project can be found here:  https://www.pi4j.com/
**********************************************************************
%%
Copyright (C) 2012 - 2019 Pi4J
%%
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Lesser Public License for more details.

You should have received a copy of the GNU General Lesser Public
License along with this program.  If not, see
<http://www.gnu.org/licenses/lgpl-3.0.html>.
#L%
 */
/**
 * Test for controller for MCP45XX and MCP46XX ICs.
 *
 * @see com.pi4j.component.potentiometer.microchip.impl.MicrochipPotentiometerDeviceController
 * @author <a href="http://raspelikan.blogspot.co.at">Raspelikan</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class MicrochipPotentiometerDeviceControllerStaticTest {
    @Mock
    private I2CDevice i2cDevice;

    @Test
    public void testCreation() throws IOException {
        // wrong parameter
        try {
            new MicrochipPotentiometerDeviceController(null);
            Assert.fail(("Got no RuntimeException on constructing " + "a DeviceController using a null-i2cDevice"));
        } catch (RuntimeException e) {
            // expected expection
        }
        // correct parameter
        new MicrochipPotentiometerDeviceController(i2cDevice);
    }

    @Test
    public void testToString() throws IOException {
        Mockito.when(i2cDevice.toString()).thenReturn("I2CDeviceMock");
        String toString = new MicrochipPotentiometerDeviceController(i2cDevice).toString();
        Assert.assertNotNull("result of 'toString()' is null!", toString);
        Assert.assertEquals("Unexpected result from calling 'toString'!", ("com.pi4j.component.potentiometer.microchip.impl.MicrochipPotentiometerDeviceController{\n" + "  i2cDevice=\'I2CDeviceMock\'\n}"), toString);
    }

    @Test
    public void testEquals() throws IOException {
        final MicrochipPotentiometerDeviceController deviceController = new MicrochipPotentiometerDeviceController(i2cDevice);
        final MicrochipPotentiometerDeviceController copyDeviceController = new MicrochipPotentiometerDeviceController(i2cDevice);
        final I2CDevice otherI2cDevice = Mockito.mock(I2CDevice.class);
        final MicrochipPotentiometerDeviceController otherDeviceController = new MicrochipPotentiometerDeviceController(otherI2cDevice);
        Assert.assertNotEquals("'dc.equals(null)' returns true!", deviceController, null);
        Assert.assertEquals("'dc.equals(dc) returns false!", deviceController, deviceController);
        Assert.assertNotEquals("\'dc.equals(\"Test\")\' returns true!", deviceController, "Test");
        Assert.assertEquals("'dc.equals(copyOfDc)' returns false!", deviceController, copyDeviceController);
        Assert.assertNotEquals("'dc.equals(otherDc)' returns true!", deviceController, otherDeviceController);
    }
}

