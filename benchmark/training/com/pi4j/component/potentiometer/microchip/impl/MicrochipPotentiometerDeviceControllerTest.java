package com.pi4j.component.potentiometer.microchip.impl;


import DeviceControllerChannel.A;
import DeviceControllerChannel.B;
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
FILENAME      :  MicrochipPotentiometerDeviceControllerTest.java

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
public class MicrochipPotentiometerDeviceControllerTest {
    @Mock
    private I2CDevice i2cDevice;

    private MicrochipPotentiometerDeviceController controller;

    @Test
    public void testGetDeviceStatus() throws IOException {
        // test io-exception
        try {
            controller.getDeviceStatus();
            Assert.fail(("Excpected IOException (but there is no) because " + "I2CDevice-mock is not initialized yet!"));
        } catch (IOException e) {
            // expected
        }
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (92)));
        // test success
        Mockito.reset(i2cDevice);
        final int[] length = new int[1];
        // 0b1111111110101 -> Status-bits (see 4.2.2.1)
        mockReadResult(i2cDevice, ((byte) (31)), ((byte) (245)), length);
        DeviceControllerDeviceStatus deviceStatus1 = controller.getDeviceStatus();
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (92)));
        Assert.assertTrue((("On calling 'getDeviceStatus' the method I2CDevice.read(...)" + ("is called with a byte-array as first argument. The length of this " + "array must be at least 2 but was ")) + (length[0])), ((length[0]) >= 2));
        Assert.assertEquals(("Unexpected EEPROM-write-active-flag according to " + "status-bits 0b1111111110101 (see 4.2.2.1)"), false, deviceStatus1.isEepromWriteActive());
        Assert.assertEquals(("Unexpected channel-B-locked-flag according to " + "status-bits 0b1111111110101 (see 4.2.2.1)"), true, deviceStatus1.isChannelBLocked());
        Assert.assertEquals(("Unexpected channel-A-locked-flag according to " + "status-bits 0b1111111110101 (see 4.2.2.1)"), false, deviceStatus1.isChannelALocked());
        Assert.assertEquals(("Unexpected EEPROM-write-protected-flag according to " + "status-bits 0b1111111110101 (see 4.2.2.1)"), true, deviceStatus1.isEepromWriteProtected());
        Mockito.reset(i2cDevice);
        // 0b1111111111010 -> Status-bits (see 4.2.2.1)
        mockReadResult(i2cDevice, ((byte) (31)), ((byte) (250)), length);
        DeviceControllerDeviceStatus deviceStatus2 = controller.getDeviceStatus();
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (92)));
        Assert.assertTrue((("On calling 'getDeviceStatus' the method I2CDevice.read(...)" + ("is called with a byte-array as first argument. The length of this " + "array must be at least 2 but was ")) + (length[0])), ((length[0]) >= 2));
        Assert.assertEquals(("Unexpected EEPROM-write-active-flag according to " + "status-bits 0b1111111111010 (see 4.2.2.1)"), true, deviceStatus2.isEepromWriteActive());
        Assert.assertEquals(("Unexpected channel-B-locked-flag according to " + "status-bits 0b1111111111010 (see 4.2.2.1)"), false, deviceStatus2.isChannelBLocked());
        Assert.assertEquals(("Unexpected channel-A-locked-flag according to " + "status-bits 0b1111111111010 (see 4.2.2.1)"), true, deviceStatus2.isChannelALocked());
        Assert.assertEquals(("Unexpected EEPROM-write-protected-flag according to " + "status-bits 0b1111111111010 (see 4.2.2.1)"), false, deviceStatus2.isEepromWriteProtected());
        // test wrong answer from device
        Mockito.reset(i2cDevice);
        // 0b0000000000000000 -> malformed result!
        mockReadResult(i2cDevice, ((byte) (0)), ((byte) (0)), length);
        try {
            controller.getDeviceStatus();
            Assert.fail(("Excpected IOException (but there is no) because " + "I2CDevice-mock returns malformed device-status!"));
        } catch (IOException e) {
            // expected
        }
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (92)));
        Assert.assertTrue((("On calling 'getDeviceStatus' the method I2CDevice.read(...)" + ("is called with a byte-array as first argument. The length of this " + "array must be at least 2 but was ")) + (length[0])), ((length[0]) >= 2));
    }

    @Test
    public void testGetValue() throws IOException {
        try {
            controller.getValue(null, true);
            Assert.fail("Got no RuntimeException on calling 'getValue(null, ...)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // test wiper 0 - volatile
        // 0b0000000000000000 -> 0
        mockReadResult(i2cDevice, ((byte) (0)), ((byte) (0)), ((int[]) (null)));
        int currentValue = controller.getValue(A, false);
        Assert.assertEquals((("Expected result of 'getCurrentValue(...)' as 0 " + "but got ") + currentValue), 0, currentValue);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (12)));
        // test wiper 1 - volatile
        Mockito.reset(i2cDevice);
        // 0b0000000010000000 -> 128
        mockReadResult(i2cDevice, ((byte) (0)), ((byte) (128)), ((int[]) (null)));
        currentValue = controller.getValue(B, false);
        Assert.assertEquals((("Expected result of 'getCurrentValue(...)' as 128 " + "but got ") + currentValue), 128, currentValue);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (28)));
        // test wiper 0 - non-volatile
        Mockito.reset(i2cDevice);
        // 0b0000000100000001 -> 257
        mockReadResult(i2cDevice, ((byte) (1)), ((byte) (1)), ((int[]) (null)));
        currentValue = controller.getValue(A, true);
        Assert.assertEquals((("Expected result of 'getCurrentValue(...)' as 257 " + "but got ") + currentValue), 257, currentValue);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (44)));
        // test wiper 1 - non-volatile
        Mockito.reset(i2cDevice);
        // 0b0000000100000001 -> 257
        mockReadResult(i2cDevice, ((byte) (1)), ((byte) (1)), ((int[]) (null)));
        currentValue = controller.getValue(B, true);
        Assert.assertEquals((("Expected result of 'getCurrentValue(...)' as 257 " + "but got ") + currentValue), 257, currentValue);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (60)));
    }

    @Test
    public void testSetValue() throws IOException {
        try {
            controller.setValue(null, 1, true);
            Assert.fail("Got no RuntimeException on calling 'setValue(null, ...)'!");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            controller.setValue(A, (-1), true);
            Assert.fail("Got no RuntimeException on calling 'setValue(...)' using a negative value!");
        } catch (RuntimeException e) {
            // expected
        }
        // test wiper 0 - volatile
        controller.setValue(A, 0, false);
        // test for proper write-argument -> see FIGURE 7-2 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (0)), ((byte) (0)) }, 0, 2);
        // test for write was called only once
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // test wiper 1 - volatile
        Mockito.reset(i2cDevice);
        controller.setValue(B, 1, false);
        // test for proper write-argument -> see FIGURE 7-2 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (16)), ((byte) (1)) }, 0, 2);
        // 'write' is called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // test wiper 0 - non-volatile
        Mockito.reset(i2cDevice);
        controller.setValue(A, 255, true);
        // test for proper write-argument -> see FIGURE 7-2 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (32)), ((byte) (255)) }, 0, 2);
        // 'write' is called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // test wiper 1 - non-volatile
        Mockito.reset(i2cDevice);
        controller.setValue(B, 256, true);
        // test for proper write-argument -> see FIGURE 7-2 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (49)), ((byte) (0)) }, 0, 2);
        // 'write' is called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testIncrease() throws IOException {
        try {
            controller.increase(null, 1);
            Assert.fail("Got no RuntimeException on calling 'increase(null, ...)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // zero-step increase
        controller.increase(A, 0);
        // 'write' called zero times
        Mockito.verify(i2cDevice, Mockito.times(0)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // one-step increase
        Mockito.reset(i2cDevice);
        controller.increase(A, 1);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (4)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // three-step increase
        Mockito.reset(i2cDevice);
        controller.increase(B, 3);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (20)), ((byte) (20)), ((byte) (20)) }, 0, 3);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // negative-steps increase
        Mockito.reset(i2cDevice);
        controller.increase(A, (-1));
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (8)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testDecrease() throws IOException {
        try {
            controller.decrease(null, 1);
            Assert.fail("Got no RuntimeException on calling 'decrease(null, ...)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // zero-step decrease
        controller.increase(A, 0);
        // 'write' called zero times
        Mockito.verify(i2cDevice, Mockito.times(0)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // one-step decrease
        Mockito.reset(i2cDevice);
        controller.decrease(A, 1);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (8)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // three-step decrease
        Mockito.reset(i2cDevice);
        controller.decrease(B, 3);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (24)), ((byte) (24)), ((byte) (24)) }, 0, 3);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // negative-steps decrease
        Mockito.reset(i2cDevice);
        controller.decrease(A, (-1));
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (4)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testGetTerminalConfiguration() throws IOException {
        try {
            controller.getTerminalConfiguration(null);
            Assert.fail("Got no RuntimeException on calling 'getTerminalConfiguration(null)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // 0b0111000011 -> TCON-bits (see 4.2.2.2)
        mockReadResult(i2cDevice, ((byte) (0)), ((byte) (195)), null);
        DeviceControllerTerminalConfiguration tconA = controller.getTerminalConfiguration(A);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (76)));
        Assert.assertNotNull("Calling 'getTerminalConfiguration(Channel.A)' did return null!", tconA);
        Assert.assertEquals(("Result of 'getTerminalConfiguration(Channel.A)' did not return '" + "Channel.A' on calling 'getChannel()'!"), A, tconA.getChannel());
        Assert.assertEquals("According to mocked read-result the channel should be disabled!", false, tconA.isChannelEnabled());
        Assert.assertEquals("According to mocked read-result the pin A should be disabled!", false, tconA.isPinAEnabled());
        Assert.assertEquals("According to mocked read-result the pin W should be enabled!", true, tconA.isPinWEnabled());
        Assert.assertEquals("According to mocked read-result the pin B should be enabled!", true, tconA.isPinBEnabled());
        DeviceControllerTerminalConfiguration tconB = controller.getTerminalConfiguration(B);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (76)), 2);
        Assert.assertNotNull("Calling 'getTerminalConfiguration(Channel.B)' did return null!", tconB);
        Assert.assertEquals(("Result of 'getTerminalConfiguration(Channel.B)' did not return '" + "Channel.A' on calling 'getChannel()'!"), B, tconB.getChannel());
        Assert.assertEquals("According to mocked read-result the channel should be enabled!", true, tconB.isChannelEnabled());
        Assert.assertEquals("According to mocked read-result the pin A should be enabled!", true, tconB.isPinAEnabled());
        Assert.assertEquals("According to mocked read-result the pin W should be disabled!", false, tconB.isPinWEnabled());
        Assert.assertEquals("According to mocked read-result the pin B should be disabled!", false, tconB.isPinBEnabled());
    }

    @Test
    public void testSetTerminalConfiguration() throws IOException {
        try {
            controller.setTerminalConfiguration(null);
            Assert.fail("Got no RuntimeException on calling 'setTerminalConfiguration(null)'!");
        } catch (RuntimeException e) {
            // expected
        }
        try {
            controller.setTerminalConfiguration(new DeviceControllerTerminalConfiguration(null, false, false, false, false));
            Assert.fail(("Got no RuntimeException on calling 'setTerminalConfiguration(tcon)' " + " where tcon.getChannel() is null!"));
        } catch (RuntimeException e) {
            // expected
        }
        // 0b0101010101 -> TCON-bits (see 4.2.2.2)
        mockReadResult(i2cDevice, ((byte) (0)), ((byte) (341)), null);
        DeviceControllerTerminalConfiguration tconA = new DeviceControllerTerminalConfiguration(A, true, true, true, false);
        controller.setTerminalConfiguration(tconA);
        // reading current configuration:
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (76)));
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        // The first four bits of the second byte are the same four bits of
        // the mocked read-result. Those four bits represent the configuration
        // of wiper1. This test only modifies wiper0, so only the last four
        // bits have to be according to 'tconA'.
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (64)), ((byte) (94)) }, 0, 2);
        DeviceControllerTerminalConfiguration tconB = new DeviceControllerTerminalConfiguration(B, false, false, false, true);
        controller.setTerminalConfiguration(tconB);
        // reading current configuration:
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        testForWriteAndRead(((byte) (76)), 2);
        // test for proper write-argument -> see FIGURE 7-5 and TABLE 4-1
        // The last four bits of the second byte are the same four bits of
        // the mocked read-result. Those four bits represent the configuration
        // of wiper0. This test only modifies wiper1, so only the last four
        // bits have to be according to 'tconB'.
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (64)), ((byte) (21)) }, 0, 2);
    }

    @Test
    public void testSetWiperLock() throws IOException {
        try {
            controller.setWiperLock(null, true);
            Assert.fail("Got no RuntimeException on calling 'setWiperLock(null, ...)'!");
        } catch (RuntimeException e) {
            // expected
        }
        // channel.A -> lock
        controller.setWiperLock(A, true);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (36)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // channel.A -> unlock
        Mockito.reset(i2cDevice);
        controller.setWiperLock(A, false);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (40)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // channel.B -> lock
        Mockito.reset(i2cDevice);
        controller.setWiperLock(B, true);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (52)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // channel.B -> unlock
        Mockito.reset(i2cDevice);
        controller.setWiperLock(B, false);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (56)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test
    public void testSetWriteProtection() throws IOException {
        // lock
        controller.setWriteProtection(true);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (244)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // unlock
        Mockito.reset(i2cDevice);
        controller.setWriteProtection(false);
        // test for proper write-argument -> see FIGURE 7-7 and TABLE 4-1
        Mockito.verify(i2cDevice).write(new byte[]{ ((byte) (248)) }, 0, 1);
        // 'write' called on time
        Mockito.verify(i2cDevice).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }
}

