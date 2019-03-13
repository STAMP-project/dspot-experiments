package cc.blynk.integration.tcp;


import ImageType.JPG;
import PinType.DIGITAL;
import Status.OFFLINE;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTestWithDB;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.auth.App;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.enums.ProvisionType;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.Widget;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.model.widgets.outputs.Gauge;
import cc.blynk.server.core.model.widgets.outputs.graph.FontSize;
import cc.blynk.server.core.model.widgets.ui.tiles.DeviceTiles;
import cc.blynk.server.core.model.widgets.ui.tiles.TileTemplate;
import cc.blynk.server.db.model.FlashedToken;
import cc.blynk.server.notifications.mail.QrHolder;
import cc.blynk.utils.AppNameUtil;
import net.glxn.qrgen.javase.QRCode;
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
public class PublishingPreviewFlow extends SingleServerInstancePerTestWithDB {
    @Test
    public void testGetProjectByToken() throws Exception {
        App appObj = new App(null, Theme.BlynkLight, ProvisionType.STATIC, 0, false, "AppPreview", "myIcon", new int[]{ 1 });
        clientPair.appClient.createApp(appObj);
        App appFromApi = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(appFromApi);
        Assert.assertNotNull(appFromApi.id);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (appFromApi.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        QrHolder[] qrHolders = makeQRs(devices, 1);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "My Dashboard").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        clientPair.appClient.send(("getProjectByToken " + (qrHolders[0].token)));
        DashBoard dashBoard = clientPair.appClient.parseDash(3);
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
    }

