package cc.blynk.integration.tcp;


import AppNameUtil.BLYNK;
import GraphGranularityType.DAILY;
import GraphGranularityType.HOURLY;
import GraphGranularityType.MINUTE;
import PinType.DIGITAL;
import PinType.VIRTUAL;
import View.PublicOnly;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DashboardSettings;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.controls.Button;
import cc.blynk.server.core.model.widgets.controls.Step;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.model.widgets.others.Player;
import cc.blynk.server.core.model.widgets.ui.TimeInput;
import cc.blynk.server.core.protocol.model.messages.ResponseMessage;
import cc.blynk.server.notifications.mail.QrHolder;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.FileUtils;
import cc.blynk.utils.SHA256Util;
import io.netty.channel.ChannelFuture;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.List;
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
public class MainWorkflowTest extends SingleServerInstancePerTest {
    @Test
    public void testCloneForLocalServerWithNoDB() throws Exception {
        Assert.assertFalse(MainWorkflowTest.holder.dbManager.isDBEnabled());
        clientPair.appClient.send("getCloneCode 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send(("getProjectByCloneCode " + token));
        DashBoard dashBoard = clientPair.appClient.parseDash(2);
        Assert.assertEquals("My Dashboard", dashBoard.name);
    }

    @Test
    public void testResetEmail() throws Exception {
        String userName = CounterBase.getUserName();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.send(((("resetPass start " + userName) + " ") + (AppNameUtil.BLYNK)));
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send(((("resetPass start " + userName) + " ") + (AppNameUtil.BLYNK)));
        appClient.verifyResult(TestUtil.notAllowed(2));
        String token = MainWorkflowTest.holder.tokensPool.getTokens().entrySet().iterator().next().getKey();
        Mockito.verify(MainWorkflowTest.holder.mailWrapper).sendWithAttachment(ArgumentMatchers.eq(userName), ArgumentMatchers.eq("Password restoration for your Blynk account."), ArgumentMatchers.contains(("http://blynk-cloud.com/restore?token=" + token)), ArgumentMatchers.any(QrHolder.class));
        appClient.send("resetPass verify 123");
        appClient.verifyResult(TestUtil.notAllowed(3));
        appClient.send(("resetPass verify " + token));
        appClient.verifyResult(TestUtil.ok(4));
        appClient.send(((("resetPass reset " + token) + " ") + (SHA256Util.makeHash("2", userName))));
        appClient.verifyResult(TestUtil.ok(5));
        // verify(holder.mailWrapper).sendHtml(eq(userName), eq("Your new password on Blynk"), contains("You have changed your password on Blynk. Please, keep it in your records so you don't forget it."));
        appClient.login(userName, "1");
        appClient.verifyResult(new ResponseMessage(6, USER_NOT_AUTHENTICATED));
        appClient.login(userName, "2");
        appClient.verifyResult(TestUtil.ok(7));
    }

    @Test
    public void registrationAllowedOnlyOncePerConnection() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register("test1@test.com", "1", BLYNK);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.register("test2@test.com", "1", BLYNK);
        appClient.verifyResult(TestUtil.notAllowed(2));
        Assert.assertTrue(isClosed());
    }

