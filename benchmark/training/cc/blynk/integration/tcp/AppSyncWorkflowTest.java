package cc.blynk.integration.tcp;


import cc.blynk.integration.CounterBase;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.outputs.ValueDisplay;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.DeviceSelector;
import cc.blynk.server.core.model.widgets.ui.table.Table;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
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
public class AppSyncWorkflowTest extends SingleServerInstancePerTest {
    @Test
    public void testLCDOnActivateSendsCorrectBodySimpleMode() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"LCD\",\"id\":1923810267,\"x\":0,\"y\":6,\"color\":600084223,\"width\":8,\"height\":2,\"tabId\":0,\"" + ((("pins\":[" + "{\"pin\":10,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023, \"value\":\"10\"},") + "{\"pin\":11,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023, \"value\":\"11\"}],") + "\"advancedMode\":false,\"textLight\":false,\"textLightOn\":false,\"frequency\":1000}")));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 10 10"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 11"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
    }

    @Test
    public void testLCDOnActivateSendsCorrectBodyAdvancedMode() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"LCD\",\"id\":1923810267,\"x\":0,\"y\":6,\"color\":600084223,\"width\":8,\"height\":2,\"tabId\":0,\"" + ((("pins\":[" + "{\"pin\":10,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023},") + "{\"pin\":11,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023}],") + "\"advancedMode\":true,\"textLight\":false,\"textLightOn\":false,\"frequency\":1000}")));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 10 p x y 10");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 10 p x y 10"));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync(1111, "1-0 vw 10 p x y 10"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
    }

    @Test
    public void testTerminalSendsSyncOnActivate() throws Exception {
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(2, GET_ENERGY, "7500"));
        clientPair.appClient.createWidget(1, "{\"id\":102, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":17}");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.send("hardware vw 17 a");
        clientPair.hardwareClient.send("hardware vw 17 b");
        clientPair.hardwareClient.send("hardware vw 17 c");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 17 a"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 17 b"));
        clientPair.appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 17 c"));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 a"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 b"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 c"));
    }

    @Test
    public void testTerminalStorageRemembersCommands() throws Exception {
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(2, GET_ENERGY, "7500"));
        clientPair.appClient.createWidget(1, "{\"id\":102, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":17}");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.send("hardware vw 17 1");
        clientPair.hardwareClient.send("hardware vw 17 2");
        clientPair.hardwareClient.send("hardware vw 17 3");
        clientPair.hardwareClient.send("hardware vw 17 4");
        clientPair.hardwareClient.send("hardware vw 17 dddyyyiii");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(5, "1-0 vw 17 dddyyyiii")));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 2"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 4"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 17 dddyyyiii"));
    }

    @Test
    public void testTerminalStorageRemembersCommandsInNewFormat() throws Exception {
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        appClient.createWidget(1, "{\"id\":102, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(4));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.activate(1);
        appClient.verifyResult(TestUtil.ok(5));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1 2 3 4 dddyyyiii"));
    }

    @Test
    public void testTableStorageRemembersCommandsInNewFormat() throws Exception {
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        Table table = new Table();
        table.id = 102;
        table.width = 2;
        table.height = 2;
        table.pin = 56;
        table.pinType = PinType.VIRTUAL;
        table.deviceId = 0;
        appClient.createWidget(1, table);
        appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.send("hardware vw 56 add 1 Row1 row1");
        clientPair.hardwareClient.send("hardware vw 56 add 2 Row2 row2");
        clientPair.hardwareClient.send("hardware vw 56 add 3 Row3 row3");
        clientPair.hardwareClient.send("hardware vw 56 add 4 Row4 row4");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 add 1 Row1 row1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 add 2 Row2 row2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 add 3 Row3 row3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 add 4 Row4 row4"));
        appClient.activate(1);
        appClient.verifyResult(TestUtil.ok(4));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 add 1 Row1 row1 true add 2 Row2 row2 true add 3 Row3 row3 true add 4 Row4 row4 true"));
    }

    @Test
    public void testTerminalAndAnotherWidgetOnTheSamePin() throws Exception {
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        appClient.createWidget(1, "{\"id\":102, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.createWidget(1, "{\"id\":103, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"DIGIT4_DISPLAY\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(4));
        appClient.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.activate(1);
        appClient.verifyResult(TestUtil.ok(6));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1 2 3 4 dddyyyiii"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 dddyyyiii"));
    }

    @Test
    public void testTerminalAndAnotherWidgetOnTheSamePinAndDeviceSelector() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        DeviceSelector deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.height = 4;
        deviceSelector.width = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        appClient.createWidget(1, deviceSelector);
        appClient.verifyResult(TestUtil.ok(4));
        appClient.createWidget(1, "{\"id\":103, \"deviceId\":200000, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"DIGIT4_DISPLAY\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(5));
        appClient.createWidget(1, "{\"id\":102, \"deviceId\":200000, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(6));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(7));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 dddyyyiii"));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1 2 3 4 dddyyyiii"));
    }

    @Test
    public void testTerminalAndAnotherWidgetOnTheSamePinAndDeviceSelectorAnotherOrder() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        DeviceSelector deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.height = 4;
        deviceSelector.width = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        appClient.createWidget(1, deviceSelector);
        appClient.verifyResult(TestUtil.ok(4));
        appClient.createWidget(1, "{\"id\":102, \"deviceId\":200000, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(5));
        appClient.createWidget(1, "{\"id\":103, \"deviceId\":200000, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"DIGIT4_DISPLAY\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(6));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(7));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 dddyyyiii"));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1 2 3 4 dddyyyiii"));
    }

    @Test
    public void testTerminalStorageRemembersCommandsInOldFormatAndDeviceSelector() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.25.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        DeviceSelector deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.height = 4;
        deviceSelector.width = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        appClient.createWidget(1, deviceSelector);
        appClient.verifyResult(TestUtil.ok(4));
        appClient.createWidget(1, "{\"id\":102, \"deviceId\":200000, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(6));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 56 dddyyyiii"));
    }

    @Test
    public void testTerminalStorageRemembersCommandsInNewFormatAndDeviceTiles() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = 21321;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(TestUtil.ok(4));
        TileTemplate tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, new int[]{ 0 }, "name", "name", "iconName", BoardType.ESP8266, new cc.blynk.server.core.model.DataStream(((short) (1)), PinType.VIRTUAL), false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        appClient.verifyResult(TestUtil.ok(5));
        appClient.createWidget(1, deviceTiles.id, tileTemplate.id, "{\"id\":102, \"deviceId\":-1, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(6));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(7));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1 2 3 4 dddyyyiii"));
    }

    @Test
    public void testTerminalStorageCleanedAfterTilesAreRemoved() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = 21321;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        appClient.createWidget(1, deviceTiles);
        appClient.verifyResult(TestUtil.ok(4));
        TileTemplate tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, new int[]{ 0 }, "name", "name", "iconName", BoardType.ESP8266, new cc.blynk.server.core.model.DataStream(((short) (1)), PinType.VIRTUAL), false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        appClient.createTemplate(1, deviceTiles.id, tileTemplate);
        appClient.verifyResult(TestUtil.ok(5));
        appClient.createWidget(1, deviceTiles.id, tileTemplate.id, "{\"id\":102, \"deviceId\":-1, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(6));
        ValueDisplay valueDisplay = new ValueDisplay();
        valueDisplay.id = 103;
        valueDisplay.width = 2;
        valueDisplay.height = 2;
        valueDisplay.deviceId = -1;
        valueDisplay.pinType = PinType.VIRTUAL;
        valueDisplay.pin = 57;
        appClient.createWidget(1, deviceTiles.id, tileTemplate.id, valueDisplay);
        appClient.verifyResult(TestUtil.ok(7));
        clientPair.hardwareClient.send("hardware vw 1 0");
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 57 2");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 1 0"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 57 2"));
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(8));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 1 0"));
        appClient.verifyResult(TestUtil.appSync("1-0 vw 57 2"));
        appClient.deleteWidget(1, deviceTiles.id);
        appClient.verifyResult(TestUtil.ok(9));
        appClient.reset();
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.neverAfter(100, TestUtil.appSync("1-0 vm 56 1"));
        appClient.neverAfter(100, TestUtil.appSync("1-0 vw 1 0"));
        appClient.neverAfter(100, TestUtil.appSync("1-0 vw 57 2"));
    }

    @Test
    public void testTerminalStorageRemembersCommandsInNewFormatAndDeviceSelector() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        DeviceSelector deviceSelector = new DeviceSelector();
        deviceSelector.id = 200000;
        deviceSelector.height = 4;
        deviceSelector.width = 1;
        deviceSelector.deviceIds = new int[]{ 0, 1 };
        appClient.createWidget(1, deviceSelector);
        appClient.verifyResult(TestUtil.ok(4));
        appClient.createWidget(1, "{\"id\":102, \"deviceId\":200000, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":56}");
        appClient.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.send("hardware vw 56 1");
        clientPair.hardwareClient.send("hardware vw 56 2");
        clientPair.hardwareClient.send("hardware vw 56 3");
        clientPair.hardwareClient.send("hardware vw 56 4");
        clientPair.hardwareClient.send("hardware vw 56 dddyyyiii");
        appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 56 1"));
        appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 56 2"));
        appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 56 3"));
        appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 56 4"));
        appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 56 dddyyyiii"));
        appClient.sync(1, 0);
        appClient.verifyResult(TestUtil.ok(6));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 1 2 3 4 dddyyyiii"));
    }

    @Test
    public void testTableSyncWorkForNewCommandFormat() throws Exception {
        stop();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "2.26.0");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(2);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        appClient.send("getEnergy");
        appClient.verifyResult(produce(3, GET_ENERGY, "7500"));
        Table table = new Table();
        table.pin = 56;
        table.pinType = PinType.VIRTUAL;
        table.isClickableRows = true;
        table.isReoderingAllowed = true;
        table.height = 2;
        table.width = 2;
        appClient.createWidget(1, table);
        appClient.verifyResult(TestUtil.ok(4));
        clientPair.hardwareClient.send("hardware vw 56 add 0 Row1 1");
        clientPair.hardwareClient.send("hardware vw 56 add 1 Row2 2");
        clientPair.hardwareClient.send("hardware vw 56 add 2 Row3 3");
        clientPair.hardwareClient.send("hardware vw 56 add 3 Row4 4");
        clientPair.hardwareClient.send("hardware vw 56 add 4 Row5 dddyyyiii");
        appClient.verifyResult(produce(1, HARDWARE, TestUtil.b("1-0 vw 56 add 0 Row1 1")));
        appClient.verifyResult(produce(2, HARDWARE, TestUtil.b("1-0 vw 56 add 1 Row2 2")));
        appClient.verifyResult(produce(3, HARDWARE, TestUtil.b("1-0 vw 56 add 2 Row3 3")));
        appClient.verifyResult(produce(4, HARDWARE, TestUtil.b("1-0 vw 56 add 3 Row4 4")));
        appClient.verifyResult(produce(5, HARDWARE, TestUtil.b("1-0 vw 56 add 4 Row5 dddyyyiii")));
        appClient.activate(1);
        appClient.verifyResult(TestUtil.ok(5));
        appClient.verifyResult(TestUtil.appSync("1-0 vm 56 add 0 Row1 1 true add 1 Row2 2 true add 2 Row3 3 true add 3 Row4 4 true add 4 Row5 dddyyyiii true"));
    }

    @Test
    public void testLCDSendsSyncOnActivate() throws Exception {
        clientPair.hardwareClient.send("hardware vw 20 p 0 0 Hello");
        clientPair.hardwareClient.send("hardware vw 20 p 0 1 World");
        clientPair.appClient.verifyResult(produce(1, HARDWARE, TestUtil.b("1-0 vw 20 p 0 0 Hello")));
        clientPair.appClient.verifyResult(produce(2, HARDWARE, TestUtil.b("1-0 vw 20 p 0 1 World")));
        clientPair.appClient.sync(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 0 Hello"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 1 World"));
    }

    @Test
    public void testLCDSendsSyncOnActivate2() throws Exception {
        clientPair.hardwareClient.send("hardware vw 20 p 0 0 H1");
        clientPair.hardwareClient.send("hardware vw 20 p 0 1 H2");
        clientPair.hardwareClient.send("hardware vw 20 p 0 2 H3");
        clientPair.hardwareClient.send("hardware vw 20 p 0 3 H4");
        clientPair.hardwareClient.send("hardware vw 20 p 0 4 H5");
        clientPair.hardwareClient.send("hardware vw 20 p 0 5 H6");
        clientPair.hardwareClient.send("hardware vw 20 p 0 6 H7");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 20 p 0 0 H1"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 20 p 0 1 H2"));
        clientPair.appClient.verifyResult(TestUtil.hardware(3, "1-0 vw 20 p 0 2 H3"));
        clientPair.appClient.verifyResult(TestUtil.hardware(4, "1-0 vw 20 p 0 3 H4"));
        clientPair.appClient.verifyResult(TestUtil.hardware(5, "1-0 vw 20 p 0 4 H5"));
        clientPair.appClient.verifyResult(TestUtil.hardware(6, "1-0 vw 20 p 0 5 H6"));
        clientPair.appClient.verifyResult(TestUtil.hardware(7, "1-0 vw 20 p 0 6 H7"));
        clientPair.appClient.sync(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 1 H2"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 2 H3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 3 H4"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 4 H5"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 5 H6"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 20 p 0 6 H7"));
    }

    @Test
    public void testActivateAndGetSync() throws Exception {
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
    }

    // https://github.com/blynkkk/blynk-server/issues/443
    @Test
    public void testSyncWidgetValueOverlapsWithPinStorage() throws Exception {
        clientPair.hardwareClient.send("hardware vw 125 1");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 125 1"));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 125 1"));
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":0, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":125}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        clientPair.hardwareClient.send("hardware vw 125 2");
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 125 2"));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 125 2"));
    }

    @Test
    public void testActivateAndGetSyncForSpecificDeviceId() throws Exception {
        clientPair.appClient.sync(1, 0);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
    }

    @Test
    public void testSyncForDeviceSelectorAndSetProperty() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.ESP8266);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0], \"width\":1, \"height\":1, \"value\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"height\":1, \"deviceId\":200000, \"x\":0, \"y\":0, \"label\":\"Button\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.setProperty(88, "label", "newLabel");
        clientPair.hardwareClient.setProperty(88, "label", "newLabel2");
        clientPair.appClient.verifyResult(TestUtil.setProperty(1, "1-0 88 label newLabel"));
        clientPair.appClient.verifyResult(TestUtil.setProperty(2, "1-0 88 label newLabel2"));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
        clientPair.appClient.verifyResult(TestUtil.setProperty(1111, "1-0 88 label newLabel2"));
        clientPair.appClient.never(TestUtil.setProperty(1111, "1-0 88 label newLabel"));
    }

    @Test
    public void testSyncForDeviceSelectorAndSetPropertyAndMultiValueWidget() throws Exception {
        Device device0 = new Device(0, "My Dashboard", BoardType.ESP8266);
        device0.status = Status.ONLINE;
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"deviceIds\":[0], \"width\":1, \"height\":1, \"value\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.createWidget(1, "{\"id\":88, \"width\":1, \"deviceId\":200000, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"TERMINAL\", \"pinType\":\"VIRTUAL\", \"pin\":88}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.setProperty(88, "label", "newLabel");
        clientPair.hardwareClient.setProperty(88, "label", "newLabel2");
        clientPair.appClient.verifyResult(TestUtil.setProperty(1, "1-0 88 label newLabel"));
        clientPair.appClient.verifyResult(TestUtil.setProperty(2, "1-0 88 label newLabel2"));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
        clientPair.appClient.verifyResult(TestUtil.setProperty(1111, "1-0 88 label newLabel2"));
        clientPair.appClient.never(TestUtil.setProperty(1111, "1-0 88 label newLabel"));
    }

    @Test
    public void testActivateAndGetSyncForTimeInput() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"TIME_INPUT\",\"id\":99, \"pin\":99, \"pinType\":\"VIRTUAL\", " + "\"x\":0,\"y\":0,\"width\":1,\"height\":1}"));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("hardware 1 vw " + "99 82800 82860 Europe/Kiev 1"));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800 82860 Europe/Kiev 1")));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1, 0);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 1 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 2 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 3 0"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 dw 5 1"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 4 244"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 7 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 aw 30 3"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 0 89.888037459418"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 11 -58.74774244674501"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 13 60 143 158"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-0 vw 99 82800 82860 Europe/Kiev 1"));
    }

    @Test
    public void testActivateAndGetSyncForNonExistingDeviceId() throws Exception {
        clientPair.appClient.sync(1, 1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testLCDOnActivateSendsCorrectBodySimpleModeAndAnotherDevice() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.createWidget(1, ("{\"deviceId\":1,\"type\":\"LCD\",\"id\":1923810267,\"x\":0,\"y\":6,\"color\":600084223,\"width\":8,\"height\":2,\"tabId\":0,\"" + ((("pins\":[" + "{\"pin\":10,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023, \"value\":\"10\"},") + "{\"pin\":11,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023, \"value\":\"11\"}],") + "\"advancedMode\":false,\"textLight\":false,\"textLightOn\":false,\"frequency\":1000}")));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        clientPair.appClient.sync(1, 1);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-1 vw 10 10"));
        clientPair.appClient.verifyResult(TestUtil.appSync("1-1 vw 11 11"));
    }
}