    @Test
    public void testSendStaticEmailForAppPublish() throws Exception {
        App appObj = new App(null, Theme.BlynkLight, ProvisionType.STATIC, 0, false, "AppPreview", "myIcon", new int[]{ 1 });
        clientPair.appClient.createApp(appObj);
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        QrHolder[] qrHolders = makeQRs(devices, 1);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "My Dashboard").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        FlashedToken flashedToken = PublishingPreviewFlow.holder.dbManager.selectFlashedToken(qrHolders[0].token);
        Assert.assertNotNull(flashedToken);
        Assert.assertEquals(flashedToken.appId, app.id);
        Assert.assertEquals(1, flashedToken.dashId);
        Assert.assertEquals(0, flashedToken.deviceId);
        Assert.assertEquals(qrHolders[0].token, flashedToken.token);
        Assert.assertFalse(flashedToken.isActivated);
    }

    @Test
    public void testSendDynamicEmailForAppPublish() throws Exception {
        App appObj = new App(null, Theme.BlynkLight, ProvisionType.DYNAMIC, 0, false, "AppPreview", "myIcon", new int[]{ 1 });
        clientPair.appClient.createApp(appObj);
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        FlashedToken flashedToken = getFlashedTokenByDevice();
        Assert.assertNotNull(flashedToken);
        QrHolder qrHolder = new QrHolder(1, (-1), null, flashedToken.token, QRCode.from(flashedToken.token).to(JPG).stream().toByteArray());
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.dynamicMailBody.replace("{project_name}", "My Dashboard")), ArgumentMatchers.eq(qrHolder));
    }

    @Test
    public void testSendDynamicEmailForAppPublishAndNoDevices() throws Exception {
        App appObj = new App(null, Theme.BlynkLight, ProvisionType.DYNAMIC, 0, false, "AppPreview", "myIcon", new int[]{ 1 });
        clientPair.appClient.createApp(appObj);
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.reset();
        clientPair.appClient.deleteDevice(1, 0);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(1);
        Assert.assertEquals(0, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        FlashedToken flashedToken = getFlashedTokenByDevice();
        Assert.assertNotNull(flashedToken);
        QrHolder qrHolder = new QrHolder(1, (-1), null, flashedToken.token, QRCode.from(flashedToken.token).to(JPG).stream().toByteArray());
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.dynamicMailBody.replace("{project_name}", "My Dashboard")), ArgumentMatchers.eq(qrHolder));
    }

    @Test
    public void testSendDynamicEmailForAppPublishWithFewDevices() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        device1 = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device1);
        Assert.assertEquals(1, device1.id);
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"DYNAMIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(2);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(2, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        FlashedToken flashedToken = getFlashedTokenByDevice();
        Assert.assertNotNull(flashedToken);
        QrHolder qrHolder = new QrHolder(1, (-1), null, flashedToken.token, QRCode.from(flashedToken.token).to(JPG).stream().toByteArray());
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.dynamicMailBody.replace("{project_name}", "My Dashboard")), ArgumentMatchers.eq(qrHolder));
    }

    @Test
    public void testFaceEditNotAllowedHasNoChild() throws Exception {
        clientPair.appClient.send("updateFace 1");
        clientPair.appClient.verifyResult(TestUtil.notAllowed(1));
    }

    @Test
    public void testFaceUpdateWorks() throws Exception {
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        dashBoard.name = "Face Edit Test";
        clientPair.appClient.createDash(dashBoard);
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.createDevice(10, device0);
        Device device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(2, device)));
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[10]}");
        App app = clientPair.appClient.parseApp(3);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.send(("emailQr 10\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        QrHolder[] qrHolders = makeQRs(new Device[]{ device }, 10);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "Face Edit Test").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        appClient2.send("loadProfileGzipped");
        Profile profile = appClient2.parseProfile(3);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(0, dashBoard.widgets.length);
        clientPair.appClient.send("updateFace 1");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        Assert.assertTrue(isClosed());
        appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("loadProfileGzipped");
        profile = appClient2.parseProfile(2);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(16, dashBoard.widgets.length);
    }

    @Test
    public void testUpdateFaceDoesntEraseExistingDeviceTiles() throws Exception {
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        dashBoard.name = "Face Edit Test";
        clientPair.appClient.createDash(dashBoard);
        Device device0 = new Device(0, "My Dashboard", BoardType.ESP8266);
        device0.status = Status.ONLINE;
        clientPair.appClient.createDevice(10, device0);
        Device device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(2, device));
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[10]}");
        App app = clientPair.appClient.parseApp(3);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        // creating manually widget for child project
        clientPair.appClient.createWidget(10, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        TileTemplate tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, null, "123", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.send((("createTemplate " + (TestUtil.b((("10 " + widgetId) + " ")))) + (MAPPER.writeValueAsString(tileTemplate))));
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        // creating manually widget for parent project
        clientPair.appClient.send(("addEnergy " + (("10000" + "\u0000") + "1370-3990-1414-55681")));
        clientPair.appClient.createWidget(1, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, null, "123", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.send((("createTemplate " + (TestUtil.b((("1 " + widgetId) + " ")))) + (MAPPER.writeValueAsString(tileTemplate))));
        clientPair.appClient.verifyResult(TestUtil.ok(8));
        clientPair.appClient.send(("emailQr 10\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(9));
        QrHolder[] qrHolders = makeQRs(new Device[]{ device }, 10);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "Face Edit Test").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(2));
        appClient2.send("loadProfileGzipped");
        Profile profile = appClient2.parseProfile(3);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(1, dashBoard.widgets.length);
        Assert.assertTrue(((dashBoard.widgets[0]) instanceof DeviceTiles));
        deviceTiles = ((DeviceTiles) (dashBoard.getWidgetById(widgetId)));
        Assert.assertNotNull(deviceTiles.tiles);
        Assert.assertNotNull(deviceTiles.templates);
        Assert.assertEquals(0, deviceTiles.tiles.length);
        Assert.assertEquals(1, deviceTiles.templates.length);
        tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, new int[]{ 0 }, "123", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        appClient2.send((("updateTemplate " + (TestUtil.b((("1 " + widgetId) + " ")))) + (MAPPER.writeValueAsString(tileTemplate))));
        appClient2.verifyResult(TestUtil.ok(4));
        clientPair.appClient.send("updateFace 1");
        clientPair.appClient.verifyResult(TestUtil.ok(10));
        Assert.assertTrue(isClosed());
        appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("loadProfileGzipped");
        profile = appClient2.parseProfile(2);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(17, dashBoard.widgets.length);
        deviceTiles = ((DeviceTiles) (dashBoard.getWidgetById(widgetId)));
        Assert.assertNotNull(deviceTiles);
        Assert.assertNotNull(deviceTiles.tiles);
        Assert.assertNotNull(deviceTiles.templates);
        Assert.assertEquals(1, deviceTiles.tiles.length);
        Assert.assertEquals(0, deviceTiles.tiles[0].deviceId);
        Assert.assertEquals(1, deviceTiles.tiles[0].templateId);
        Assert.assertEquals(1, deviceTiles.templates.length);
        Assert.assertNotNull(deviceTiles.templates[0].deviceIds);
        Assert.assertEquals(1, deviceTiles.templates[0].deviceIds.length);
    }

    @Test
    public void testDeviceTilesAreNotCopiedFromParentProjectOnCreationAndFaceUpdate() throws Exception {
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        dashBoard.name = "Face Edit Test";
        clientPair.appClient.createDash(dashBoard);
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        clientPair.appClient.createDevice(10, device0);
        device0 = clientPair.appClient.parseDevice(2);
        clientPair.appClient.verifyResult(TestUtil.createDevice(2, device0));
        Device device2 = new Device(2, "My Dashboard", BoardType.Arduino_UNO);
        clientPair.appClient.createDevice(10, device2);
        device2 = clientPair.appClient.parseDevice(3);
        clientPair.appClient.verifyResult(TestUtil.createDevice(3, device2));
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[10]}");
        App app = clientPair.appClient.parseApp(4);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        // creating manually widget for child project
        clientPair.appClient.createWidget(10, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        TileTemplate tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, new int[]{ 2 }, "123", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.send((("createTemplate " + (TestUtil.b((("10 " + widgetId) + " ")))) + (MAPPER.writeValueAsString(tileTemplate))));
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        clientPair.appClient.createWidget(10, "{\"id\":155, \"deviceId\":0, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(2));
        appClient2.send("loadProfileGzipped");
        Profile profile = appClient2.parseProfile(3);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(1, dashBoard.devices.length);
        Assert.assertEquals(0, dashBoard.devices[0].id);
        Assert.assertEquals(2, dashBoard.widgets.length);
        Assert.assertTrue(((dashBoard.widgets[0]) instanceof DeviceTiles));
        deviceTiles = ((DeviceTiles) (dashBoard.getWidgetById(widgetId)));
        Assert.assertNotNull(deviceTiles.tiles);
        Assert.assertNotNull(deviceTiles.templates);
        Assert.assertEquals(0, deviceTiles.tiles.length);
        Assert.assertEquals(1, deviceTiles.templates.length);
    }

    @Test
    public void testChildProjectDoesntGetParentValues() throws Exception {
        DashBoard dashBoard = new DashBoard();
        dashBoard.id = 10;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        dashBoard.name = "Face Edit Test";
        clientPair.appClient.createDash(dashBoard);
        Device device0 = new Device(0, "My Dashboard", BoardType.Arduino_UNO);
        clientPair.appClient.createDevice(10, device0);
        device0 = clientPair.appClient.parseDevice(2);
        clientPair.appClient.verifyResult(TestUtil.createDevice(2, device0));
        Device device2 = new Device(2, "My Dashboard", BoardType.Arduino_UNO);
        clientPair.appClient.createDevice(10, device2);
        device2 = clientPair.appClient.parseDevice(3);
        clientPair.appClient.verifyResult(TestUtil.createDevice(3, device2));
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[10]}");
        App app = clientPair.appClient.parseApp(4);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        long widgetId = 21321;
        DeviceTiles deviceTiles = new DeviceTiles();
        deviceTiles.id = widgetId;
        deviceTiles.x = 8;
        deviceTiles.y = 8;
        deviceTiles.width = 50;
        deviceTiles.height = 100;
        // creating manually widget for child project
        clientPair.appClient.createWidget(10, deviceTiles);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        TileTemplate tileTemplate = new cc.blynk.server.core.model.widgets.ui.tiles.templates.PageTileTemplate(1, null, new int[]{ 2 }, "123", "name", "iconName", BoardType.ESP8266, null, false, null, null, null, 0, 0, FontSize.LARGE, false, 2);
        clientPair.appClient.send((("createTemplate " + (TestUtil.b((("10 " + widgetId) + " ")))) + (MAPPER.writeValueAsString(tileTemplate))));
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        clientPair.appClient.createWidget(1, "{\"id\":155, \"value\":\"data\", \"deviceId\":0, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(2));
        appClient2.send("loadProfileGzipped");
        Profile profile = appClient2.parseProfile(3);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(1, dashBoard.widgets.length);
        Assert.assertTrue(((dashBoard.widgets[0]) instanceof DeviceTiles));
        deviceTiles = ((DeviceTiles) (dashBoard.getWidgetById(widgetId)));
        Assert.assertNotNull(deviceTiles.tiles);
        Assert.assertNotNull(deviceTiles.templates);
        Assert.assertEquals(0, deviceTiles.tiles.length);
        Assert.assertEquals(1, deviceTiles.templates.length);
        Gauge gauge = ((Gauge) (dashBoard.getWidgetById(155)));
        Assert.assertNull(gauge);
        clientPair.appClient.send("updateFace 1");
        clientPair.appClient.verifyResult(TestUtil.ok(8));
        if (!(isClosed())) {
            TestUtil.sleep(300);
        }
        Assert.assertTrue(isClosed());
        appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.send("loadProfileGzipped");
        profile = appClient2.parseProfile(2);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        gauge = ((Gauge) (dashBoard.getWidgetById(155)));
        Assert.assertNotNull(gauge);
        Assert.assertNull(gauge.value);
        // one more time, to check another branch
        clientPair.appClient.send("updateFace 1");
        clientPair.appClient.verifyResult(TestUtil.ok(9));
        Assert.assertTrue(isClosed());
        appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.send("loadProfileGzipped");
        profile = appClient2.parseProfile(2);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        gauge = ((Gauge) (dashBoard.getWidgetById(155)));
        Assert.assertNotNull(gauge);
        Assert.assertNull(gauge.value);
    }

    @Test
    public void testFaceEditForRestrictiveFields() throws Exception {
        Profile profile = JsonParser.parseProfileFromString(TestUtil.readTestUserProfile());
        DashBoard dashBoard = profile.dashBoards[0];
        dashBoard.id = 10;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        dashBoard.name = "Face Edit Test";
        dashBoard.devices = null;
        clientPair.appClient.createDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Device device0 = new Device(0, "My Device", BoardType.Arduino_UNO);
        device0.status = Status.ONLINE;
        clientPair.appClient.createDevice(10, device0);
        Device device = clientPair.appClient.parseDevice(2);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(2, device));
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[10]}");
        App app = clientPair.appClient.parseApp(3);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.send(("emailQr 10\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        QrHolder[] qrHolders = makeQRs(new Device[]{ device }, 10);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "Face Edit Test").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(2));
        appClient2.send("loadProfileGzipped");
        profile = appClient2.parseProfile(3);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(16, dashBoard.widgets.length);
        clientPair.appClient.send("addPushToken 1\u0000uid1\u0000token1");
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.updateWidget(1, "{\"id\":10, \"height\":2, \"width\":1, \"x\":22, \"y\":23, \"username\":\"pupkin@gmail.com\", \"token\":\"token\", \"secret\":\"secret\", \"type\":\"TWITTER\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(6));
        clientPair.appClient.send("updateFace 1");
        clientPair.appClient.verifyResult(TestUtil.ok(7));
        Assert.assertTrue(isClosed());
        appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("loadProfileGzipped");
        profile = appClient2.parseProfile(2);
        Assert.assertEquals(1, profile.dashBoards.length);
        dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        Assert.assertEquals(1, dashBoard.parentId);
        Assert.assertEquals(16, dashBoard.widgets.length);
        Notification notification = dashBoard.getNotificationWidget();
        Assert.assertEquals(0, notification.androidTokens.size());
        Assert.assertEquals(0, notification.iOSTokens.size());
        Twitter twitter = dashBoard.getTwitterWidget();
        Assert.assertNull(twitter.username);
        Assert.assertNull(twitter.token);
        Assert.assertNull(twitter.secret);
        Assert.assertEquals(22, twitter.x);
        Assert.assertEquals(23, twitter.y);
    }

    @Test
    public void testDeleteWorksForPreviewApp() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        QrHolder[] qrHolders = makeQRs(devices, 1);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "My Dashboard").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        clientPair.appClient.send(((((("loadProfileGzipped " + (qrHolders[0].token)) + " ") + (qrHolders[0].dashId)) + " ") + (CounterBase.getUserName())));
        DashBoard dashBoard = clientPair.appClient.parseDash(3);
        Assert.assertNotNull(dashBoard);
        Assert.assertNotNull(dashBoard.devices);
        Assert.assertNull(dashBoard.devices[0].token);
        Assert.assertNull(dashBoard.devices[0].lastLoggedIP);
        Assert.assertEquals(0, dashBoard.devices[0].disconnectTime);
        Assert.assertEquals(OFFLINE, dashBoard.devices[0].status);
        dashBoard.id = 2;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        clientPair.appClient.createDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.deleteDash(2);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.send("loadProfileGzipped 1");
        dashBoard = clientPair.appClient.parseDash(6);
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(1, dashBoard.id);
        clientPair.appClient.send("loadProfileGzipped 2");
        clientPair.appClient.verifyResult(TestUtil.illegalCommand(7));
    }

    @Test
    public void testDeleteWorksForParentOfPreviewApp() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"AppPreview\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        QrHolder[] qrHolders = makeQRs(devices, 1);
        StringBuilder sb = new StringBuilder();
        qrHolders[0].attach(sb);
        Mockito.verify(PublishingPreviewFlow.holder.mailWrapper, Mockito.timeout(500)).sendWithAttachment(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("AppPreview" + " - App details")), ArgumentMatchers.eq(PublishingPreviewFlow.holder.textHolder.staticMailBody.replace("{project_name}", "My Dashboard").replace(DYNAMIC_SECTION, sb.toString())), ArgumentMatchers.eq(qrHolders));
        clientPair.appClient.send(((((("loadProfileGzipped " + (qrHolders[0].token)) + " ") + (qrHolders[0].dashId)) + " ") + (CounterBase.getUserName())));
        DashBoard dashBoard = clientPair.appClient.parseDash(3);
        Assert.assertNotNull(dashBoard);
        Assert.assertNotNull(dashBoard.devices);
        Assert.assertNull(dashBoard.devices[0].token);
        Assert.assertNull(dashBoard.devices[0].lastLoggedIP);
        Assert.assertEquals(0, dashBoard.devices[0].disconnectTime);
        Assert.assertEquals(OFFLINE, dashBoard.devices[0].status);
        dashBoard.id = 2;
        dashBoard.parentId = 1;
        dashBoard.isPreview = true;
        clientPair.appClient.createDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.deleteDash(1);
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertNotNull(profile);
        Assert.assertNotNull(profile.dashBoards);
        Assert.assertEquals(1, profile.dashBoards.length);
        clientPair.appClient.send("loadProfileGzipped 2");
        String response = clientPair.appClient.getBody(2);
        Assert.assertNotNull(response);
    }

    @Test
    public void testExportedAppFlowWithOneDynamicTest() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"DYNAMIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(2));
        appClient2.send("loadProfileGzipped 1");
        DashBoard dashBoard = appClient2.parseDash(3);
        Assert.assertNotNull(dashBoard);
        Device device = dashBoard.devices[0];
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        TestHardClient hardClient1 = new TestHardClient("localhost", SingleServerInstancePerTestWithDB.properties.getHttpPort());
        start();
        hardClient1.login(device.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        appClient2.verifyResult(TestUtil.hardwareConnected(1, "1-0"));
        hardClient1.send("hardware vw 1 100");
        appClient2.verifyResult(TestUtil.hardware(2, "1-0 vw 1 100"));
    }

    @Test
    public void testFullDynamicAppFlow() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Blynk\",\"provisionType\":\"DYNAMIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = clientPair.appClient.parseApp(1);
        Assert.assertNotNull(app);
        Assert.assertNotNull(app.id);
        clientPair.hardwareClient.send("hardware dw 1 abc");
        clientPair.hardwareClient.send("hardware vw 77 123");
        clientPair.appClient.verifyResult(TestUtil.hardware(1, "1-0 dw 1 abc"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 vw 77 123"));
        clientPair.appClient.send("loadProfileGzipped 1");
        DashBoard dashBoard = clientPair.appClient.parseDash(4);
        Assert.assertNotNull(dashBoard);
        Assert.assertNotNull(dashBoard.pinsStorage);
        Assert.assertEquals(0, dashBoard.pinsStorage.size());
        Widget w = dashBoard.findWidgetByPin(0, ((short) (1)), DIGITAL);
        Assert.assertNotNull(w);
        Assert.assertEquals("abc", ((OnePinWidget) (w)).value);
        clientPair.appClient.reset();
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        clientPair.appClient.send(("emailQr 1\u0000" + (app.id)));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        QrHolder[] qrHolders = makeQRs(devices, 1);
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTestWithDB.properties);
        start();
        appClient2.register("test@blynk.cc", "a", app.id);
        appClient2.verifyResult(TestUtil.ok(1));
        appClient2.login("test@blynk.cc", "a", "Android", "1.10.4", app.id);
        appClient2.verifyResult(TestUtil.ok(2));
        appClient2.send(((((((("loadProfileGzipped " + (qrHolders[0].token)) + "\u0000") + 1) + "\u0000") + (CounterBase.getUserName())) + "\u0000") + (AppNameUtil.BLYNK)));
        dashBoard = appClient2.parseDash(3);
        Assert.assertNotNull(dashBoard);
        Assert.assertNotNull(dashBoard.pinsStorage);
        Assert.assertTrue(dashBoard.pinsStorage.isEmpty());
        w = dashBoard.findWidgetByPin(0, ((short) (1)), DIGITAL);
        Assert.assertNotNull(w);
        Assert.assertNull(((OnePinWidget) (w)).value);
        Device device = dashBoard.devices[0];
        Assert.assertNotNull(device);
        Assert.assertNull(device.token);
        appClient2.reset();
        appClient2.getDevice(1, device.id);
        device = appClient2.parseDevice();
        TestHardClient hardClient1 = new TestHardClient("localhost", SingleServerInstancePerTestWithDB.properties.getHttpPort());
        start();
        hardClient1.login(device.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        appClient2.verifyResult(TestUtil.hardwareConnected(1, "1-0"));
        hardClient1.send("hardware vw 1 100");
        appClient2.verifyResult(TestUtil.hardware(2, "1-0 vw 1 100"));
    }
}

