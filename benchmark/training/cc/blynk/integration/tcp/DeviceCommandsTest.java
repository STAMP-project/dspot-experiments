package cc.blynk.integration.tcp;


import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceCommandsTest extends SingleServerInstancePerTest {
    @Test
    public void testAddNewDevice() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
        DeviceCommandsTest.assertEqualDevice(device1, devices[1]);
    }

    @Test
    public void testUpdateExistingDevice() throws Exception {
        Device device0 = new Device(0, "My Dashboard Updated", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.updateDevice(1, device0);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
    }

    @Test
    public void testUpdateNonExistingDevice() throws Exception {
        Device device = new Device(100, "My Dashboard Updated", BoardType.Arduino_UNO);
        clientPair.appClient.updateDevice(1, device);
        clientPair.appClient.verifyResult(TestUtil.illegalCommandBody(1));
    }

    @Test
    public void testGetDevices() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
    }

    @Test
    public void testTokenNotUpdatedForExistingDevice() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
        String token = devices[0].token;
        device0.name = "My Dashboard UPDATED";
        device0.token = "123";
        clientPair.appClient.updateDevice(1, device0);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
        Assert.assertEquals("My Dashboard UPDATED", devices[0].name);
        Assert.assertEquals(token, devices[0].token);
    }

    @Test
    public void testDeletedNewlyAddedDevice() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
        DeviceCommandsTest.assertEqualDevice(device1, devices[1]);
        clientPair.appClient.send(("deleteDevice 1\u0000" + (device1.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceCommandsTest.assertEqualDevice(device0, devices[0]);
    }
}

