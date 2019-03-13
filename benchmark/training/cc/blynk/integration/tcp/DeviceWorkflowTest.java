package cc.blynk.integration.tcp;


import GraphGranularityType.DAILY;
import GraphGranularityType.HOURLY;
import GraphGranularityType.MINUTE;
import PinType.DIGITAL;
import PinType.VIRTUAL;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.TemporaryTokenValue;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.device.Tag;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.controls.Terminal;
import cc.blynk.server.core.model.widgets.outputs.ValueDisplay;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
import cc.blynk.server.notifications.push.android.AndroidGCMMessage;
import cc.blynk.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceWorkflowTest extends SingleServerInstancePerTest {
    private static int tcpHardPort;

    @Test
    public void testSendHardwareCommandToMultipleDevices() throws Exception {
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
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceWorkflowTest.assertEqualDevice(device1, devices[1]);
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        device1.status = Status.ONLINE;
        clientPair.appClient.send("hardware 1 vw 100 100");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(2, "vw 100 100"));
        hardClient2.never(TestUtil.hardware(2, "vw 1 100"));
        clientPair.appClient.send("hardware 1-0 vw 100 101");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(3, "vw 100 101"));
        hardClient2.never(TestUtil.hardware(3, "vw 1 101"));
        clientPair.appClient.send("hardware 1-1 vw 100 102");
        hardClient2.verifyResult(TestUtil.hardware(4, "vw 100 102"));
        clientPair.hardwareClient.never(TestUtil.hardware(4, "vw 100 102"));
    }

    @Test
    public void testDeviceWentOfflineMessage() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        stop().await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(DeviceWorkflowTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testSendHardwareCommandToAppFromMultipleDevices() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        clientPair.hardwareClient.send("hardware vw 100 101");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 100 101"))));
        hardClient2.send("hardware vw 100 100");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(2, TestUtil.b("1-1 vw 100 100"))));
    }

    @Test
    public void testSendDeviceSpecificPMMessage() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":1}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(2, device)));
        TestHardClient hardClient = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient.login(device.token);
        hardClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        String expectedBody = "pm 1 out";
        Mockito.verify(hardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b(expectedBody))));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        stop().awaitUninterruptibly();
    }

    @Test
    public void testSendPMOnActivateForMultiDevices() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":33}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(2, device)));
        TestHardClient hardClient = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient.login(device.token);
        hardClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        Mockito.verify(hardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 33 out"))));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.deactivate(1);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
        hardClient.reset();
        clientPair.hardwareClient.reset();
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        Mockito.verify(hardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 33 out"))));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in"))));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        stop().awaitUninterruptibly();
    }

    @Test
    public void testActivateForMultiDevices() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.activate(1);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(1, DEVICE_NOT_IN_NETWORK)));
    }

    @Test
    public void testTagWorks() throws Exception {
        Tag tag = new Tag(100000, "My New Tag");
        tag.deviceIds = new int[]{ 1 };
        clientPair.appClient.createTag(1, tag);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createTag(1, tag)));
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":100000, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":33, \"value\":1}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice(3);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(3, device));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.send("hardware 1-100000 dw 33 1");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(3, TestUtil.b("dw 33 10"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("dw 33 1"))));
        tag.deviceIds = new int[]{ 0, 1 };
        clientPair.appClient.updateTag(1, tag);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("hardware 1-100000 dw 33 10");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(3, TestUtil.b("dw 33 10"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(3, TestUtil.b("dw 33 10"))));
    }

    @Test
    public void testActivateAndGetSyncForMultiDevices() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":188, \"width\":1, \"height\":1, \"deviceId\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":33, \"value\":1}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(2, device)));
        clientPair.appClient.reset();
        clientPair.appClient.activate(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
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
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.appSync(TestUtil.b("1-1 dw 33 1"))));
    }

    @Test
    public void testOfflineOnlineStatusForMultiDevices() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        device0.status = Status.ONLINE;
        device1.status = Status.ONLINE;
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(3);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceWorkflowTest.assertEqualDevice(device1, devices[1]);
        stop().await();
        device1.status = Status.OFFLINE;
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
        DeviceWorkflowTest.assertEqualDevice(device1, devices[1]);
    }

    @Test
    public void testCorrectOnlineStatusForDisconnect() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
        stop().await();
        device0.status = Status.OFFLINE;
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices(1);
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
        Assert.assertEquals(System.currentTimeMillis(), devices[0].disconnectTime, 5000);
    }

    @Test
    public void testCorrectConnectTime() throws Exception {
        long now = System.currentTimeMillis();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        Assert.assertEquals(now, devices[0].connectTime, 10000);
    }

    @Test
    public void testCorrectOnlineStatusForReconnect() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
        stop().await();
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(devices[0].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-0"));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        devices = clientPair.appClient.parseDevices();
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        DeviceWorkflowTest.assertEqualDevice(device0, devices[0]);
    }

    @Test
    public void testHardwareChannelClosedOnDashRemoval() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        long tries = 0;
        // waiting for channel to be closed.
        // but only limited amount if time
        while ((!(isClosed())) && (tries < 100)) {
            TestUtil.sleep(10);
            tries++;
        } 
        Assert.assertTrue(isClosed());
        Assert.assertTrue(isClosed());
    }

    @Test
    public void testHardwareChannelClosedOnDeviceRemoval() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(1, device)));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        String tempDir = DeviceWorkflowTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", CounterBase.getUserName());
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (8)), MINUTE));
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (8)), HOURLY));
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, DIGITAL, ((short) (8)), DAILY));
        Path pinReportingDataPath13 = Paths.get(tempDir, "data", CounterBase.getUserName(), ReportingDiskDao.generateFilename(1, 1, VIRTUAL, ((short) (9)), DAILY));
        FileUtils.write(pinReportingDataPath10, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath11, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath12, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath13, 1.11, 1111111);
        clientPair.appClient.send(("deleteDevice 1\u0000" + "1"));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Assert.assertFalse(isClosed());
        Assert.assertTrue(isClosed());
        Assert.assertTrue(Files.notExists(pinReportingDataPath10));
        Assert.assertTrue(Files.notExists(pinReportingDataPath11));
        Assert.assertTrue(Files.notExists(pinReportingDataPath12));
        Assert.assertTrue(Files.notExists(pinReportingDataPath13));
    }

    @Test
    public void testHardwareDataRemovedWhenDeviceRemoved() throws Exception {
        clientPair.appClient.createDevice(1, new Device(1, "My Device", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        ValueDisplay valueDisplay = new ValueDisplay();
        valueDisplay.id = 11111;
        valueDisplay.x = 1;
        valueDisplay.y = 2;
        valueDisplay.height = 1;
        valueDisplay.width = 1;
        valueDisplay.deviceId = 1;
        valueDisplay.pin = 1;
        valueDisplay.pinType = PinType.VIRTUAL;
        clientPair.appClient.createWidget(1, valueDisplay);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Terminal terminal = new Terminal();
        terminal.id = 11112;
        terminal.x = 1;
        terminal.y = 2;
        terminal.height = 1;
        terminal.width = 1;
        terminal.deviceId = 1;
        terminal.pin = 3;
        terminal.pinType = PinType.VIRTUAL;
        clientPair.appClient.createWidget(1, terminal);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        hardClient2.send("hardware vw 1 123");
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-1 vw 1 123"));
        hardClient2.send("hardware vw 2 124");
        clientPair.appClient.verifyResult(TestUtil.hardware(3, "1-1 vw 2 124"));
        hardClient2.send("hardware vw 3 125");
        clientPair.appClient.verifyResult(TestUtil.hardware(4, "1-1 vw 3 125"));
        hardClient2.send("hardware vw 3 126");
        clientPair.appClient.verifyResult(TestUtil.hardware(5, "1-1 vw 3 126"));
        clientPair.appClient.send(("deleteDevice 1\u0000" + "1"));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(4)));
        clientPair.appClient.sync(1, 1);
        clientPair.appClient.neverAfter(500, TestUtil.appSync(1111, "1-1 vw 1 123"));
        clientPair.appClient.never(TestUtil.appSync(1111, "1-1 vw 2 124"));
        clientPair.appClient.never(TestUtil.appSync(1111, "1-1 vw 3 125"));
        clientPair.appClient.never(TestUtil.appSync(1111, "1-1 vw 3 126"));
    }

    @Test
    public void testTemporaryTokenWorksAsExpected() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.getProvisionToken(1, device1);
        device1 = clientPair.appClient.parseDevice(1);
        Assert.assertNotNull(device1);
        Assert.assertEquals(1, device1.id);
        Assert.assertEquals(32, device1.token.length());
        clientPair.appClient.send("loadProfileGzipped 1");
        DashBoard dash = clientPair.appClient.parseDash(2);
        Assert.assertNotNull(dash);
        Assert.assertEquals(1, dash.devices.length);
        Assert.assertTrue(((DeviceWorkflowTest.holder.tokenManager.getTokenValueByToken(device1.token)) instanceof TemporaryTokenValue));
        TestHardClient hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device1.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        clientPair.appClient.send("loadProfileGzipped 1");
        dash = clientPair.appClient.parseDash(4);
        Assert.assertNotNull(dash);
        Assert.assertEquals(2, dash.devices.length);
        clientPair.appClient.reset();
        hardClient2 = new TestHardClient("localhost", DeviceWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device1.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "1-1"));
        clientPair.appClient.send("loadProfileGzipped 1");
        dash = clientPair.appClient.parseDash(2);
        Assert.assertNotNull(dash);
        Assert.assertEquals(2, dash.devices.length);
        Assert.assertFalse(((DeviceWorkflowTest.holder.tokenManager.getTokenValueByToken(device1.token)) instanceof TemporaryTokenValue));
        Assert.assertFalse(DeviceWorkflowTest.holder.tokenManager.clearTemporaryTokens());
    }

    @Test
    public void testCorrectRemovalForTags() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        Tag tag1 = new Tag(100001, "tag1");
        Tag tag2 = new Tag(100002, "tag2", new int[]{ 0 });
        Tag tag3 = new Tag(100003, "tag3", new int[]{ 1 });
        Tag tag4 = new Tag(100004, "tag4", new int[]{ 0, 1 });
        clientPair.appClient.createTag(1, tag1);
        clientPair.appClient.createTag(1, tag2);
        clientPair.appClient.createTag(1, tag3);
        clientPair.appClient.createTag(1, tag4);
        clientPair.appClient.verifyResult(TestUtil.createTag(2, tag1));
        clientPair.appClient.verifyResult(TestUtil.createTag(3, tag2));
        clientPair.appClient.verifyResult(TestUtil.createTag(4, tag3));
        clientPair.appClient.verifyResult(TestUtil.createTag(5, tag4));
        clientPair.appClient.deleteDevice(1, 1);
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        clientPair.appClient.send("getTags 1");
        Tag[] tags = clientPair.appClient.parseTags(7);
        Assert.assertNotNull(tags);
        Assert.assertEquals(100001, tags[0].id);
        Assert.assertEquals(0, tags[0].deviceIds.length);
        Assert.assertEquals(100002, tags[1].id);
        Assert.assertEquals(1, tags[1].deviceIds.length);
        Assert.assertEquals(0, tags[1].deviceIds[0]);
        Assert.assertEquals(100003, tags[2].id);
        Assert.assertEquals(0, tags[2].deviceIds.length);
        Assert.assertEquals(100004, tags[3].id);
        Assert.assertEquals(1, tags[3].deviceIds.length);
        Assert.assertEquals(0, tags[3].deviceIds[0]);
    }

    @Test
    public void testCorrectRemovalForDeviceSelector() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = 21321;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        PageTileTemplate tileTemplate = new PageTileTemplate(1, null, null, "name", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        deviceTiles = new DeviceTiles();
        deviceTiles.id = 21322;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        tileTemplate = new PageTileTemplate(1, null, new int[]{ 0 }, "name", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.send(("addEnergy " + (("100000" + "\u0000") + "1370-3990-1414-55681")));
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        deviceTiles = new DeviceTiles();
        deviceTiles.id = 21323;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        tileTemplate = new PageTileTemplate(1, null, new int[]{ 1 }, "name", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(TestUtil.ok(8));
        deviceTiles = new DeviceTiles();
        deviceTiles.id = 21324;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(9));
        tileTemplate = new PageTileTemplate(1, null, new int[]{ 0, 1 }, "name", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        clientPair.appClient.verifyResult(TestUtil.ok(10));
        clientPair.appClient.deleteDevice(1, 1);
        clientPair.appClient.verifyResult(TestUtil.ok(11));
        clientPair.appClient.getWidget(1, 21321);
        deviceTiles = ((DeviceTiles) (clientPair.appClient.parseWidget(12)));
        Assert.assertNotNull(deviceTiles);
        Assert.assertEquals(21321, deviceTiles.id);
        Assert.assertEquals(0, deviceTiles.tiles.length);
        Assert.assertEquals(0, deviceTiles.templates[0].deviceIds.length);
        clientPair.appClient.getWidget(1, 21322);
        deviceTiles = ((DeviceTiles) (clientPair.appClient.parseWidget(13)));
        Assert.assertNotNull(deviceTiles);
        Assert.assertEquals(21322, deviceTiles.id);
        Assert.assertEquals(1, deviceTiles.tiles.length);
        Assert.assertEquals(1, deviceTiles.templates[0].deviceIds.length);
        Assert.assertEquals(0, deviceTiles.templates[0].deviceIds[0]);
        clientPair.appClient.getWidget(1, 21323);
        deviceTiles = ((DeviceTiles) (clientPair.appClient.parseWidget(14)));
        Assert.assertNotNull(deviceTiles);
        Assert.assertEquals(21323, deviceTiles.id);
        Assert.assertEquals(0, deviceTiles.tiles.length);
        Assert.assertEquals(0, deviceTiles.templates[0].deviceIds.length);
        clientPair.appClient.getWidget(1, 21324);
        deviceTiles = ((DeviceTiles) (clientPair.appClient.parseWidget(15)));
        Assert.assertNotNull(deviceTiles);
        Assert.assertEquals(21324, deviceTiles.id);
        Assert.assertEquals(1, deviceTiles.tiles.length);
        Assert.assertEquals(1, deviceTiles.templates[0].deviceIds.length);
        Assert.assertEquals(0, deviceTiles.templates[0].deviceIds[0]);
    }
}

