package com.pi4j.component.potentiometer.microchip.impl;


import DeviceControllerChannel.A;
import com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerChannel;
import com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerNonVolatileMode;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/* #%L
**********************************************************************
ORGANIZATION  :  Pi4J
PROJECT       :  Pi4J :: Device Abstractions
FILENAME      :  MicrochipPotentiometerPotentiometerImplStaticTest.java

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
 * Test for abstract Pi4J-device for MCP45XX and MCP46XX ICs.
 *
 * @see com.pi4j.component.potentiometer.microchip.impl.MicrochipPotentiometerBase
 * @author <a href="http://raspelikan.blogspot.co.at">Raspelikan</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class MicrochipPotentiometerPotentiometerImplStaticTest {
    @Mock
    private I2CDevice i2cDevice;

    @Mock
    private I2CBus i2cBus;

    @Mock
    private MicrochipPotentiometerDeviceController controller;

    @Mock
    private MicrochipPotentiometerDeviceControllerFactory controllerFactory;

    /**
     * publishes some internals for testing purposes
     */
    static class TestablePotentiometer extends MicrochipPotentiometerBase {
        private boolean capableOfNonVolatileWiper = false;

        TestablePotentiometer(I2CBus i2cBus, boolean pinA0, boolean pinA1, boolean pinA2, MicrochipPotentiometerChannel channel, MicrochipPotentiometerNonVolatileMode nonVolatileMode, MicrochipPotentiometerDeviceControllerFactory controllerFactory) throws IOException {
            super(i2cBus, pinA0, pinA1, pinA2, channel, nonVolatileMode, 0, controllerFactory);
        }

        public TestablePotentiometer(I2CBus i2cBus, MicrochipPotentiometerChannel channel, MicrochipPotentiometerNonVolatileMode nonVolatileMode, int initialValueForVolatileWipers) throws IOException {
            super(i2cBus, false, false, false, channel, nonVolatileMode, initialValueForVolatileWipers);
        }

        public void initialize(final int initialValueForVolatileWipers) throws IOException {
            super.initialize(initialValueForVolatileWipers);
        }

        @Override
        public boolean isCapableOfNonVolatileWiper() {
            return capableOfNonVolatileWiper;
        }

        public void setCapableOfNonVolatileWiper(boolean capableOfNonVolatileWiper) {
            this.capableOfNonVolatileWiper = capableOfNonVolatileWiper;
        }

        @Override
        public int getMaxValue() {
            return 256;
        }

        @Override
        public boolean isRheostat() {
            return false;
        }

        @Override
        public MicrochipPotentiometerChannel[] getSupportedChannelsByDevice() {
            return new MicrochipPotentiometerChannel[]{ MicrochipPotentiometerChannel.A, MicrochipPotentiometerChannel.B };
        }
    }

    @Test
    public void testCreation() throws IOException {
        // wrong parameters
        try {
            new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(null, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
            Assert.fail(("Got no RuntimeException on constructing " + "a PotentiometerImpl using a null-I2CBus"));
        } catch (RuntimeException e) {
            // expected expection
        }
        try {
            new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, null, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
            Assert.fail(("Got no RuntimeException on constructing " + "a PotentiometerImpl using a null-Channel"));
        } catch (RuntimeException e) {
            // expected expection
        }
        try {
            new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.C, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
            Assert.fail(("Got no RuntimeException on constructing " + "a PotentiometerImpl using a not supported channel"));
        } catch (RuntimeException e) {
            // expected expection
        }
        try {
            new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, null, controllerFactory);
            Assert.fail(("Got no RuntimeException on constructing " + "a PotentiometerImpl using a null-NonVolatileMode"));
        } catch (RuntimeException e) {
            // expected expection
        }
        try {
            new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, null);
            Assert.fail(("Got no RuntimeException on constructing " + "a PotentiometerImpl using a null-controllerFactory"));
        } catch (RuntimeException e) {
            // expected expection
        }
        // correct parameters
        new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
        new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, MicrochipPotentiometerChannel.B, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, 127);
    }

    @Test
    public void testBuildI2CAddress() throws IOException {
        int address1 = MicrochipPotentiometerBase.buildI2CAddress(false, false, false);
        Assert.assertEquals(("'buildI2CAddress(false, false, false)' " + "does not return '0b0101000'"), 40, address1);
        int address2 = MicrochipPotentiometerBase.buildI2CAddress(true, false, false);
        Assert.assertEquals(("'buildI2CAddress(true, false, false)' " + "does not return '0b0101001'"), 41, address2);
        int address3 = MicrochipPotentiometerBase.buildI2CAddress(true, true, false);
        Assert.assertEquals(("'buildI2CAddress(true, true, false)' " + "does not return '0b0101011'"), 43, address3);
        int address4 = MicrochipPotentiometerBase.buildI2CAddress(true, true, true);
        Assert.assertEquals(("'buildI2CAddress(true, true, true)' " + "does not return '0b0101111'"), 47, address4);
    }

    @Test
    public void testInitialization() throws IOException {
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer poti = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
        Mockito.reset(controller);
        poti.setCapableOfNonVolatileWiper(true);
        poti.initialize(0);
        // called with expected parameters
        Mockito.verify(controller).getValue(A, false);
        // only called with expected parameters
        Mockito.verify(controller, Mockito.times(1)).getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        // never called since non-volatile-wiper is true
        Mockito.verify(controller, Mockito.times(0)).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.reset(controller);
        poti.setCapableOfNonVolatileWiper(false);
        poti.initialize(120);
        // called with expected parameters
        Mockito.verify(controller).setValue(A, 120, false);
        // only called with expected parameters
        Mockito.verify(controller, Mockito.times(1)).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // never called since non-volatile-wiper is true
        Mockito.verify(controller, Mockito.times(0)).getValue(A, true);
    }

    @Test
    public void testToString() throws IOException {
        Mockito.when(controller.toString()).thenReturn("ControllerMock");
        final String toString = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory).toString();
        Assert.assertNotNull("result of 'toString()' is null!", toString);
        Assert.assertEquals("Unexpected result from calling 'toString'!", ("com.pi4j.component.potentiometer.microchip.impl.MicrochipPotentiometerPotentiometerImplStaticTest$TestablePotentiometer{\n" + ((("  channel=\'com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerChannel.A\',\n" + "  controller=\'ControllerMock\',\n") + "  nonVolatileMode=\'VOLATILE_ONLY\',\n") + "  currentValue=\'0\'\n}")), toString);
    }

    @Test
    public void testEquals() throws IOException {
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer poti = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer copyOfPoti = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer other1 = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.B, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer other2 = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.NONVOLATILE_ONLY, controllerFactory);
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer other3 = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.NONVOLATILE_ONLY, controllerFactory);
        setCurrentValue(127);
        controller = Mockito.mock(MicrochipPotentiometerDeviceController.class);
        Mockito.when(controllerFactory.getController(ArgumentMatchers.any(I2CDevice.class))).thenReturn(controller);
        final MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer other4 = new MicrochipPotentiometerPotentiometerImplStaticTest.TestablePotentiometer(i2cBus, false, false, false, MicrochipPotentiometerChannel.A, MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY, controllerFactory);
        Assert.assertNotEquals("'poti.equals(null)' returns true!", poti, null);
        Assert.assertEquals("'poti.equals(poti) returns false!", poti, poti);
        Assert.assertNotEquals("\'poti.equals(\"Test\")\' returns true!", poti, "Test");
        Assert.assertEquals("'poti.equals(copyOfPoti)' returns false!", poti, copyOfPoti);
        Assert.assertNotEquals("'poti.equals(other1)' returns true!", poti, other1);
        Assert.assertEquals("'poti.equals(other2)' returns false!", poti, other2);
        Assert.assertEquals("'poti.equals(other3)' returns false!", poti, other3);
        Assert.assertNotEquals("'poti.equals(other4)' returns true!", poti, other4);
    }
}

