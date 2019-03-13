/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  I2CDeviceImplTest.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package com.pi4j.io.i2c.impl;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class I2CDeviceImplTest {
    private static final int ADDRESS = 40;

    private static final int LOCALADDRESS = 2;

    private static final byte READ_FIRSTBYTE = 77;

    private static final byte READ_SECONDBYTE = 78;

    private static final byte WRITE_FIRSTBYTE = 47;

    private static final byte WRITE_SECONDBYTE = 11;

    private Answer<Integer> readAnswer = new Answer<Integer>() {
        @Override
        public Integer answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            int size = ((Integer) (args[((args.length) - 3)]));
            int offset = ((Integer) (args[((args.length) - 2)]));
            byte[] buffer = ((byte[]) (args[((args.length) - 1)]));
            if (size < 2) {
                throw new IOException((("Expected a size greater than one but got '" + size) + "'!"));
            }
            if (offset < 0) {
                throw new IOException((("Expected an non-negative offset but got '" + offset) + "'!"));
            }
            if (buffer == null) {
                throw new IOException("Got null-buffer!");
            }
            if ((buffer.length) < (offset + size)) {
                throw new IndexOutOfBoundsException((((("Expected a buffer greater than 'offset + size' (=" + (offset + size)) + ") but got '") + (buffer.length)) + "'"));
            }
            buffer[offset] = I2CDeviceImplTest.READ_FIRSTBYTE;
            buffer[(offset + 1)] = I2CDeviceImplTest.READ_SECONDBYTE;
            return 2;
        }
    };

    private Answer<Integer> writeAndReadAnswer = new Answer<Integer>() {
        @Override
        public Integer answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            int writeSize = ((Integer) (args[((args.length) - 6)]));
            int writeOffset = ((Integer) (args[((args.length) - 5)]));
            byte[] writeData = ((byte[]) (args[((args.length) - 4)]));
            if (writeSize < 2) {
                throw new IOException((("Expected a writeSize greater than one but got '" + writeSize) + "'!"));
            }
            if (writeOffset < 0) {
                throw new IllegalArgumentException((("Expected an non-negative writeOffset but got '" + writeOffset) + "'!"));
            }
            if (writeData == null) {
                throw new IllegalArgumentException("Got null-writeData!");
            }
            if ((writeData.length) < (writeOffset + writeSize)) {
                throw new IndexOutOfBoundsException((((("Expected a buffer greater than 'offset + size' (=" + (writeOffset + writeSize)) + ") but got '") + (writeData.length)) + "'"));
            }
            byte firstByte = writeData[writeOffset];
            if (firstByte != (I2CDeviceImplTest.WRITE_FIRSTBYTE)) {
                throw new IOException(((((("Expected to get '" + (I2CDeviceImplTest.WRITE_FIRSTBYTE)) + "' at writeData[") + writeOffset) + "] but got '") + firstByte));
            }
            byte secondByte = writeData[(writeOffset + 1)];
            if (secondByte != (I2CDeviceImplTest.WRITE_SECONDBYTE)) {
                throw new IOException(((((("Expected to get '" + (I2CDeviceImplTest.WRITE_SECONDBYTE)) + "' at writeData[") + (writeOffset + 1)) + "] but got '") + secondByte));
            }
            int size = ((Integer) (args[((args.length) - 3)]));
            int offset = ((Integer) (args[((args.length) - 2)]));
            byte[] buffer = ((byte[]) (args[((args.length) - 1)]));
            if (size < 2) {
                throw new IllegalArgumentException((("Expected a size greater than one but got '" + size) + "'!"));
            }
            if (offset < 0) {
                throw new IllegalArgumentException((("Expected an non-negative offset but got '" + offset) + "'!"));
            }
            if (buffer == null) {
                throw new IllegalArgumentException("Got null-buffer!");
            }
            if ((buffer.length) < (offset + size)) {
                throw new IndexOutOfBoundsException((((("Expected a buffer greater than 'offset + size' (=" + (offset + size)) + ") but got '") + (buffer.length)) + "'"));
            }
            buffer[offset] = I2CDeviceImplTest.READ_FIRSTBYTE;
            buffer[(offset + 1)] = I2CDeviceImplTest.READ_SECONDBYTE;
            return 2;
        }
    };

    @SuppressWarnings("unused")
    private Answer<Object> writeAnswer = new Answer<Object>() {
        @Override
        public Integer answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            int writeSize = ((Integer) (args[((args.length) - 3)]));
            int writeOffset = ((Integer) (args[((args.length) - 2)]));
            byte[] writeData = ((byte[]) (args[((args.length) - 1)]));
            if (writeSize < 2) {
                throw new IOException((("Expected a writeSize greater than one but got '" + writeSize) + "'!"));
            }
            if (writeOffset < 0) {
                throw new IllegalArgumentException((("Expected an non-negative writeOffset but got '" + writeOffset) + "'!"));
            }
            if (writeData == null) {
                throw new IllegalArgumentException("Got null-writeData!");
            }
            if ((writeData.length) < (writeOffset + writeSize)) {
                throw new IndexOutOfBoundsException((((("Expected a buffer greater than 'offset + size' (=" + (writeOffset + writeSize)) + ") but got '") + (writeData.length)) + "'"));
            }
            return null;// void

        }
    };

    @Mock
    private I2CBusImpl bus;

    private I2CDeviceImpl device;

    @Test
    public void testBasics() throws IOException {
        int address = device.getAddress();
        Assert.assertEquals(("'getAddress()' returns another address that the " + "device was constructed with"), I2CDeviceImplTest.ADDRESS, address);
    }

    @Test
    public void testReading() throws IOException {
        // read a byte
        int readResult1 = 50;
        Mockito.when(bus.readByteDirect(ArgumentMatchers.eq(device))).thenReturn(readResult1);
        int read1 = device.read();
        Assert.assertEquals("'read()' does not return the expected value!", readResult1, read1);
        // read a byte from register
        int readResult2 = 51;
        Mockito.when(bus.readByte(ArgumentMatchers.eq(device), ArgumentMatchers.eq(I2CDeviceImplTest.LOCALADDRESS))).thenReturn(readResult2);
        int read2 = device.read(I2CDeviceImplTest.LOCALADDRESS);
        Assert.assertEquals("'read(int)' does not return the expected value!", readResult2, read2);
        // read n bytes
        Mockito.when(bus.readBytesDirect(ArgumentMatchers.eq(device), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(byte[].class))).thenAnswer(readAnswer);
        byte[] buffer1 = new byte[2];
        int read3 = device.read(buffer1, 0, 2);
        Assert.assertEquals("Expected to read one byte but got a different number of bytes!", 2, read3);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer1[0], I2CDeviceImplTest.READ_FIRSTBYTE);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer1[1], I2CDeviceImplTest.READ_SECONDBYTE);
        byte[] buffer2 = new byte[3];
        int read4 = device.read(buffer2, 1, 2);
        Assert.assertEquals("Expected to read one byte but got a different number of bytes!", 2, read4);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer2[1], I2CDeviceImplTest.READ_FIRSTBYTE);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer2[2], I2CDeviceImplTest.READ_SECONDBYTE);
        try {
            device.read(new byte[1], 0, 1);
            Assert.fail("Expected 'read(...)' to throw an exception but got none!");
        } catch (IOException e) {
            // expected
        }
        try {
            device.read(new byte[2], 1, 2);
            Assert.fail("Expected 'read(...)' to throw an exception but got none!");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        // read n bytes from register
        Mockito.when(bus.readBytes(ArgumentMatchers.eq(device), ArgumentMatchers.eq(I2CDeviceImplTest.LOCALADDRESS), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(byte[].class))).thenAnswer(readAnswer);
        byte[] buffer3 = new byte[2];
        int read5 = device.read(I2CDeviceImplTest.LOCALADDRESS, buffer3, 0, 2);
        Assert.assertEquals("Expected to read one byte but got a different number of bytes!", 2, read5);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer3[0], I2CDeviceImplTest.READ_FIRSTBYTE);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer3[1], I2CDeviceImplTest.READ_SECONDBYTE);
        byte[] buffer4 = new byte[3];
        int read6 = device.read(I2CDeviceImplTest.LOCALADDRESS, buffer4, 1, 2);
        Assert.assertEquals("Expected to read one byte but got a different number of bytes!", 2, read6);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer4[1], I2CDeviceImplTest.READ_FIRSTBYTE);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer4[2], I2CDeviceImplTest.READ_SECONDBYTE);
        try {
            device.read(I2CDeviceImplTest.LOCALADDRESS, new byte[1], 0, 1);
            Assert.fail("Expected 'read(...)' to throw an exception but got none!");
        } catch (IOException e) {
            // expected
        }
        try {
            device.read(I2CDeviceImplTest.LOCALADDRESS, new byte[2], 1, 2);
            Assert.fail("Expected 'read(...)' to throw an exception but got none!");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        // write and read bytes
        byte[] dataToBeWritten = new byte[]{ I2CDeviceImplTest.WRITE_FIRSTBYTE, I2CDeviceImplTest.WRITE_SECONDBYTE };
        Mockito.when(bus.writeAndReadBytesDirect(ArgumentMatchers.eq(device), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(byte[].class))).thenAnswer(writeAndReadAnswer);
        byte[] buffer5 = new byte[2];
        int read7 = device.read(dataToBeWritten, 0, 2, buffer5, 0, 2);
        Assert.assertEquals("Expected to read one byte but got a different number of bytes!", 2, read7);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer5[0], I2CDeviceImplTest.READ_FIRSTBYTE);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer5[1], I2CDeviceImplTest.READ_SECONDBYTE);
        byte[] buffer6 = new byte[3];
        int read8 = device.read(dataToBeWritten, 0, 2, buffer6, 1, 2);
        Assert.assertEquals("Expected to read one byte but got a different number of bytes!", 2, read8);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer6[1], I2CDeviceImplTest.READ_FIRSTBYTE);
        Assert.assertEquals("Unexpected bytes in buffer!", buffer6[2], I2CDeviceImplTest.READ_SECONDBYTE);
        try {
            device.read(new byte[1], 0, 1);
            Assert.fail("Expected 'read(...)' to throw an exception but got none!");
        } catch (IOException e) {
            // expected
        }
        try {
            device.read(new byte[2], 1, 2);
            Assert.fail("Expected 'read(...)' to throw an exception but got none!");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }
}

