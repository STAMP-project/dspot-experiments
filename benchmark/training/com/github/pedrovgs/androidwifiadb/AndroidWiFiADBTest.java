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
package com.github.pedrovgs.androidwifiadb;


import com.github.pedrovgs.androidwifiadb.adb.ADB;
import com.github.pedrovgs.androidwifiadb.view.View;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AndroidWiFiADBTest extends UnitTest {
    private static final int SOME_DEVICES = 4;

    private static final String ANY_DEVICE_NAME = "Device n? ";

    private static final String ANY_DEVICE_ID = "abcdef123";

    private static final String ANY_DEVICE_IP = "0.0.0.0";

    @Mock
    private ADB adb;

    @Mock
    private View view;

    @Test
    public void shouldShowErrorIfADBIsNotInstalled() {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        givenADBIsNotInstalled();
        androidWiFiAdb.connectDevices();
        Mockito.verify(view).showADBNotInstalledNotification();
    }

    @Test
    public void shouldShowNoConnectedDevicesNotificationIfThereAreNotConnectedDevicesByUSB() {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        givenThereAreNoConnectedDevices();
        androidWiFiAdb.connectDevices();
        Mockito.verify(view).showNoConnectedDevicesNotification();
    }

    @Test
    public void shouldShowDevicesConnectedIfADBWiFiWhenConnectionIsEstablished() {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        List<Device> devices = givenThereAreSomeDevicesConnectedByUSB();
        givenDevicesAreConnectedSuccessfully(devices);
        androidWiFiAdb.connectDevices();
        for (Device device : devices) {
            Mockito.verify(view).showConnectedDeviceNotification(device);
        }
    }

    @Test
    public void shouldShowDeviceConnectionErrorWhenConnectionIsNotEstablished() {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        List<Device> devices = givenThereAreSomeDevicesConnectedByUSB();
        givenDevicesAreNotConnectedSuccessfully(devices);
        androidWiFiAdb.connectDevices();
        for (Device device : devices) {
            Mockito.verify(view).showErrorConnectingDeviceNotification(device);
        }
    }

    @Test
    public void shouldNotRefreshDevicesListIfAdbIsNotIstalled() throws Exception {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        givenADBIsNotInstalled();
        Assert.assertFalse(androidWiFiAdb.refreshDevicesList());
    }

    @Test
    public void shouldRefreshDevicesListAddNewDevice() throws Exception {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        List<Device> devices = givenThereAreSomeDevicesConnectedByUSB();
        givenDevicesAreConnectedSuccessfully(devices);
        givenAnyIpToDevices();
        Assert.assertEquals(0, androidWiFiAdb.getDevices().size());
        androidWiFiAdb.refreshDevicesList();
        Assert.assertEquals(devices.size(), androidWiFiAdb.getDevices().size());
    }

    @Test
    public void shouldRefreshDevicesListUpdateExistingDevices() throws Exception {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        List<Device> devices = givenThereAreSomeDevicesConnectedByUSB();
        givenDevicesAreConnectedSuccessfully(devices);
        androidWiFiAdb.connectDevices();
        androidWiFiAdb.refreshDevicesList();
        Assert.assertEquals(devices.size(), androidWiFiAdb.getDevices().size());
    }

    @Test
    public void shouldDisconnectDevice() throws Exception {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        givenADBIsInstalled();
        Device device = givenAnyConnectedDevice();
        givenDevicesAreDisconnectedSuccessfully(Arrays.asList(device));
        androidWiFiAdb.disconnectDevice(device);
        Assert.assertFalse(device.isConnected());
    }

    @Test
    public void shouldConnectDevice() throws Exception {
        AndroidWiFiADB androidWiFiAdb = givenAnAndroidWiFiADB();
        givenADBIsInstalled();
        Device device = givenAnyDisonnectedDevice();
        givenDevicesAreConnectedSuccessfully(Arrays.asList(device));
        androidWiFiAdb.connectDevice(device);
        Assert.assertTrue(device.isConnected());
    }
}

