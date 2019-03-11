package com.pi4j.component.potentiometer.microchip.impl;


import DeviceControllerChannel.A;
import DeviceControllerChannel.B;
import MicrochipPotentiometerChannel.C;
import MicrochipPotentiometerNonVolatileMode.NONVOLATILE_ONLY;
import MicrochipPotentiometerNonVolatileMode.VOLATILE_AND_NONVOLATILE;
import MicrochipPotentiometerNonVolatileMode.VOLATILE_ONLY;
import com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerChannel;
import com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerDeviceStatus;
import com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerNonVolatileMode;
import com.pi4j.component.potentiometer.microchip.MicrochipPotentiometerTerminalConfiguration;
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

import static DeviceControllerChannel.A;
import static DeviceControllerChannel.B;


/* #%L
**********************************************************************
ORGANIZATION  :  Pi4J
PROJECT       :  Pi4J :: Device Abstractions
FILENAME      :  MicrochipPotentiometerPotentiometerImplTest.java

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
public class MicrochipPotentiometerPotentiometerImplTest {
    private static int INITIAL_VALUE_A = 0;

    private static int INITIAL_VALUE_B = 100;

    @Mock
    private I2CDevice i2cDevice;

    @Mock
    private I2CBus i2cBus;

    @Mock
    private MicrochipPotentiometerDeviceController controller;

    @Mock
    private MicrochipPotentiometerDeviceControllerFactory controllerFactory;

    private MicrochipPotentiometerPotentiometerImplTest.TestableMCP45xxMCP46xxPotentiometer potiA;

    private MicrochipPotentiometerPotentiometerImplTest.TestableMCP45xxMCP46xxPotentiometer potiB;

    class TestableMCP45xxMCP46xxPotentiometer extends MicrochipPotentiometerBase {
        private boolean capableOfNonVolatileWiper;

        TestableMCP45xxMCP46xxPotentiometer(MicrochipPotentiometerChannel channel, boolean capableOfNonVolatileWiper, int initialValue) throws IOException {
            super(i2cBus, false, false, false, channel, VOLATILE_ONLY, initialValue, controllerFactory);
            this.capableOfNonVolatileWiper = capableOfNonVolatileWiper;
        }

        @Override
        public boolean isCapableOfNonVolatileWiper() {
            return capableOfNonVolatileWiper;
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
    public void testDeviceStatus() throws IOException {
        boolean WRITE_ACTIVE = false;
        boolean WRITE_PROTECTED = true;
        boolean WIPER0_LOCKED = false;
        boolean WIPER1_LOCKED = true;
        // prepare controller-status mock
        DeviceControllerDeviceStatus deviceStatusMock = Mockito.mock(DeviceControllerDeviceStatus.class);
        Mockito.when(deviceStatusMock.isEepromWriteActive()).thenReturn(WRITE_ACTIVE);
        Mockito.when(deviceStatusMock.isEepromWriteProtected()).thenReturn(WRITE_PROTECTED);
        Mockito.when(deviceStatusMock.isChannelALocked()).thenReturn(WIPER0_LOCKED);
        Mockito.when(deviceStatusMock.isChannelBLocked()).thenReturn(WIPER1_LOCKED);
        Mockito.when(controller.getDeviceStatus()).thenReturn(deviceStatusMock);
        // test poti of channel A
        MicrochipPotentiometerDeviceStatus deviceStatusA = getDeviceStatus();
        Assert.assertNotNull(("Method 'getDeviceStatus()' returned null but " + "expected a proper 'DeviceStatus'-instance!"), deviceStatusA);
        Assert.assertEquals("Got unexpected write-active-flag", WRITE_ACTIVE, deviceStatusA.isEepromWriteActive());
        Assert.assertEquals("Got unexpected write-protected-flag", WRITE_PROTECTED, deviceStatusA.isEepromWriteProtected());
        Assert.assertEquals("Got wrong channel in device-status", getChannel(), deviceStatusA.getWiperLockChannel());
        Assert.assertEquals("Got unexpected write-locked-flag", WIPER0_LOCKED, deviceStatusA.isWiperLockActive());
        // test poti of channel B
        MicrochipPotentiometerDeviceStatus deviceStatusB = getDeviceStatus();
        Assert.assertNotNull(("Method 'getDeviceStatus()' returned null but " + "expected a proper 'DeviceStatus'-instance!"), deviceStatusB);
        Assert.assertEquals("Got unexpected write-active-flag", WRITE_ACTIVE, deviceStatusB.isEepromWriteActive());
        Assert.assertEquals("Got unexpected write-protected-flag", WRITE_PROTECTED, deviceStatusB.isEepromWriteProtected());
        Assert.assertEquals("Got wrong channel in device-status", getChannel(), deviceStatusB.getWiperLockChannel());
        Assert.assertEquals("Got unexpected write-locked-flag", WIPER1_LOCKED, deviceStatusB.isWiperLockActive());
    }

    @Test
    public void testGetCurrentValue() throws IOException {
        // test simple calls
        int potiAValue = getCurrentValue();
        Assert.assertEquals(((("Expected to get initial-value '" + (MicrochipPotentiometerPotentiometerImplTest.INITIAL_VALUE_A)) + "' on calling 'getCurrentValue()' ") + "after building an object-instance"), MicrochipPotentiometerPotentiometerImplTest.INITIAL_VALUE_A, potiAValue);
        int potiBValue = getCurrentValue();
        Assert.assertEquals(((("Expected to get initial-value '" + (MicrochipPotentiometerPotentiometerImplTest.INITIAL_VALUE_B)) + "' on calling 'getCurrentValue()' ") + "after building an object-instance"), MicrochipPotentiometerPotentiometerImplTest.INITIAL_VALUE_B, potiBValue);
        // repeatable calls
        int potiBValue1 = getCurrentValue();
        Assert.assertEquals(((("Expected to get initial-value '" + (MicrochipPotentiometerPotentiometerImplTest.INITIAL_VALUE_B)) + "' on calling 'getCurrentValue()' ") + "after building an object-instance"), MicrochipPotentiometerPotentiometerImplTest.INITIAL_VALUE_B, potiBValue1);
        int potiBValue2 = getCurrentValue();
        Assert.assertEquals(("Expected to get the same value on calling '" + ("getCurrentValue()' for the second time as returned " + "at the first time!")), potiBValue1, potiBValue2);
    }

    @Test
    public void testMicrochipPotentiometerNonVolatileMode() {
        // null-test
        try {
            setNonVolatileMode(null);
            Assert.fail("Got no RuntimeException on calling 'setNonVolatileMode(null)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // unsupported modes (potiB is not capable of non-volatile wipers)
        try {
            potiB.setNonVolatileMode(NONVOLATILE_ONLY);
            Assert.fail("Got no RuntimeException on calling 'setNonVolatileMode(NONVOLATILE_ONLY)'!");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            potiB.setNonVolatileMode(VOLATILE_AND_NONVOLATILE);
            Assert.fail("Got no RuntimeException on calling 'setNonVolatileMode(VOLATILE_AND_NONVOLATILE)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // supported modes
        potiA.setNonVolatileMode(NONVOLATILE_ONLY);
        MicrochipPotentiometerNonVolatileMode nonVolatileMode1 = getNonVolatileMode();
        Assert.assertEquals(("After calling 'setNonVolatileMode(NONVOLATILE_ONLY)' the method " + "'getNonVolatileMode()' does not return NONVOLATILE_ONLY!"), NONVOLATILE_ONLY, nonVolatileMode1);
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        MicrochipPotentiometerNonVolatileMode nonVolatileMode2 = getNonVolatileMode();
        Assert.assertEquals(("After calling 'setNonVolatileMode(VOLATILE_ONLY)' the method " + "'getNonVolatileMode()' does not return VOLATILE_ONLY!"), VOLATILE_ONLY, nonVolatileMode2);
        potiA.setNonVolatileMode(VOLATILE_AND_NONVOLATILE);
        MicrochipPotentiometerNonVolatileMode nonVolatileMode3 = getNonVolatileMode();
        Assert.assertEquals(("After calling 'setNonVolatileMode(VOLATILE_AND_NONVOLATILE)' the method " + "'getNonVolatileMode()' does not return VOLATILE_AND_NONVOLATILE!"), VOLATILE_AND_NONVOLATILE, nonVolatileMode3);
        potiB.setNonVolatileMode(VOLATILE_ONLY);
        MicrochipPotentiometerNonVolatileMode nonVolatileMode4 = getNonVolatileMode();
        Assert.assertEquals(("After calling 'setNonVolatileMode(VOLATILE_ONLY)' the method " + "'getNonVolatileMode()' does not return VOLATILE_ONLY!"), VOLATILE_ONLY, nonVolatileMode4);
    }

    @Test
    public void testSetCurrentValue() throws IOException {
        // test RAM_ONLY
        Mockito.reset(controller);
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        setCurrentValue(50);
        // controller 'setValue' used to set value '50' on channel 'A' for volatile-wiper
        Mockito.verify(controller).setValue(A, 50, false);
        // controller 'setValue' only used one time
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        int currentValue1 = getCurrentValue();
        Assert.assertEquals("Expected to get 50, previously set, on calling 'getCurrentValue()'!", 50, currentValue1);
        // test EEPROM_ONLY
        Mockito.reset(controller);
        potiA.setNonVolatileMode(NONVOLATILE_ONLY);
        setCurrentValue(60);
        // controller 'setValue' used to set '60' on channel 'A' for non-volatile-wiper
        Mockito.verify(controller).setValue(A, 60, true);
        // controller 'setValue' only used one time
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        int currentValue2 = getCurrentValue();
        Assert.assertEquals(("Expected to get 50, since MicrochipPotentiometerNonVolatileMode is NONVOLATILE_ONLY, " + "on calling 'getCurrentValue()'!"), 50, currentValue2);
        // test RAM_AND_EEPROM
        Mockito.reset(controller);
        potiA.setNonVolatileMode(VOLATILE_AND_NONVOLATILE);
        setCurrentValue(70);
        // controller 'setValue' used to set '70' on channel 'A' for non-volatile-wiper
        Mockito.verify(controller).setValue(A, 70, true);
        // controller 'setValue' used to set '70' on channel 'A' for volatile-wiper
        Mockito.verify(controller).setValue(A, 70, false);
        // controller 'setValue' used two times
        Mockito.verify(controller, Mockito.times(2)).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        int currentValue3 = getCurrentValue();
        Assert.assertEquals("Expected to get 70, previously set, on calling 'getCurrentValue()'!", 70, currentValue3);
        // test value below lower boundary
        Mockito.reset(controller);
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        setCurrentValue((-50));
        // controller 'setValue' used to set '0' on channel 'A' for volatile-wiper
        Mockito.verify(controller).setValue(A, 0, false);
        // controller 'setValue' used one time
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        int currentValue4 = getCurrentValue();
        Assert.assertEquals("Expected to get 0, previously set, on calling 'getCurrentValue()'!", 0, currentValue4);
        // test value above upper boundary
        Mockito.reset(controller);
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        setCurrentValue(400);
        // controller 'setValue' used to set '256' on channel 'A' for volatile-wiper
        Mockito.verify(controller).setValue(A, 256, false);
        // controller 'setValue' used on time
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        int currentValue5 = getCurrentValue();
        Assert.assertEquals("Expected to get 256, previously set, on calling 'getCurrentValue()'!", 256, currentValue5);
    }

    @Test
    public void testIncrease() throws IOException {
        // wrong parameters
        potiA.setNonVolatileMode(VOLATILE_AND_NONVOLATILE);
        try {
            increase();
        } catch (RuntimeException e) {
            // expected because only VOLATILE_ONLY is supported
        }
        potiA.setNonVolatileMode(NONVOLATILE_ONLY);
        try {
            increase();
        } catch (RuntimeException e) {
            // expected because only VOLATILE_ONLY is supported
        }
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        try {
            potiA.increase((-10));
        } catch (RuntimeException e) {
            // expected because only positive values are allowed
        }
        // success
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        setCurrentValue(240);
        Mockito.reset(controller);
        increase();
        // controller 'increase' used with '1' step on channel 'A' for volatile-wiper
        Mockito.verify(controller).increase(A, 1);
        // controller 'increase' used one time
        Mockito.verify(controller).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue1 = getCurrentValue();
        Assert.assertEquals("Expected to get 241 on calling 'getCurrentValue()'!", 241, currentValue1);
        Mockito.reset(controller);
        potiA.increase(2);
        // controller 'increase' used with '2' steps on channel 'A' for volatile-wiper
        Mockito.verify(controller).increase(A, 2);
        // controller 'increase' used on time
        Mockito.verify(controller).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue2 = getCurrentValue();
        Assert.assertEquals("Expected to get 243 on calling 'getCurrentValue()'!", 243, currentValue2);
        Mockito.reset(controller);
        potiA.increase(10);
        // controller 'setValue' used to set '253' on channel 'A' for volatile-wiper
        // instead of increase because for more than 5 steps using 'setValue' is "cheaper"
        Mockito.verify(controller).setValue(A, 253, false);
        // controller 'setValue' used on time
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // controller 'increase' is not used
        Mockito.verify(controller, Mockito.times(0)).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        Mockito.reset(controller);
        potiA.increase(10);
        // controller 'setValue' used to set '256' on channel 'A' for volatile-wiper
        // instead of increase because this hits the upper boundary
        Mockito.verify(controller).setValue(A, 256, false);
        // controller 'setValue' used on time
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        // controller 'increase' is not used
        Mockito.verify(controller, Mockito.times(0)).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue3 = getCurrentValue();
        Assert.assertEquals("Expected to get 256 on calling 'getCurrentValue()'!", 256, currentValue3);
        Mockito.reset(controller);
        increase();
        Mockito.verify(controller, Mockito.times(0)).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(controller, Mockito.times(0)).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue4 = getCurrentValue();
        Assert.assertEquals("Expected to get 256 on calling 'getCurrentValue()'!", 256, currentValue4);
    }

    @Test
    public void testDecrease() throws IOException {
        // wrong parameters
        setCurrentValue(10);
        potiA.setNonVolatileMode(VOLATILE_AND_NONVOLATILE);
        try {
            decrease();
        } catch (RuntimeException e) {
            // expected because only VOLATILE_ONLY is supported
        }
        potiA.setNonVolatileMode(NONVOLATILE_ONLY);
        try {
            potiA.decrease(10);
        } catch (RuntimeException e) {
            // expected because only VOLATILE_ONLY is supported
        }
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        try {
            potiA.decrease((-10));
        } catch (RuntimeException e) {
            // expected because only positive values are allowed
        }
        // success
        potiA.setNonVolatileMode(VOLATILE_ONLY);
        Mockito.reset(controller);
        decrease();
        Mockito.verify(controller).decrease(A, 1);
        Mockito.verify(controller).decrease(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue1 = getCurrentValue();
        Assert.assertEquals("Expected to get 9 on calling 'getCurrentValue()'!", 9, currentValue1);
        Mockito.reset(controller);
        potiA.decrease(2);
        Mockito.verify(controller).decrease(A, 2);
        Mockito.verify(controller).decrease(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue2 = getCurrentValue();
        Assert.assertEquals("Expected to get 7 on calling 'getCurrentValue()'!", 7, currentValue2);
        Mockito.reset(controller);
        potiA.decrease(6);
        Mockito.verify(controller).setValue(A, 1, false);
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(controller, Mockito.times(0)).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue3 = getCurrentValue();
        Assert.assertEquals("Expected to get 1 on calling 'getCurrentValue()'!", 1, currentValue3);
        Mockito.reset(controller);
        potiA.decrease(20);
        Mockito.verify(controller).setValue(A, 0, false);
        Mockito.verify(controller).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(controller, Mockito.times(0)).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue4 = getCurrentValue();
        Assert.assertEquals("Expected to get 0 on calling 'getCurrentValue()'!", 0, currentValue4);
        Mockito.reset(controller);
        decrease();
        Mockito.verify(controller, Mockito.times(0)).setValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
        Mockito.verify(controller, Mockito.times(0)).increase(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyInt());
        int currentValue5 = getCurrentValue();
        Assert.assertEquals("Expected to get 0 on calling 'getCurrentValue()'!", 0, currentValue5);
    }

    @Test
    public void testUpdateCacheFromDevice() throws IOException {
        setCurrentValue(50);
        int currentValue1 = getCurrentValue();
        Assert.assertEquals(("Precondition for test fails: 'getCurrentValue()' should " + "return 50!"), 50, currentValue1);
        Mockito.reset(controller);
        Mockito.when(controller.getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.eq(false))).thenReturn(40);
        Mockito.when(controller.getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.eq(true))).thenReturn(70);
        int currentValue2 = updateCacheFromDevice();
        Mockito.verify(controller).getValue(A, false);
        Mockito.verify(controller).getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        Assert.assertEquals("Did not get updated value by method 'updateCacheFromDevice()'", 40, currentValue2);
        int currentValue3 = getCurrentValue();
        Assert.assertEquals(("'getCurrentValue()' did not return updated value after " + "calling 'updateCacheFromDevice()'"), currentValue2, currentValue3);
    }

    @Test
    public void testGetNonVolatileValue() throws IOException {
        try {
            getNonVolatileValue();
            Assert.fail(("Expected 'getNonVolatileValue()' to throw RuntimeException " + "because potiB is not capable of non-volatile wipers!"));
        } catch (RuntimeException e) {
            // expected since potiB is not capable of non-volatile wipers
        }
        Mockito.verify(controller, Mockito.times(0)).getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        Mockito.when(controller.getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.eq(false))).thenReturn(40);
        Mockito.when(controller.getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.eq(true))).thenReturn(70);
        int nonVolatileValue = potiA.getNonVolatileValue();
        Mockito.verify(controller).getValue(A, true);
        Mockito.verify(controller).getValue(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        Assert.assertEquals("Did not get non-volatile-value on calling 'getNonVolatileValue()'", 70, nonVolatileValue);
    }

    @Test
    public void testGetTerminalConfiguration() throws IOException {
        // channel A poti
        final DeviceControllerTerminalConfiguration mockedTconA = new DeviceControllerTerminalConfiguration(A, true, false, true, false);
        Mockito.when(controller.getTerminalConfiguration(ArgumentMatchers.eq(A))).thenReturn(mockedTconA);
        MicrochipPotentiometerTerminalConfiguration tconA = getTerminalConfiguration();
        Assert.assertNotNull(("'getTerminalConfiguration()' return null but expected a " + "properly filled object!"), tconA);
        Assert.assertNotNull(("'getTerminalConfiguration()' returned an object which " + ("method 'getChannel() returns null and not the poti's " + "channel!")), tconA.getChannel());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the poti's channel!"), MicrochipPotentiometerChannel.A, tconA.getChannel());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'channelEnabled'!"), mockedTconA.isChannelEnabled(), tconA.isChannelEnabled());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'pinAEnabled'!"), mockedTconA.isPinAEnabled(), tconA.isPinAEnabled());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'pinWEnabled'!"), mockedTconA.isPinWEnabled(), tconA.isPinWEnabled());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'pinBEnabled'!"), mockedTconA.isPinBEnabled(), tconA.isPinBEnabled());
        // channel B poti
        final DeviceControllerTerminalConfiguration mockedTconB = new DeviceControllerTerminalConfiguration(B, false, true, false, true);
        Mockito.when(controller.getTerminalConfiguration(ArgumentMatchers.eq(B))).thenReturn(mockedTconB);
        MicrochipPotentiometerTerminalConfiguration tconB = getTerminalConfiguration();
        Assert.assertNotNull(("'getTerminalConfiguration()' return null but expected a " + "properly filled object!"), tconB);
        Assert.assertNotNull(("'getTerminalConfiguration()' returned an object which " + ("method 'getChannel() returns null and not the poti's " + "channel!")), tconB.getChannel());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the poti's channel!"), MicrochipPotentiometerChannel.B, tconB.getChannel());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'channelEnabled'!"), mockedTconB.isChannelEnabled(), tconB.isChannelEnabled());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'pinAEnabled'!"), mockedTconB.isPinAEnabled(), tconB.isPinAEnabled());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'pinWEnabled'!"), mockedTconB.isPinWEnabled(), tconB.isPinWEnabled());
        Assert.assertEquals(("'getTerminalConfiguration()' returned a configuration " + "not matching the mocked controllers configuration 'pinBEnabled'!"), mockedTconB.isPinBEnabled(), tconB.isPinBEnabled());
    }

    @Test
    public void testSetTerminalConfiguration() throws IOException {
        try {
            potiA.setTerminalConfiguration(null);
            Assert.fail("Expected 'setTerminalConfiguration(null)' to throw RuntimeException!");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            setTerminalConfiguration(new MicrochipPotentiometerTerminalConfiguration(MicrochipPotentiometerChannel.B, true, true, true, true));
            Assert.fail(("Expected 'setTerminalConfiguration(...)' to throw RuntimeException " + ("because the given configuration's channel does not match " + "the potentiometers channel!")));
        } catch (RuntimeException e) {
            // expected
        }
        // test poti A
        setTerminalConfiguration(new MicrochipPotentiometerTerminalConfiguration(MicrochipPotentiometerChannel.A, true, false, true, false));
        Mockito.verify(controller).setTerminalConfiguration(new DeviceControllerTerminalConfiguration(A, true, false, true, false));
        Mockito.verify(controller).setTerminalConfiguration(ArgumentMatchers.any(DeviceControllerTerminalConfiguration.class));
    }

    @Test
    public void testSetWriteProtection() throws IOException {
        // enable lock
        setWriteProtection(true);
        setWriteProtection(true);
        setWriteProtection(ArgumentMatchers.anyBoolean());
        // disable lock
        Mockito.reset(controller);
        setWriteProtection(false);
        setWriteProtection(false);
        setWriteProtection(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testSetWiperLock() throws IOException {
        // enable lock for poti A
        setWiperLock(true);
        setWiperLock(A, true);
        Mockito.verify(controller).setWiperLock(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        // disable lock for poti A
        Mockito.reset(controller);
        setWiperLock(false);
        setWiperLock(A, false);
        Mockito.verify(controller).setWiperLock(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        // disable lock for poti B
        Mockito.reset(controller);
        setWiperLock(false);
        setWiperLock(B, false);
        Mockito.verify(controller).setWiperLock(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
        // disable lock for poti B
        Mockito.reset(controller);
        setWiperLock(false);
        setWiperLock(B, false);
        Mockito.verify(controller).setWiperLock(ArgumentMatchers.any(DeviceControllerChannel.class), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testIsChannelSupportedByDevice() {
        boolean supported1 = isChannelSupportedByDevice(null);
        Assert.assertFalse("'isChannelSupported(null) must be false but wasn't!", supported1);
        boolean supported2 = potiA.isChannelSupportedByDevice(MicrochipPotentiometerChannel.A);
        Assert.assertTrue("'isChannelSupported(Channel.A) must be true but wasn't!", supported2);
        boolean supported3 = potiA.isChannelSupportedByDevice(C);
        Assert.assertFalse("'isChannelSupported(Channel.C) must be false but wasn't!", supported3);
    }
}

