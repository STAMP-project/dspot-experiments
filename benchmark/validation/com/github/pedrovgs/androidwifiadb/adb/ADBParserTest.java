/**
 * Copyright (C) 2015 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.androidwifiadb.adb;


import com.github.pedrovgs.androidwifiadb.Device;
import com.github.pedrovgs.androidwifiadb.UnitTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ADBParserTest extends UnitTest {
    private static final String ADB_DEVICES_OUTPUT_ONE_DEVICE = "List of devices attached\n" + "0810a0dd00e3656f    device usb:336592896X product:hammerhead model:Nexus_5 device:hammerhead";

    private static final String ADB_DEVICES_OUTPUT_SOME_DEVICES = "List of devices attached\n" + ("0810a0dd00e3656f    device usb:336592896X product:hammerhead model:Nexus_5 device:hammerhead\n" + "0810a0d333333656f    device usb:336592896X product:hammerhead model:Nexus_6");

    private static final String ADB_DEVICES_NO_DEVICES = "List of devices attached\n";

    private static final String ADB_DEVICES_OUTPUT_WITH_DEVICES_BY_IP = "List of devices attached\n" + ("0810a0dd00e3656f    device usb:336592896X product:hammerhead model:Nexus_5 device:hammerhead\n" + "192.168.1.128:5555f    device usb:336592896X product:hammerhead model:Nexus_6 device:hammerhead\n");

    private static final String GET_IP_OUTPUT = "21: wlan0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP qlen 1000\n" + "    inet 192.168.1.128/24 brd 192.168.1.255 scope global wlan0";

    private static final String GET_ADB_PROP_DEVICE_NOT_FOUND = "error: device '(null)' not found";

    private static final String GET_ADP_PROP_NO_TCP_PORT_INFO = "[init.svc.adbd]: [running]\n" + ((("[persist.sys.usb.config]: [mtp,adb]\n" + "[ro.adb.secure]: [1]\n") + "[sys.usb.config]: [mtp,adb]\n") + "[sys.usb.state]: [mtp,adb]");

    private static final String TCPIP_PORT = "5555";

    private static final String GET_ADB_PROP_WITH_TCP_PORT = (((("  [init.svc.adbd]: [running]\n" + (("  [persist.sys.usb.config]: [mtp,adb]\n" + "  [ro.adb.secure]: [1]\n") + "  [service.adb.tcp.port]: [")) + (ADBParserTest.TCPIP_PORT)) + "]\n") + "  [sys.usb.config]: [mtp,adb]\n") + "  [sys.usb.state]: [mtp,adb]\n";

    @Test
    public void shouldParseAdbDevicesOutputAndReturnTheListOfDevicesWithJustOneDeviceConnected() {
        ADBParser parser = givenAADBParser();
        List<Device> devices = parser.parseGetDevicesOutput(ADBParserTest.ADB_DEVICES_OUTPUT_ONE_DEVICE);
        Assert.assertEquals(1, devices.size());
        Device device = devices.get(0);
        Assert.assertEquals("0810a0dd00e3656f", device.getId());
        Assert.assertEquals("Nexus_5", device.getName());
    }

    @Test
    public void shouldParseAdbDevicesOutputAndReturnTheListOfDevicesWithMoreThanOneDevice() {
        ADBParser parser = givenAADBParser();
        List<Device> devices = parser.parseGetDevicesOutput(ADBParserTest.ADB_DEVICES_OUTPUT_SOME_DEVICES);
        Assert.assertEquals(2, devices.size());
        Device firstDevice = devices.get(0);
        Device secondDevice = devices.get(1);
        Assert.assertEquals("0810a0dd00e3656f", firstDevice.getId());
        Assert.assertEquals("Nexus_5", firstDevice.getName());
        Assert.assertEquals("0810a0d333333656f", secondDevice.getId());
        Assert.assertEquals("Nexus_6", secondDevice.getName());
    }

    @Test
    public void shouldReturnAnEmptyListIfThereAreNoDevices() {
        ADBParser parser = givenAADBParser();
        List<Device> devices = parser.parseGetDevicesOutput(ADBParserTest.ADB_DEVICES_NO_DEVICES);
        Assert.assertTrue(devices.isEmpty());
    }

    @Test
    public void shouldNotReturnDevicesConnectedByIp() {
        ADBParser parser = givenAADBParser();
        List<Device> devices = parser.parseGetDevicesOutput(ADBParserTest.ADB_DEVICES_OUTPUT_WITH_DEVICES_BY_IP);
        Assert.assertEquals(1, devices.size());
        Device device = devices.get(0);
        Assert.assertEquals("0810a0dd00e3656f", device.getId());
        Assert.assertEquals("Nexus_5", device.getName());
    }

    @Test
    public void shouldReturnDeviceIp() {
        ADBParser parser = givenAADBParser();
        String deviceIp = parser.parseGetDeviceIp(ADBParserTest.GET_IP_OUTPUT);
        Assert.assertEquals("192.168.1.128", deviceIp);
    }

    @Test
    public void shouldReturnEmptyIfDeviceIpIsAnEmptyString() {
        ADBParser parser = givenAADBParser();
        String deviceIp = parser.parseGetDeviceIp("");
        Assert.assertEquals("", deviceIp);
    }

    @Test
    public void shouldReturnEmptyStringIfDeviceNotFound() throws Exception {
        ADBParser parser = givenAADBParser();
        String tcpPort = parser.parseAdbServiceTcpPort(ADBParserTest.GET_ADB_PROP_DEVICE_NOT_FOUND);
        Assert.assertEquals("", tcpPort);
    }

    @Test
    public void shouldReturnEmptyStringIfAdbPropertiesDoesNotContainTCPPort() throws Exception {
        ADBParser parser = givenAADBParser();
        String tcpPort = parser.parseAdbServiceTcpPort(ADBParserTest.GET_ADP_PROP_NO_TCP_PORT_INFO);
        Assert.assertEquals("", tcpPort);
    }

    @Test
    public void shouldReturnTCPPort() throws Exception {
        ADBParser parser = givenAADBParser();
        String tcpPort = parser.parseAdbServiceTcpPort(ADBParserTest.GET_ADB_PROP_WITH_TCP_PORT);
        Assert.assertEquals(ADBParserTest.TCPIP_PORT, tcpPort);
    }
}