    @Test
    public void createBasicProfile() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        String username = CounterBase.incrementAndGetUserName();
        appClient.register(username, "1", BLYNK);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login(username, "1", "Android", "RC13");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.createDash("{\"id\":1, \"createdAt\":1, \"name\":\"test board\"}");
        appClient.verifyResult(TestUtil.ok(3));
        appClient.createWidget(1, "{\"id\":1, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":1}");
        appClient.verifyResult(TestUtil.ok(4));
        appClient.reset();
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board\",\"createdAt\":1,\"updatedAt\":0,\"widgets\":[{\"type\":\"BUTTON\",\"id\":1,\"x\":0,\"y\":0,\"color\":0,\"width\":1,\"height\":1,\"tabId\":0,\"label\":\"Some Text\",\"isDefaultColor\":false,\"deviceId\":0,\"pinType\":\"DIGITAL\",\"pin\":1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false}],\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", profile.toString());
        appClient.createWidget(1, "{\"id\":2, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.reset();
        appClient.send("loadProfileGzipped");
        profile = appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board\",\"createdAt\":1,\"updatedAt\":0,\"widgets\":[{\"type\":\"BUTTON\",\"id\":1,\"x\":0,\"y\":0,\"color\":0,\"width\":1,\"height\":1,\"tabId\":0,\"label\":\"Some Text\",\"isDefaultColor\":false,\"deviceId\":0,\"pinType\":\"DIGITAL\",\"pin\":1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false},{\"type\":\"BUTTON\",\"id\":2,\"x\":2,\"y\":2,\"color\":0,\"width\":1,\"height\":1,\"tabId\":0,\"label\":\"Some Text 2\",\"isDefaultColor\":false,\"deviceId\":0,\"pinType\":\"DIGITAL\",\"pin\":2,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false}],\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", profile.toString());
        appClient.updateWidget(1, "{\"id\":2, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"new label\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":3}\"");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.reset();
        appClient.send("loadProfileGzipped");
        profile = appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board\",\"createdAt\":1,\"updatedAt\":0,\"widgets\":[{\"type\":\"BUTTON\",\"id\":1,\"x\":0,\"y\":0,\"color\":0,\"width\":1,\"height\":1,\"tabId\":0,\"label\":\"Some Text\",\"isDefaultColor\":false,\"deviceId\":0,\"pinType\":\"DIGITAL\",\"pin\":1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false},{\"type\":\"BUTTON\",\"id\":2,\"x\":2,\"y\":2,\"color\":0,\"width\":1,\"height\":1,\"tabId\":0,\"label\":\"new label\",\"isDefaultColor\":false,\"deviceId\":0,\"pinType\":\"DIGITAL\",\"pin\":3,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false}],\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", profile.toString());
        appClient.deleteWidget(1, 3);
        appClient.verifyResult(TestUtil.illegalCommand(2));
        appClient.deleteWidget(1, 1);
        appClient.verifyResult(TestUtil.ok(3));
        appClient.deleteWidget(1, 2);
        appClient.verifyResult(TestUtil.ok(4));
        appClient.reset();
        appClient.send("loadProfileGzipped");
        profile = appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board\",\"createdAt\":1,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", profile.toString());
    }

    @Test
    public void testNoEmptyPMCommands() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        String username = CounterBase.incrementAndGetUserName();
        appClient.register(username, "1", BLYNK);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login(username, "1", "Android", "RC13");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.createDash("{\"id\":1, \"createdAt\":1, \"name\":\"test board\"}");
        appClient.verifyResult(TestUtil.ok(3));
        appClient.createWidget(1, "{\"id\":1, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"VIRTUAL\", \"pin\":1}");
        appClient.verifyResult(TestUtil.ok(4));
        Device device = new Device();
        device.id = 1;
        device.name = "123";
        device.boardType = BoardType.ESP32_Dev_Board;
        appClient.createDevice(1, device);
        device = appClient.parseDevice(5);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        appClient.verifyResult(TestUtil.createDevice(5, device));
        appClient.activate(1);
        appClient.verifyResult(new ResponseMessage(6, DEVICE_NOT_IN_NETWORK));
        TestHardClient hardClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        hardClient.login(device.token);
        hardClient.verifyResult(TestUtil.ok(1));
        hardClient.never(TestUtil.hardware(1, "pm"));
        appClient.verifyResult(new cc.blynk.server.core.protocol.model.messages.StringMessage(1, HARDWARE_CONNECTED, "1-1"));
        appClient.activate(1);
        appClient.verifyResult(TestUtil.ok(7));
        hardClient.never(TestUtil.hardware(1, "pm"));
    }

    @Test
    public void doNotAllowUsersWithQuestionMark() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register("te?st@test.com", "1", BLYNK);
        appClient.verifyResult(TestUtil.illegalCommand(1));
    }

    @Test
    public void createDashWithDevices() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register("test@test.com", "1", BLYNK);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login("test@test.com", "1", "Android", "RC13");
        appClient.verifyResult(TestUtil.ok(2));
        DashBoard dash = new DashBoard();
        dash.id = 1;
        dash.name = "AAAa";
        Device device = new Device();
        device.id = 0;
        device.name = "123";
        dash.devices = new Device[]{ device };
        appClient.createDash(("no_token\u0000" + (dash.toString())));
        appClient.verifyResult(TestUtil.ok(3));
        appClient.send("getDevices 1");
        Device[] devices = appClient.parseDevices(4);
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        Assert.assertEquals(0, devices[0].id);
        Assert.assertEquals("123", devices[0].name);
        Assert.assertNull(devices[0].token);
    }

    @Test
    public void testRegisterWithAnotherApp() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register(CounterBase.getUserName(), "1", "MyApp");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.13.3", "MyApp");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.createDash("{\"id\":1, \"createdAt\":1, \"name\":\"test board\"}");
        appClient.verifyResult(TestUtil.ok(3));
        appClient.createWidget(1, "{\"id\":1, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":1}");
        appClient.verifyResult(TestUtil.ok(4));
        appClient.reset();
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        Assert.assertEquals("{\"dashBoards\":[{\"id\":1,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board\",\"createdAt\":1,\"updatedAt\":0,\"widgets\":[{\"type\":\"BUTTON\",\"id\":1,\"x\":0,\"y\":0,\"color\":0,\"width\":1,\"height\":1,\"tabId\":0,\"label\":\"Some Text\",\"isDefaultColor\":false,\"deviceId\":0,\"pinType\":\"DIGITAL\",\"pin\":1,\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0.0,\"max\":0.0,\"pushMode\":false}],\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", profile.toString());
    }

    @Test
    public void testDoubleLogin() throws Exception {
        clientPair.hardwareClient.login(((CounterBase.getUserName()) + " 1"));
        clientPair.hardwareClient.verifyResult(new ResponseMessage(1, USER_ALREADY_REGISTERED));
    }

    @Test
    public void testDoubleLogin2() throws Exception {
        TestHardClient newHardwareClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        newHardwareClient.login(clientPair.token);
        newHardwareClient.login(clientPair.token);
        newHardwareClient.verifyResult(TestUtil.ok(1));
        newHardwareClient.verifyResult(new ResponseMessage(2, USER_ALREADY_REGISTERED));
    }

    @Test
    public void sendCommandBeforeLogin() {
        TestHardClient newHardwareClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        newHardwareClient.send("hardware vw 1 1");
        long tries = 0;
        while ((!(isClosed())) && (tries < 10)) {
            TestUtil.sleep(100);
            tries++;
        } 
        Assert.assertTrue(isClosed());
    }

    @Test
    public void testForwardBluetoothFromAppWorks() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":743, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"STEP\", \"pwmMode\":true, \"pinType\":\"VIRTUAL\", \"pin\":67}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardwareBT 1-0 vw 67 100");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "1-0 vw 67 100")));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        Assert.assertNotNull(profile);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (67)), VIRTUAL);
        Assert.assertNotNull(widget);
        Assert.assertTrue((widget instanceof Step));
        Assert.assertEquals("100", ((OnePinWidget) (widget)).value);
    }

    @Test
    public void testValueForPWMPinForStteperIsAccepted() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":743, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"STEP\", \"pwmMode\":true, \"pinType\":\"DIGITAL\", \"pin\":24}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1 aw 24 100");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(2, "aw 24 100"));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        Assert.assertNotNull(profile);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (24)), DIGITAL);
        Assert.assertNotNull(widget);
        Assert.assertTrue((widget instanceof Step));
        Assert.assertEquals("100", ((OnePinWidget) (widget)).value);
    }

    @Test
    public void testSendInvalidVirtualPin() throws Exception {
        clientPair.hardwareClient.send("hardware vw 256 100");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(1));
    }

    @Test
    public void testSendInvalidVirtualPin2() throws Exception {
        clientPair.hardwareClient.send("hardware vw -1 100");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(1));
    }

    @Test
    public void testSendValidVirtualPin() throws Exception {
        clientPair.hardwareClient.send("hardware vw 0 100");
        clientPair.hardwareClient.send("hardware vw 255 100");
        clientPair.hardwareClient.never(TestUtil.illegalCommand(1));
        clientPair.hardwareClient.never(TestUtil.illegalCommand(2));
    }

    @Test
    public void testNoEnergyDrainForBusinessApps() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register("test@test.com", "1", "MyApp");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login("test@test.com", "1", "Android", "1.13.3", "MyApp");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.createDash("{\"id\":2, \"createdAt\":1458856800001, \"name\":\"test board\"}");
        appClient.verifyResult(TestUtil.ok(3));
        appClient.send("getEnergy");
        appClient.verifyResult(produce(4, GET_ENERGY, "2000"));
        appClient.createWidget(2, "{\"id\":2, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(5));
        appClient.createWidget(2, "{\"id\":3, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(6));
        appClient.createWidget(2, "{\"id\":4, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(7));
        appClient.createWidget(2, "{\"id\":5, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(8));
        appClient.createWidget(2, "{\"id\":6, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(9));
        appClient.createWidget(2, "{\"id\":7, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        appClient.verifyResult(TestUtil.ok(10));
    }

    @Test
    public void testPingCommandWorks() throws Exception {
        clientPair.appClient.send("ping");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testAddAndRemoveTabs() throws Exception {
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(2, GET_ENERGY, "7500"));
        clientPair.appClient.createWidget(1, "{\"id\":100, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"tabs\":[{\"label\":\"tab 1\"}, {\"label\":\"tab 2\"}, {\"label\":\"tab 3\"}], \"type\":\"TABS\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.createWidget(1, "{\"id\":101, \"width\":1, \"height\":1, \"x\":15, \"y\":0, \"tabId\":1, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":18}");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.createWidget(1, "{\"id\":102, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":0, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":17}");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(6, GET_ENERGY, "7100"));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(19, profile.dashBoards[0].widgets.length);
        clientPair.appClient.deleteWidget(1, 100);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(3, GET_ENERGY, "7300"));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(17, profile.dashBoards[0].widgets.length);
        Assert.assertNotNull(profile.dashBoards[0].findWidgetByPin(0, ((short) (17)), DIGITAL));
    }

    @Test
    public void testAddAndUpdateTabs() throws Exception {
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(16, profile.dashBoards[0].widgets.length);
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(2, GET_ENERGY, "7500"));
        clientPair.appClient.createWidget(1, "{\"id\":100, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"tabs\":[{\"label\":\"tab 1\"}, {\"label\":\"tab 2\"}, {\"label\":\"tab 3\"}], \"type\":\"TABS\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.createWidget(1, "{\"id\":101, \"width\":1, \"height\":1, \"x\":15, \"y\":0, \"tabId\":1, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":18}");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.createWidget(1, "{\"id\":102, \"width\":1, \"height\":1, \"x\":5, \"y\":0, \"tabId\":2, \"label\":\"Some Text\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":17}");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(6, GET_ENERGY, "7100"));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(19, profile.dashBoards[0].widgets.length);
        clientPair.appClient.updateWidget(1, "{\"id\":100, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"tabs\":[{\"label\":\"tab 1\"}, {\"label\":\"tab 2\"}], \"type\":\"TABS\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("getEnergy");
        clientPair.appClient.verifyResult(produce(3, GET_ENERGY, "7300"));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(18, profile.dashBoards[0].widgets.length);
        Assert.assertNull(profile.dashBoards[0].findWidgetByPin(0, ((short) (17)), DIGITAL));
        Assert.assertNotNull(profile.dashBoards[0].findWidgetByPin(0, ((short) (18)), DIGITAL));
    }

    @Test
    public void testPurchaseEnergy() throws Exception {
        clientPair.appClient.send(("addEnergy " + (("1000" + "\u0000") + "5262996016779471529.4493624392154338")));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(1)));
        clientPair.appClient.send(("addEnergy " + (("1000" + "\u0000") + "A3B93EE9-BC65-499E-A660-F2A84F2AF1FC")));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(2)));
        clientPair.appClient.send(("addEnergy " + (("1000" + "\u0000") + "com.blynk.energy.280001461578468247")));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(3)));
        clientPair.appClient.send(("addEnergy " + "1000"));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(4)));
        clientPair.appClient.send(("addEnergy " + ("1000" + "\u0000")));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(5)));
        clientPair.appClient.send(("addEnergy " + (("1000" + "\u0000") + "150000195113772")));
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        clientPair.appClient.send(("addEnergy " + (("1000" + "\u0000") + "1370-3990-1414-55681")));
        clientPair.appClient.verifyResult(TestUtil.ok(7));
    }

    @Test
    public void testApplicationPingCommandOk() throws Exception {
        clientPair.appClient.send("ping");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.send("ping");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testHardPingCommandOk() throws Exception {
        clientPair.hardwareClient.send("ping");
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("ping");
        clientPair.hardwareClient.verifyResult(TestUtil.ok(2));
    }

    @Test
    public void testDashCommands() throws Exception {
        clientPair.appClient.updateDash("{\"id\":10, \"name\":\"test board update\"}");
        clientPair.appClient.verifyResult(TestUtil.illegalCommand(1));
        clientPair.appClient.createDash("{\"id\":10, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.createDash("{\"id\":10, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.notAllowed(3));
        clientPair.appClient.updateDash("{\"id\":10, \"name\":\"test board update\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.hardwareClient.send("ping");
        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(TestUtil.illegalCommand(6));
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        clientPair.appClient.reset();
        Profile responseProfile;
        DashBoard responseDash;
        clientPair.appClient.send("loadProfileGzipped");
        responseProfile = clientPair.appClient.parseProfile(1);
        responseProfile.dashBoards[0].updatedAt = 0;
        responseProfile.dashBoards[0].createdAt = 0;
        Assert.assertEquals("{\"dashBoards\":[{\"id\":10,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board update\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}", responseProfile.toString());
        clientPair.appClient.send("loadProfileGzipped 10");
        responseDash = clientPair.appClient.parseDash(2);
        responseDash.updatedAt = 0;
        responseDash.createdAt = 0;
        Assert.assertEquals("{\"id\":10,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board update\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":false,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}", responseDash.toString());
        clientPair.appClient.send("loadProfileGzipped 1");
        clientPair.appClient.verifyResult(TestUtil.illegalCommand(3));
        clientPair.appClient.activate(10);
        clientPair.appClient.verifyResult(new ResponseMessage(4, DEVICE_NOT_IN_NETWORK));
        clientPair.appClient.send("loadProfileGzipped");
        responseProfile = clientPair.appClient.parseProfile(5);
        responseProfile.dashBoards[0].updatedAt = 0;
        responseProfile.dashBoards[0].createdAt = 0;
        String expectedProfile = "{\"dashBoards\":[{\"id\":10,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board update\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":true,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}";
        Assert.assertEquals(expectedProfile, responseProfile.toString());
        clientPair.appClient.updateDash("{\"id\":10,\"name\":\"test board update\",\"keepScreenOn\":false,\"isShared\":false,\"isActive\":false}");
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        expectedProfile = "{\"dashBoards\":[{\"id\":10,\"parentId\":-1,\"isPreview\":false,\"name\":\"test board update\",\"createdAt\":0,\"updatedAt\":0,\"theme\":\"Blynk\",\"keepScreenOn\":false,\"isAppConnectedOn\":false,\"isNotificationsOff\":false,\"isShared\":false,\"isActive\":true,\"widgetBackgroundOn\":false,\"color\":-1,\"isDefaultColor\":true}]}";
        clientPair.appClient.send("loadProfileGzipped");
        responseProfile = clientPair.appClient.parseProfile(7);
        responseProfile.dashBoards[0].updatedAt = 0;
        responseProfile.dashBoards[0].createdAt = 0;
        Assert.assertEquals(expectedProfile, responseProfile.toString());
    }

    @Test
    public void testHardwareChannelClosedOnDashRemoval() throws Exception {
        String username = CounterBase.getUserName();
        String tempDir = MainWorkflowTest.holder.props.getProperty("data.folder");
        Path userReportFolder = Paths.get(tempDir, "data", username);
        if (Files.notExists(userReportFolder)) {
            Files.createDirectories(userReportFolder);
        }
        Path pinReportingDataPath10 = Paths.get(tempDir, "data", username, ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), MINUTE));
        Path pinReportingDataPath11 = Paths.get(tempDir, "data", username, ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), HOURLY));
        Path pinReportingDataPath12 = Paths.get(tempDir, "data", username, ReportingDiskDao.generateFilename(1, 0, DIGITAL, ((short) (8)), DAILY));
        Path pinReportingDataPath13 = Paths.get(tempDir, "data", username, ReportingDiskDao.generateFilename(1, 0, VIRTUAL, ((short) (9)), DAILY));
        FileUtils.write(pinReportingDataPath10, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath11, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath12, 1.11, 1111111);
        FileUtils.write(pinReportingDataPath13, 1.11, 1111111);
        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Assert.assertTrue(isClosed());
        Assert.assertTrue(Files.notExists(pinReportingDataPath10));
        Assert.assertTrue(Files.notExists(pinReportingDataPath11));
        Assert.assertTrue(Files.notExists(pinReportingDataPath12));
        Assert.assertTrue(Files.notExists(pinReportingDataPath13));
    }

    @Test
    public void testGetTokenWorksWithNewFormats() throws Exception {
        clientPair.appClient.createDash("{\"id\":10, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createDevice(10, new Device(2, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice(2);
        String token = device.token;
        Assert.assertNotNull(token);
        clientPair.appClient.getDevice(10, 2);
        Device device2 = clientPair.appClient.parseDevice(3);
        Assert.assertNotNull(device2);
        Assert.assertEquals(token, device2.token);
        clientPair.appClient.getDevice(10, 2);
        device2 = clientPair.appClient.parseDevice(4);
        Assert.assertNotNull(device2);
        Assert.assertEquals(token, device2.token);
        clientPair.appClient.createDash("{\"id\":11, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.getDevice(11, 0);
        clientPair.appClient.verifyResult(TestUtil.illegalCommandBody(6));
    }

    @Test
    public void deleteDashDeletesTokensAlso() throws Exception {
        clientPair.appClient.createDash("{\"id\":10, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.createDevice(10, new Device(2, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token = device.token;
        Assert.assertNotNull(token);
        clientPair.appClient.reset();
        clientPair.appClient.send("getShareToken 10");
        String sharedToken = clientPair.appClient.getBody();
        Assert.assertNotNull(sharedToken);
        clientPair.appClient.deleteDash(10);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        TestHardClient newHardClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        newHardClient.login(token);
        newHardClient.verifyResult(new ResponseMessage(1, INVALID_TOKEN));
        TestAppClient newAppClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        newAppClient.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + sharedToken) + " Android 24"));
        newAppClient.verifyResult(TestUtil.notAllowed(1));
    }

    @Test
    public void loadGzippedProfile() throws Exception {
        Profile expectedProfile = JsonParser.parseProfileFromString(TestUtil.readTestUserProfile());
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        profile.dashBoards[0].updatedAt = 0;
        expectedProfile.dashBoards[0].devices = null;
        profile.dashBoards[0].devices = null;
        Assert.assertEquals(expectedProfile.toString(), profile.toString());
    }

    @Test
    public void settingsUpdateCommand() throws Exception {
        DashboardSettings settings = new DashboardSettings("New Name", true, Theme.BlynkLight, true, true, false, false, 0, false);
        clientPair.appClient.send(("updateSettings 1\u0000" + (JsonParser.toJson(settings))));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        DashBoard dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(settings.name, dashBoard.name);
        Assert.assertEquals(settings.isAppConnectedOn, dashBoard.isAppConnectedOn);
        Assert.assertEquals(settings.isNotificationsOff, dashBoard.isNotificationsOff);
        Assert.assertEquals(settings.isShared, dashBoard.isShared);
        Assert.assertEquals(settings.keepScreenOn, dashBoard.keepScreenOn);
        Assert.assertEquals(settings.theme, dashBoard.theme);
    }

    @Test
    public void testSendUnicodeChar() throws Exception {
        clientPair.hardwareClient.send("hardware vw 1 ?F");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 vw 1 ?F")));
    }

    @Test
    public void testAppSendAnyHardCommandAndBack() throws Exception {
        clientPair.appClient.send("hardware 1 dw 1 1");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "dw 1 1")));
        clientPair.hardwareClient.send("hardware ar 2");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "ar 2")));
    }

    @Test
    public void testAppNoActiveDashForHard() throws Exception {
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 aw 1 1")));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(2, NO_ACTIVE_DASHBOARD)));
    }

    @Test
    public void testHardwareSendsWrongCommand() throws Exception {
        clientPair.hardwareClient.send("hardware aw 1 ");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(1));
        clientPair.hardwareClient.send("hardware aw 1");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(2));
    }

    @Test
    public void testAppChangeActiveDash() throws Exception {
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 aw 1 1")));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Profile newProfile = TestUtil.parseProfile(TestUtil.readTestUserProfile("user_profile_json_3_dashes.txt"));
        clientPair.appClient.createDash(newProfile.dashBoards[1]);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(2, NO_ACTIVE_DASHBOARD)));
        clientPair.appClient.activate(2);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(3, DEVICE_NOT_IN_NETWORK)));
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(3, NO_ACTIVE_DASHBOARD)));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(4, "1-0 aw 1 1")));
    }

    @Test
    public void testActive2AndDeactivate1() throws Exception {
        TestHardClient hardClient2 = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        Profile newProfile = TestUtil.parseProfile(TestUtil.readTestUserProfile("user_profile_json_3_dashes.txt"));
        clientPair.appClient.createDash(newProfile.dashBoards[1]);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.activate(2);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(3, DEVICE_NOT_IN_NETWORK)));
        clientPair.appClient.reset();
        clientPair.appClient.createDevice(2, new Device(2, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token2 = device.token;
        hardClient2.login(token2);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 aw 1 1")));
        hardClient2.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, (("2-" + (device.id)) + " aw 1 1"))));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(2, NO_ACTIVE_DASHBOARD)));
        hardClient2.send("hardware aw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(3, (("2-" + (device.id)) + " aw 1 1"))));
        stop().awaitUninterruptibly();
    }

    @Test
    public void testActivateWorkflow() throws Exception {
        clientPair.appClient.activate(2);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.illegalCommand(1)));
        clientPair.appClient.deactivate(2);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.illegalCommand(2)));
        clientPair.appClient.send("hardware 1 ar 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
    }

    @Test
    public void testTweetNotWorks() throws Exception {
        clientPair.hardwareClient.send("tweet");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(1, NOTIFICATION_INVALID_BODY)));
        clientPair.hardwareClient.send("tweet ");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(2, NOTIFICATION_INVALID_BODY)));
        StringBuilder a = new StringBuilder();
        for (int i = 0; i < 141; i++) {
            a.append("a");
        }
        clientPair.hardwareClient.send(("tweet " + a));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(3, NOTIFICATION_INVALID_BODY)));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("tweet yo");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(4, NOTIFICATION_NOT_AUTHORIZED)));
    }

    @Test
    public void testSmsWorks() throws Exception {
        clientPair.hardwareClient.send("sms");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(1, NOTIFICATION_INVALID_BODY)));
        // no sms widget
        clientPair.hardwareClient.send("sms yo");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(2, NOTIFICATION_NOT_AUTHORIZED)));
        // adding sms widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"to\":\"3809683423423\", \"x\":0, \"y\":0, \"type\":\"SMS\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("sms yo");
        Mockito.verify(MainWorkflowTest.holder.smsWrapper, Mockito.timeout(500)).send(ArgumentMatchers.eq("3809683423423"), ArgumentMatchers.eq("yo"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.send("sms yo");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(4, QUOTA_LIMIT)));
    }

    @Test
    public void testTweetWorks() throws Exception {
        clientPair.hardwareClient.send("tweet yo");
        Mockito.verify(MainWorkflowTest.holder.twitterWrapper, Mockito.timeout(500)).send(ArgumentMatchers.eq("token"), ArgumentMatchers.eq("secret"), ArgumentMatchers.eq("yo"), ArgumentMatchers.any());
        clientPair.hardwareClient.send("tweet yo");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(2, QUOTA_LIMIT)));
    }

    @Test
    public void testPlayerUpdateWorksAsExpected() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"PLAYER\",\"id\":99, \"pin\":99, \"pinType\":\"VIRTUAL\", " + "\"x\":0,\"y\":0,\"width\":1,\"height\":1}"));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1 vw 99 play");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 play")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Player player = ((Player) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(player);
        Assert.assertTrue(player.isOnPlay);
        clientPair.appClient.send("hardware 1 vw 99 stop");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 stop")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        player = ((Player) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(player);
        Assert.assertFalse(player.isOnPlay);
    }

    @Test
    public void testTimeInputUpdateWorksAsExpected() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"TIME_INPUT\",\"id\":99, \"pin\":99, \"pinType\":\"VIRTUAL\", " + "\"x\":0,\"y\":0,\"width\":1,\"height\":1}"));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 82800 82860 Europe/Kiev 1"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800 82860 Europe/Kiev 1")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        TimeInput timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals(82860, timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertArrayEquals(new int[]{ 1 }, timeInput.days);
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 82800 82860 Europe/Kiev "))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800 82860 Europe/Kiev ")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals(82860, timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 82800  Europe/Kiev "))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800  Europe/Kiev ")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals((-1), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 82800  Europe/Kiev 1,2,3,4"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800  Europe/Kiev 1,2,3,4")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals((-1), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, timeInput.days);
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99   Europe/Kiev 1,2,3,4"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99   Europe/Kiev 1,2,3,4")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals((-1), timeInput.startAt);
        Assert.assertEquals((-1), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, timeInput.days);
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 82800 82800 Europe/Kiev  10800"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 82800 82800 Europe/Kiev  10800")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals(82800, timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
        clientPair.appClient.send(("hardware 1 vw " + (TestUtil.b("99 ss sr Europe/Kiev  10800"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "vw 99 ss sr Europe/Kiev  10800")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals((-2), timeInput.startAt);
        Assert.assertEquals((-3), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
    }

    @Test
    public void testTimeInputUpdateWorksAsExpectedFromHardSide() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"TIME_INPUT\",\"orgId\":99, \"pin\":99, \"pinType\":\"VIRTUAL\", " + "\"x\":0,\"y\":0,\"width\":1,\"height\":1}"));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99 82800 82860 Europe/Kiev 1"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 vw 99 82800 82860 Europe/Kiev 1")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        TimeInput timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals(82860, timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertArrayEquals(new int[]{ 1 }, timeInput.days);
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99 82800 82860 Europe/Kiev "))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "1-0 vw 99 82800 82860 Europe/Kiev ")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals(82860, timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99 82800  Europe/Kiev "))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(3, "1-0 vw 99 82800  Europe/Kiev ")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals((-1), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99 82800  Europe/Kiev 1,2,3,4"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(4, "1-0 vw 99 82800  Europe/Kiev 1,2,3,4")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals((-1), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, timeInput.days);
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99   Europe/Kiev 1,2,3,4"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(5, "1-0 vw 99   Europe/Kiev 1,2,3,4")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals((-1), timeInput.startAt);
        Assert.assertEquals((-1), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, timeInput.days);
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99 82800 82800 Europe/Kiev  10800"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(6, "1-0 vw 99 82800 82800 Europe/Kiev  10800")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals(82800, timeInput.startAt);
        Assert.assertEquals(82800, timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
        clientPair.hardwareClient.send(("hardware vw " + (TestUtil.b("99 ss sr Europe/Kiev  10800"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(7, "1-0 vw 99 ss sr Europe/Kiev  10800")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        timeInput = ((TimeInput) (profile.dashBoards[0].findWidgetByPin(0, ((short) (99)), VIRTUAL)));
        Assert.assertNotNull(timeInput);
        Assert.assertEquals((-2), timeInput.startAt);
        Assert.assertEquals((-3), timeInput.stopAt);
        Assert.assertEquals(ZoneId.of("Europe/Kiev"), timeInput.tzName);
        Assert.assertNull(timeInput.days);
    }

    @Test
    public void testWrongCommandForAggregation() throws Exception {
        clientPair.hardwareClient.send("hardware vw 10 aaaa");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 vw 10 aaaa")));
    }

    @Test
    public void testWrongPin() throws Exception {
        clientPair.hardwareClient.send("hardware vw x aaaa");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(1));
    }

    @Test
    public void testAppSendWAwWorks() throws Exception {
        String body = "aw 8 333";
        clientPair.hardwareClient.send(("hardware " + body));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 aw 8 333")));
    }

    @Test
    public void testClosedConnectionWhenNotLogged() throws Exception {
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.getDevice(1, 0);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertTrue(isClosed());
        appClient2.login(CounterBase.getUserName(), "1", "Android", "1RC7");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testRefreshTokenClosesExistingConnections() throws Exception {
        clientPair.appClient.send("refreshToken 1");
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        Assert.assertTrue(isClosed());
    }

    @Test
    public void testSendPinModeCommandWhenHardwareGoesOnline() throws Exception {
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        Assert.assertTrue(channelFuture.isDone());
        String body = "vw 13 1";
        clientPair.appClient.send(("hardware 1 " + body));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(1, DEVICE_NOT_IN_NETWORK)));
        TestHardClient hardClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        hardClient.login(clientPair.token);
        Mockito.verify(hardClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(hardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in")));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        stop().awaitUninterruptibly();
    }

    @Test
    public void testSendGeneratedPinModeCommandWhenHardwareGoesOnline() throws Exception {
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.awaitUninterruptibly();
        Assert.assertTrue(channelFuture.isDone());
        clientPair.appClient.send("hardware 1 vw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(1, DEVICE_NOT_IN_NETWORK)));
        TestHardClient hardClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        hardClient.login(clientPair.token);
        Mockito.verify(hardClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        String expectedBody = "pm 1 out 2 out 3 out 5 out 6 in 7 in 30 in 8 in";
        Mockito.verify(hardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, expectedBody)));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        stop().awaitUninterruptibly();
    }

    @Test
    public void testSendHardwareCommandToNotActiveDashboard() throws Exception {
        clientPair.appClient.createDash("{\"id\":2,\"name\":\"My Dashboard2\"}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.reset();
        clientPair.appClient.createDevice(2, new Device(2, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        clientPair.appClient.reset();
        // connecting separate hardware to non active dashboard
        TestHardClient nonActiveDashHardClient = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        nonActiveDashHardClient.login(device.token);
        Mockito.verify(nonActiveDashHardClient.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        nonActiveDashHardClient.reset();
        // sending hardware command from hardware that has no active dashboard
        nonActiveDashHardClient.send("hardware aw 1 1");
        // verify(nonActiveDashHardClient.responseMock, timeout(1000)).channelRead(any(), eq(new ResponseMessage(1, NO_ACTIVE_DASHBOARD)));
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000).times(1)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardwareConnected(1, ("2-" + (device.id)))));
        clientPair.hardwareClient.send("hardware aw 1 1");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, "1-0 aw 1 1")));
        stop().awaitUninterruptibly();
    }

    @Test
    public void testConnectAppAndHardwareAndSendCommands() throws Exception {
        for (int i = 0; i < 100; i++) {
            clientPair.appClient.send("hardware 1 aw 1 1");
        }
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testTryReachQuotaLimit() throws Exception {
        String body = "aw 100 100";
        // within 1 second sending more messages than default limit 100.
        for (int i = 0; i < 200; i++) {
            clientPair.hardwareClient.send(("hardware " + body));
            TestUtil.sleep(5);
        }
        ArgumentCaptor<ResponseMessage> objectArgumentCaptor = ArgumentCaptor.forClass(ResponseMessage.class);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), objectArgumentCaptor.capture());
        List<ResponseMessage> arguments = objectArgumentCaptor.getAllValues();
        ResponseMessage responseMessage = arguments.get(0);
        Assert.assertTrue(((responseMessage.id) > 100));
        // at least 100 iterations should be
        for (int i = 0; i < 100; i++) {
            Mockito.verify(clientPair.appClient.responseMock).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware((i + 1), ("1-0 " + body))));
        }
        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();
        // check no more accepted
        for (int i = 0; i < 10; i++) {
            clientPair.hardwareClient.send(("hardware " + body));
            TestUtil.sleep(9);
        }
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new ResponseMessage(1, QUOTA_LIMIT)));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(1, body)));
    }

    @Test
    public void testCreateProjectWithDevicesGeneratesNewTokens() throws Exception {
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 2;
        dashBoard.name = "Test Dash";
        Device device = new Device();
        device.id = 1;
        device.name = "MyDevice";
        device.token = "aaa";
        dashBoard.devices = new Device[]{ device };
        clientPair.appClient.createDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getDevices 2");
        Device[] devices = clientPair.appClient.parseDevices(2);
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        Assert.assertEquals(1, devices[0].id);
        Assert.assertEquals("MyDevice", devices[0].name);
        Assert.assertNotEquals("aaa", devices[0].token);
    }

    @Test
    public void testButtonStateInPWMModeIsStored() throws Exception {
        clientPair.appClient.createWidget(1, "{\"type\":\"BUTTON\",\"id\":1000,\"x\":0,\"y\":0,\"color\":616861439,\"width\":2,\"height\":2,\"label\":\"Relay\",\"pinType\":\"DIGITAL\",\"pin\":18,\"pwmMode\":true,\"rangeMappingOn\":false,\"min\":0,\"max\":0,\"value\":\"1\",\"pushMode\":false}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1 aw 18 1032");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "aw 18 1032")));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (18)), DIGITAL);
        Assert.assertNotNull(widget);
        Assert.assertEquals("1032", ((Button) (widget)).value);
    }

    @Test
    public void testTwoWidgetsOnTheSamePin() throws Exception {
        clientPair.appClient.createWidget(1, "{\"type\":\"BUTTON\",\"id\":1000,\"x\":0,\"y\":0,\"color\":616861439,\"width\":2,\"height\":2,\"label\":\"Relay\",\"pinType\":\"VIRTUAL\",\"pin\":37,\"pwmMode\":true,\"rangeMappingOn\":false,\"min\":0,\"max\":0,\"pushMode\":false}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"type\":\"BUTTON\",\"id\":1001,\"x\":0,\"y\":0,\"color\":616861439,\"width\":2,\"height\":2,\"label\":\"Relay\",\"pinType\":\"VIRTUAL\",\"pin\":37,\"pwmMode\":true,\"rangeMappingOn\":false,\"min\":0,\"max\":0,\"pushMode\":false}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("hardware 1 vw 37 10");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(3, "vw 37 10")));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(3);
        int counter = 0;
        for (Widget widget : profile.dashBoards[0].widgets) {
            if (widget.isSame(0, ((short) (37)), VIRTUAL)) {
                counter++;
                Assert.assertEquals("10", ((OnePinWidget) (widget)).value);
            }
        }
        Assert.assertEquals(2, counter);
        clientPair.hardwareClient.send("hardware vw 37 11");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 vw 37 11"));
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(5);
        counter = 0;
        for (Widget widget : profile.dashBoards[0].widgets) {
            if (widget.isSame(0, ((short) (37)), VIRTUAL)) {
                counter++;
                Assert.assertEquals("11", ((OnePinWidget) (widget)).value);
            }
        }
        Assert.assertEquals(2, counter);
    }

    @Test
    public void testButtonStateInPWMModeIsStoredWithUIHack() throws Exception {
        clientPair.appClient.createWidget(1, "{\"type\":\"BUTTON\",\"id\":1000,\"x\":0,\"y\":0,\"color\":616861439,\"width\":2,\"height\":2,\"label\":\"Relay\",\"pinType\":\"DIGITAL\",\"pin\":18,\"pwmMode\":true,\"rangeMappingOn\":false,\"min\":0,\"max\":0,\"value\":\"1\",\"pushMode\":false}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1 dw 18 1");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardware(2, "dw 18 1")));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        Widget widget = profile.dashBoards[0].findWidgetByPin(0, ((short) (18)), DIGITAL);
        Assert.assertNotNull(widget);
        Assert.assertEquals("1", ((Button) (widget)).value);
    }

    @Test
    public void testOutdatedAppNotificationAlertWorks() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.1.1");
        appClient.verifyResult(TestUtil.ok(1));
        Mockito.verify(appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.appIsOutdated(1, ("Your app is outdated. Please update to the latest app version. " + "Ignoring this notice may affect your projects."))));
    }

    @Test
    public void testOutdatedAppNotificationNotTriggered() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.1.2");
        appClient.verifyResult(TestUtil.ok(1));
        Mockito.verify(appClient.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.appIsOutdated(1, ("Your app is outdated. Please update to the latest app version. " + "Ignoring this notice may affect your projects."))));
    }

    @Test
    public void newUserReceivesGrettingEmailAndNoIPLogged() throws Exception {
        TestAppClient appClient1 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient1.register("test@blynk.cc", "a", "Blynk");
        appClient1.verifyResult(TestUtil.ok(1));
        User user = MainWorkflowTest.holder.userDao.getByName("test@blynk.cc", "Blynk");
        Assert.assertNull(user.lastLoggedIP);
        Mockito.verify(MainWorkflowTest.holder.mailWrapper).sendHtml(ArgumentMatchers.eq("test@blynk.cc"), ArgumentMatchers.eq("Get started with Blynk"), ArgumentMatchers.contains("Welcome to Blynk, a platform to build your next awesome IOT project."));
        appClient1.login("test@blynk.cc", "a");
        appClient1.verifyResult(TestUtil.ok(2));
        user = MainWorkflowTest.holder.userDao.getByName("test@blynk.cc", "Blynk");
        Assert.assertNull(user.lastLoggedIP);
    }

    @Test
    public void test() throws Exception {
        Twitter twitter = new Twitter();
        twitter.secret = "123";
        twitter.token = "124";
        DashBoard dash = new DashBoard();
        dash.sharedToken = "ffffffffffffffffffffffffffff";
        dash.widgets = new Widget[]{ twitter };
        System.out.println(JsonParser.init().writerFor(DashBoard.class).writeValueAsString(dash));
        System.out.println(JsonParser.init().writerFor(DashBoard.class).withView(PublicOnly.class).writeValueAsString(dash));
    }
}

