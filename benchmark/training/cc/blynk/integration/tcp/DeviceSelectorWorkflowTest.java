package cc.blynk.integration.tcp;


import cc.blynk.integration.CounterBase;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.controls.Terminal;
import cc.blynk.server.core.model.widgets.outputs.LCD;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.table.Table;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceSelectorWorkflowTest extends SingleServerInstancePerTest {
    private static int tcpHardPort;

    @Test
    public void testSendHardwareCommandViaDeviceSelector() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceSelectorWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        device1.status = Status.ONLINE;
        clientPair.appClient.send("hardware 1-200000 vw 88 1");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(2, "vw 88 1"));
        hardClient2.never(TestUtil.hardware(2, "vw 88 1"));
        // change device
        clientPair.appClient.send("hardware 1 vu 200000 1");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.never(TestUtil.hardware(3, "vu 200000 1"));
        hardClient2.never(TestUtil.hardware(3, "vu 200000 1"));
        clientPair.appClient.send("hardware 1-200000 vw 88 2");
        clientPair.hardwareClient.never(TestUtil.hardware(4, "vw 88 2"));
        hardClient2.verifyResult(TestUtil.hardware(4, "vw 88 2"));
        // change device back
        clientPair.appClient.send("hardware 1 vu 200000 0");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.never(TestUtil.hardware(5, "vu 200000 0"));
        hardClient2.never(TestUtil.hardware(5, "vu 200000 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync(1111, TestUtil.b("1-0 vw 88 1")));
        clientPair.appClient.send("hardware 1-200000 vw 88 0");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(6, "vw 88 0"));
        hardClient2.never(TestUtil.hardware(6, "vw 88 0"));
    }

    @Test
    public void testSendHardwareCommandViaDeviceSelectorInSharedApp() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send("getShareToken 1");
        String sharedToken = clientPair.appClient.getBody(4);
        Assert.assertNotNull(sharedToken);
        Assert.assertEquals(32, sharedToken.length());
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceSelectorWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        device1.status = Status.ONLINE;
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        // login with shared app
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + sharedToken) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("hardware 1-200000 vw 88 1");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(2, "vw 88 1"));
        hardClient2.never(TestUtil.hardware(2, "vw 88 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync(2, TestUtil.b("1-200000 vw 88 1")));
        clientPair.hardwareClient.send("hardware vw 88 value_from_device_0");
        hardClient2.send("hardware vw 88 value_from_device_1");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 88 value_from_device_0"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-1 vw 88 value_from_device_1"));
        appClient2.verifyResult(TestUtil.hardware(1, "1-0 vw 88 value_from_device_0"));
        appClient2.verifyResult(TestUtil.hardware(2, "1-1 vw 88 value_from_device_1"));
        // change device
        appClient2.send("hardware 1 vu 200000 1");
        appClient2.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.never(TestUtil.hardware(3, "vu 200000 1"));
        hardClient2.never(TestUtil.hardware(3, "vu 200000 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync(3, TestUtil.b("1 vu 200000 1")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-1 vw 88 value_from_device_1")));
        appClient2.send("hardware 1-200000 vw 88 2");
        clientPair.hardwareClient.never(TestUtil.hardware(4, "vw 88 2"));
        hardClient2.verifyResult(TestUtil.hardware(4, "vw 88 2"));
        clientPair.appClient.verifyResult(TestUtil.appSync(4, TestUtil.b("1-200000 vw 88 2")));
        // change device back
        appClient2.send("hardware 1 vu 200000 0");
        appClient2.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.never(TestUtil.hardware(5, "vu 200000 0"));
        hardClient2.never(TestUtil.hardware(5, "vu 200000 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync(5, TestUtil.b("1 vu 200000 0")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 88 value_from_device_0")));
        appClient2.send("hardware 1-200000 vw 88 0");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(6, "vw 88 0"));
        hardClient2.never(TestUtil.hardware(6, "vw 88 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync(6, TestUtil.b("1-200000 vw 88 0")));
    }

    @Test
    public void testSetPropertyIsSentForDeviceSelectorWidget() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.createWidget(1, "{\"id\":89, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Display\", \"type\":\"DIGIT4_DISPLAY\", \"pinType\":\"VIRTUAL\", \"pin\":89}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        clientPair.hardwareClient.setProperty(89, "label", "123");
        clientPair.appClient.verifyResult(TestUtil.setProperty(1, "1-0 89 label 123"));
    }

    @Test
    public void testSetPropertyIsSentForDeviceSelectorWidgetOnActivateForExistingWidget() throws Exception {
        testSetPropertyIsSentForDeviceSelectorWidget();
        clientPair.hardwareClient.send("hardware vw 89 1");
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 89 1"));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.setProperty(1111, "1-0 89 label 123"));
        clientPair.appClient.verifyResult(TestUtil.appSync(1111, TestUtil.b("1-0 vw 89 1")));
    }

    @Test
    public void testSetPropertyIsRememberedBetweenDevices() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        clientPair.hardwareClient.setProperty(88, "label", "123");
        clientPair.appClient.verifyResult(TestUtil.setProperty(1, "1-0 88 label 123"));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceSelectorWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        hardClient2.setProperty(88, "label", "124");
        clientPair.appClient.verifyResult(TestUtil.setProperty(2, "1-1 88 label 124"));
        clientPair.appClient.send("hardware 1 vu 200000 1");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.setProperty(1111, "1-1 88 label 124"));
    }

    @Test
    public void testBasicSelectorWorkflow() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.createWidget(1, "{\"id\":89, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Display\", \"type\":\"DIGIT4_DISPLAY\", \"pinType\":\"VIRTUAL\", \"pin\":89}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceSelectorWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        device1.status = Status.ONLINE;
        clientPair.hardwareClient.send("hardware vw 89 value_from_device_0");
        hardClient2.send("hardware vw 89 value_from_device_1");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 89 value_from_device_0"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-1 vw 89 value_from_device_1"));
        clientPair.appClient.send("hardware 1 vw 88 100");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(2, "vw 88 100"));
        // change device, expecting syncs and OK
        clientPair.appClient.send("hardware 1 vu 200000 1");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.never(TestUtil.hardware(3, "vu 200000 1"));
        hardClient2.never(TestUtil.hardware(3, "vu 200000 1"));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-1 vw 89 value_from_device_1")));
        // switch device back, expecting syncs and OK
        clientPair.appClient.send("hardware 1 vu 200000 0");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.hardwareClient.never(TestUtil.hardware(4, "vu 200000 0"));
        hardClient2.never(TestUtil.hardware(4, "vu 200000 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 89 value_from_device_0")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 88 100")));
    }

    @Test
    public void testDeviceSelectorSyncTimeInput() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, ("{\"type\":\"TIME_INPUT\",\"id\":99, \"pin\":99, \"pinType\":\"VIRTUAL\", " + "\"x\":0,\"y\":0,\"width\":1,\"height\":1, \"deviceId\":200000}"));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceSelectorWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        device1.status = Status.ONLINE;
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 82800 82860 Europe/Kiev 1"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800 82860 Europe/Kiev 1")));
        // change device, expecting syncs and OK
        clientPair.appClient.send("hardware 1 vu 200000 1");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(3, "vu 200000 1")));
        Mockito.verify(hardClient2.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(3, "vu 200000 1")));
        // switch device back, expecting syncs and OK
        clientPair.appClient.send("hardware 1 vu 200000 0");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(4, "vu 200000 0")));
        Mockito.verify(hardClient2.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(4, "vu 200000 0")));
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 dw 1 1")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 dw 2 1")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 aw 3 0")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 dw 5 1")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 4 244")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 aw 7 3")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 aw 30 3")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 0 89.888037459418")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 11 -58.74774244674501")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 13 60 143 158")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 99 82800 82860 Europe/Kiev 1")));
    }

    @Test
    public void testNoSyncForDeviceSelectorWidget() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send("hardware 1 vw 88 100");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(4, "vw 88 100"));
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.never(TestUtil.appSync(TestUtil.b("1-200000 vw 88 100")));
        clientPair.appClient.verifyResult(TestUtil.appSync(TestUtil.b("1-0 vw 88 100")));
    }

    @Test
    public void testDeviceSelectorWorksAfterDeviceRemoval() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"value\":0, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.createWidget(1, "{\"id\":89, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Display\", \"type\":\"DIGIT4_DISPLAY\", \"pinType\":\"VIRTUAL\", \"pin\":89}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceSelectorWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceSelectorWorkflowTest.assertEqualDevice(device1, devices[1]);
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceSelectorWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1-200000 vw 88 100");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(2, "vw 88 100"));
        // change device, expecting syncs and OK
        clientPair.appClient.send("hardware 1 vu 200000 1");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.never(TestUtil.hardware(3, "vu 200000 1"));
        clientPair.appClient.send("hardware 1-200000 vw 88 101");
        hardClient2.verifyResult(TestUtil.hardware(4, "vw 88 101"));
        clientPair.appClient.send(("deleteDevice 1\u0000" + "1"));
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        // channel should be closed. so will not receive message
        clientPair.appClient.send("hardware 1-200000 vw 88 102");
        Mockito.verify(hardClient2.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(5, "vw 88 100")));
    }

    @Test
    public void terminalWithDeviceSelectorStoreMultipleCommands() throws Exception {
        var device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        var device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        var device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        var deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.x = 0;
        deviceSelector.y = 0;
        deviceSelector.width = 1;
        deviceSelector.height = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        var terminal = new Terminal();
        terminal.id = 88;
        terminal.width = 1;
        terminal.height = 1;
        terminal.deviceId = ((int) (deviceSelector.id));
        terminal.pinType = PinType.VIRTUAL;
        terminal.pin = 88;
        clientPair.appClient.createWidget(1, deviceSelector);
        clientPair.appClient.createWidget(1, terminal);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        for (var i = 1; i <= 26; i++) {
            clientPair.hardwareClient.send(("hardware vw 88 " + i));
            clientPair.appClient.verifyResult(TestUtil.hardware(i, ("1-0 vw 88 " + i)));
        }
        clientPair.appClient.reset();
        clientPair.appClient.sync(1, 0);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // expecting 25 syncs and not 26
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        for (var i = 2; i <= 26; i++) {
            clientPair.appClient.verifyResult(TestUtil.appSync(("1-0 vw 88 " + i)));
        }
    }

    @Test
    public void TableWithDeviceSelectorStoreMultipleCommands() throws Exception {
        var device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        var device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        var device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        var deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.x = 0;
        deviceSelector.y = 0;
        deviceSelector.width = 1;
        deviceSelector.height = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        var table = new Table();
        table.id = 88;
        table.width = 1;
        table.height = 1;
        table.deviceId = ((int) (deviceSelector.id));
        table.pinType = PinType.VIRTUAL;
        table.pin = 88;
        clientPair.appClient.createWidget(1, deviceSelector);
        clientPair.appClient.createWidget(1, table);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        for (var i = 1; i <= 101; i++) {
            clientPair.hardwareClient.send(("hardware vw 88 " + i));
            clientPair.appClient.verifyResult(TestUtil.hardware(i, ("1-0 vw 88 " + i)));
        }
        clientPair.appClient.reset();
        clientPair.appClient.sync(1, 0);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // expecting 25 syncs and not 26
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        for (var i = 2; i <= 101; i++) {
            clientPair.appClient.verifyResult(TestUtil.appSync(("1-0 vw 88 " + i)));
        }
    }

    @Test
    public void LCDWithDeviceSelectorStoreMultipleCommands() throws Exception {
        var device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        var device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        var device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        var deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.x = 0;
        deviceSelector.y = 0;
        deviceSelector.width = 1;
        deviceSelector.height = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        var lcd = new LCD();
        lcd.id = 88;
        lcd.width = 1;
        lcd.height = 1;
        lcd.deviceId = ((int) (deviceSelector.id));
        lcd.dataStreams = new DataStream[]{ new DataStream(((short) (88)), PinType.VIRTUAL) };
        clientPair.appClient.createWidget(1, deviceSelector);
        clientPair.appClient.createWidget(1, lcd);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        for (var i = 1; i <= 7; i++) {
            clientPair.hardwareClient.send(("hardware vw 88 " + i));
            clientPair.appClient.verifyResult(TestUtil.hardware(i, ("1-0 vw 88 " + i)));
        }
        clientPair.appClient.reset();
        clientPair.appClient.sync(1, 0);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        // expecting 25 syncs and not 26
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        for (var i = 2; i <= 7; i++) {
            clientPair.appClient.verifyResult(TestUtil.appSync(("1-0 vw 88 " + i)));
        }
    }

    @Test
    public void testDeviceSelectorForSharedApp() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"STEP\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody(4);
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        // change device
        clientPair.appClient.send("hardware 1 vu 200000 1");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.never(TestUtil.hardware(5, "vu 200000 1"));
        appClient2.verifyResult(TestUtil.appSync(5, "1 vu 200000 1"));
        appClient2.send("hardware 1 vu 200000 0");
        appClient2.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.never(TestUtil.hardware(2, "vu 200000 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync(2, "1 vu 200000 0"));
    }
}

