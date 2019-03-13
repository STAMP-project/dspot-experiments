/**
 * Copyright (C) 2017 Genymobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.genymobile.gnirehtet;


import org.junit.Assert;
import org.junit.Test;


public class AdbMonitorTest {
    @Test
    public void testReadValidPacket() {
        String data = "00180123456789ABCDEF\tdevice\n";
        String result = AdbMonitor.readPacket(AdbMonitorTest.toByteBuffer(data));
        Assert.assertEquals("0123456789ABCDEF\tdevice\n", result);
    }

    @Test
    public void testReadValidPackets() {
        String data = "00300123456789ABCDEF\tdevice\nFEDCBA9876543210\tdevice\n";
        String result = AdbMonitor.readPacket(AdbMonitorTest.toByteBuffer(data));
        Assert.assertEquals("0123456789ABCDEF\tdevice\nFEDCBA9876543210\tdevice\n", result);
    }

    @Test
    public void testReadValidPacketWithGarbage() {
        String data = "00180123456789ABCDEF\tdevice\ngarbage";
        String result = AdbMonitor.readPacket(AdbMonitorTest.toByteBuffer(data));
        Assert.assertEquals("0123456789ABCDEF\tdevice\n", result);
    }

    @Test
    public void testReadShortPacket() {
        String data = "00180123456789ABCDEF\tdevi";
        String result = AdbMonitor.readPacket(AdbMonitorTest.toByteBuffer(data));
        Assert.assertNull(result);
    }

    @Test
    public void testHandlePacketDevice() {
        final String[] pSerial = new String[1];
        AdbMonitor monitor = new AdbMonitor(( serial) -> pSerial[0] = serial);
        String packet = "0123456789ABCDEF\tdevice\n";
        monitor.handlePacket(packet);
        Assert.assertEquals("0123456789ABCDEF", pSerial[0]);
    }

    @Test
    public void testHandlePacketOffline() {
        final String[] pSerial = new String[1];
        AdbMonitor monitor = new AdbMonitor(( serial) -> pSerial[0] = serial);
        String packet = "0123456789ABCDEF\toffline\n";
        monitor.handlePacket(packet);
        Assert.assertNull(pSerial[0]);
    }

    @Test
    public void testMultipleConnectedDevices() {
        final String[] pSerials = new String[2];
        AdbMonitor monitor = new AdbMonitor(new AdbMonitor.AdbDevicesCallback() {
            private int i;

            @Override
            public void onNewDeviceConnected(String serial) {
                pSerials[((i)++)] = serial;
            }
        });
        String packet = "0123456789ABCDEF\tdevice\nFEDCBA9876543210\tdevice\n";
        monitor.handlePacket(packet);
        Assert.assertEquals("0123456789ABCDEF", pSerials[0]);
        Assert.assertEquals("FEDCBA9876543210", pSerials[1]);
    }

    @Test
    @SuppressWarnings("checkstyle:MagicNumber")
    public void testMultipleConnectedDevicesWithDisconnection() {
        final String[] pSerials = new String[3];
        AdbMonitor monitor = new AdbMonitor(new AdbMonitor.AdbDevicesCallback() {
            private int i;

            @Override
            public void onNewDeviceConnected(String serial) {
                pSerials[((i)++)] = serial;
            }
        });
        String packet = "0123456789ABCDEF\tdevice\nFEDCBA9876543210\tdevice\n";
        monitor.handlePacket(packet);
        packet = "0123456789ABCDEF\tdevice\n";
        monitor.handlePacket(packet);
        packet = "0123456789ABCDEF\tdevice\nFEDCBA9876543210\tdevice\n";
        monitor.handlePacket(packet);
        Assert.assertEquals("0123456789ABCDEF", pSerials[0]);
        Assert.assertEquals("FEDCBA9876543210", pSerials[1]);
        Assert.assertEquals("FEDCBA9876543210", pSerials[2]);
    }
}

