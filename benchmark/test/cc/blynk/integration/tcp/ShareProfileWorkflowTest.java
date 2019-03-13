package cc.blynk.integration.tcp;


import cc.blynk.integration.CounterBase;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DashboardSettings;
import cc.blynk.server.core.model.DataStream;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.serialization.JsonParser;
import cc.blynk.server.core.model.widgets.OnePinWidget;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.core.model.widgets.notifications.Twitter;
import cc.blynk.server.core.model.widgets.others.eventor.Eventor;
import cc.blynk.server.core.model.widgets.others.eventor.Rule;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.BaseAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinAction;
import cc.blynk.server.core.model.widgets.others.eventor.model.action.SetPinActionType;
import cc.blynk.server.core.model.widgets.others.eventor.model.condition.number.GreaterThan;
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
public class ShareProfileWorkflowTest extends SingleServerInstancePerTest {
    @Test
    public void testGetShareTokenNoDashId() throws Exception {
        clientPair.appClient.send("getShareToken");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(1)));
    }

    @Test
    public void testGetShareToken() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("loadProfileGzipped");
        Profile serverProfile = appClient2.parseProfile(2);
        DashBoard serverDash = serverProfile.dashBoards[0];
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Twitter twitter = profile.dashBoards[0].getTwitterWidget();
        ShareProfileWorkflowTest.clearPrivateData(twitter);
        Notification notification = profile.dashBoards[0].getNotificationWidget();
        ShareProfileWorkflowTest.clearPrivateData(notification);
        profile.dashBoards[0].updatedAt = serverDash.updatedAt;
        Assert.assertNull(serverDash.sharedToken);
        serverDash.devices = null;
        profile.dashBoards[0].devices = null;
        Assert.assertEquals(profile.dashBoards[0].toString(), serverDash.toString());
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(2);
        profile.dashBoards[0].updatedAt = 0;
        Notification originalNotification = profile.dashBoards[0].getNotificationWidget();
        Assert.assertNotNull(originalNotification);
        Assert.assertEquals(1, originalNotification.androidTokens.size());
        Assert.assertEquals("token", originalNotification.androidTokens.get("uid"));
        Twitter originalTwitter = profile.dashBoards[0].getTwitterWidget();
        Assert.assertNotNull(originalTwitter);
        Assert.assertEquals("token", originalTwitter.token);
        Assert.assertEquals("secret", originalTwitter.secret);
    }

    @Test
    public void getShareTokenAndLoginViaIt() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("hardware 1 vw 1 1");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, APP_SYNC, TestUtil.b("1 vw 1 1"))));
        appClient2.send("hardware 1 vw 2 2");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, APP_SYNC, TestUtil.b("1 vw 2 2"))));
        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();
        appClient2.reset();
        appClient2.send("hardware 1 ar 30");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("ar 30"))));
        clientPair.appClient.send("hardware 1 ar 30");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("ar 30"))));
        clientPair.hardwareClient.reset();
        clientPair.hardwareClient.send("ping");
        appClient2.send("hardware 1 pm 2 2");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.send("hardware 1 ar 30");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("ar 30"))));
        clientPair.appClient.send("hardware 1 pm 2 2");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(250).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("pm 2 2"))));
    }

    @Test
    public void getShareMultipleTokensAndLoginViaIt() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token1 = clientPair.appClient.getBody();
        Assert.assertNotNull(token1);
        Assert.assertEquals(32, token1.length());
        DashBoard dash = new DashBoard();
        dash.id = 2;
        dash.name = "test";
        clientPair.appClient.createDash(dash);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        DashboardSettings settings = new DashboardSettings(dash.name, true, Theme.Blynk, false, false, false, false, 0, false);
        clientPair.appClient.send(("updateSettings 2\u0000" + (JsonParser.toJson(settings))));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send("getShareToken 2");
        String token2 = clientPair.appClient.getBody(4);
        Assert.assertNotNull(token2);
        Assert.assertEquals(32, token2.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token1) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token2) + " Android 24"));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("hardware 1 vw 1 1");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(5, APP_SYNC, TestUtil.b("1 vw 1 1"))));
        appClient2.send("hardware 1 vw 2 2");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, APP_SYNC, TestUtil.b("1 vw 2 2"))));
        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();
        appClient2.reset();
        appClient2.send("hardware 1 ar 30");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("ar 30"))));
        clientPair.appClient.send("hardware 1 ar 30");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("ar 30"))));
        clientPair.hardwareClient.reset();
        clientPair.hardwareClient.send("ping");
        appClient2.send("hardware 1 pm 2 2");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.send("hardware 1 ar 30");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("ar 30"))));
        clientPair.appClient.send("hardware 1 pm 2 2");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(250).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("pm 2 2"))));
    }

    @Test
    public void testSharingChargingCorrect() throws Exception {
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, GET_ENERGY, "7500")));
        clientPair.appClient.reset();
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, GET_ENERGY, "6500")));
        clientPair.appClient.reset();
        clientPair.appClient.send("getShareToken 1");
        token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, GET_ENERGY, "6500")));
        clientPair.appClient.reset();
        clientPair.appClient.send("getShareToken 1");
        token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        clientPair.appClient.send("getShareToken 1");
        clientPair.appClient.send("getShareToken 1");
        clientPair.appClient.send("getShareToken 1");
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(5, GET_ENERGY, "6500")));
    }

    @Test
    public void checkStateWasChanged() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        appClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1 vw 1 1");
        appClient2.verifyResult(produce(2, APP_SYNC, TestUtil.b("1 vw 1 1")));
        appClient2.send("hardware 1 vw 2 2");
        clientPair.appClient.verifyResult(produce(2, APP_SYNC, TestUtil.b("1 vw 2 2")));
        clientPair.appClient.reset();
        appClient2.reset();
        // check from master side
        clientPair.appClient.send("hardware 1 aw 3 1");
        clientPair.hardwareClient.verifyResult(produce(1, HARDWARE, TestUtil.b("aw 3 1")));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        OnePinWidget tmp = ShareProfileWorkflowTest.getWidgetByPin(profile, 3);
        Assert.assertNotNull(tmp);
        Assert.assertEquals("1", tmp.value);
        // check from slave side
        appClient2.send("hardware 1 aw 3 150");
        clientPair.hardwareClient.verifyResult(produce(1, HARDWARE, TestUtil.b("aw 3 150")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        tmp = ShareProfileWorkflowTest.getWidgetByPin(profile, 3);
        Assert.assertNotNull(tmp);
        Assert.assertEquals("150", tmp.value);
        // check from hard side
        clientPair.hardwareClient.send("hardware aw 3 151");
        clientPair.appClient.verifyResult(produce(1, HARDWARE, TestUtil.b("1-0 aw 3 151")));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(1);
        tmp = ShareProfileWorkflowTest.getWidgetByPin(profile, 3);
        Assert.assertNotNull(tmp);
        Assert.assertEquals("151", tmp.value);
    }

    @Test
    public void checkSetPropertyWasChanged() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        appClient2.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("setProperty 1 color 123");
        clientPair.appClient.verifyResult(TestUtil.setProperty(1, "1-0 1 color 123"));
        appClient2.verifyResult(TestUtil.setProperty(1, "1-0 1 color 123"));
    }

    @Test
    public void checkSharingMessageWasReceived() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("sharing 1 off");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, SHARING, TestUtil.b("1 off"))));
    }

    @Test
    public void checkSharingMessageWasReceivedMultipleRecievers() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("sharing 1 off");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, SHARING, TestUtil.b("1 off"))));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, SHARING, TestUtil.b("1 off"))));
    }

    @Test
    public void checkSharingMessageWasReceivedAlsoForNonSharedApp() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.login(CounterBase.getUserName(), "1", "Android", "1.10.4");
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("sharing 1 off");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, SHARING, TestUtil.b("1 off"))));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, SHARING, TestUtil.b("1 off"))));
    }

    @Test
    public void eventorWorksInSharedModeFromAppSide() throws Exception {
        DataStream triggerDataStream = new DataStream(((short) (1)), PinType.VIRTUAL);
        DataStream dataStream = new DataStream(((short) (2)), PinType.VIRTUAL);
        SetPinAction setPinAction = new SetPinAction(dataStream, "123", SetPinActionType.CUSTOM);
        Rule rule = new Rule(triggerDataStream, null, new GreaterThan(37), new BaseAction[]{ setPinAction }, true);
        Eventor eventor = new Eventor();
        eventor.rules = new Rule[]{ rule };
        clientPair.appClient.createWidget(1, eventor);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody(2);
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("hardware 1 vw 1 38");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("vw 1 38"))));
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, APP_SYNC, TestUtil.b("1 vw 1 38"))));
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(888, "vw 2 123"));
        clientPair.appClient.verifyResult(TestUtil.hardware(888, "1-0 vw 2 123"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(888, HARDWARE, TestUtil.b("1-0 vw 2 123"))));
    }

    @Test
    public void checkBothClientsReceiveMessage() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        // check from hard side
        clientPair.hardwareClient.send("hardware aw 3 151");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 aw 3 151"))));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 aw 3 151"))));
        clientPair.hardwareClient.send("hardware aw 3 152");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("1-0 aw 3 152"))));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("1-0 aw 3 152"))));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        OnePinWidget tmp = ShareProfileWorkflowTest.getWidgetByPin(profile, 3);
        Assert.assertNotNull(tmp);
        Assert.assertEquals("152", tmp.value);
    }

    @Test
    public void wrongSharedToken() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send(((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + "a") + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(1)));
    }

    @Test
    public void revokeSharedToken() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.reset();
        appClient2.reset();
        Assert.assertFalse(isClosed());
        Assert.assertFalse(isClosed());
        clientPair.appClient.send("refreshShareToken 1");
        token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(1)));
        Assert.assertFalse(isClosed());
        Assert.assertTrue(isClosed());
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
    }

    @Test
    public void testDeactivateAndActivateForSubscriptions() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, DEACTIVATE_DASHBOARD, "1")));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, DEACTIVATE_DASHBOARD, "1")));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, ACTIVATE_DASHBOARD, "1")));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, ACTIVATE_DASHBOARD, "1")));
    }

    @Test
    public void testDeactivateOnLogout() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("deactivate");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, DEACTIVATE_DASHBOARD, "")));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, DEACTIVATE_DASHBOARD, "")));
    }

    @Test
    public void loadGzippedProfileForSharedBoard() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        String parentProfileString = clientPair.appClient.getBody();
        Profile parentProfile = JsonParser.parseProfileFromString(parentProfileString);
        appClient2.send("loadProfileGzipped");
        String body2 = appClient2.getBody(2);
        Twitter twitter = parentProfile.dashBoards[0].getTwitterWidget();
        ShareProfileWorkflowTest.clearPrivateData(twitter);
        Notification notification = parentProfile.dashBoards[0].getNotificationWidget();
        ShareProfileWorkflowTest.clearPrivateData(notification);
        for (Device device : parentProfile.dashBoards[0].devices) {
            device.token = null;
            device.hardwareInfo = null;
            device.deviceOtaInfo = null;
            device.lastLoggedIP = null;
            device.disconnectTime = 0;
            device.firstConnectTime = 0;
            device.dataReceivedAt = 0;
            device.connectTime = 0;
            device.status = null;
        }
        parentProfile.dashBoards[0].sharedToken = null;
        Assert.assertEquals(parentProfile.toString().replace("\"disconnectTime\":0,", "").replace("\"firstConnectTime\":0,", "").replace("\"dataReceivedAt\":0,", "").replace("\"connectTime\":0,", ""), body2);
    }

    @Test
    public void loadGzippedDashForSharedBoard() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.reset();
        clientPair.appClient.send("loadProfileGzipped");
        String parentProfileString = clientPair.appClient.getBody();
        Profile parentProfile = JsonParser.parseProfileFromString(parentProfileString);
        appClient2.send("loadProfileGzipped 1");
        String body2 = appClient2.getBody(2);
        Twitter twitter = parentProfile.dashBoards[0].getTwitterWidget();
        ShareProfileWorkflowTest.clearPrivateData(twitter);
        Notification notification = parentProfile.dashBoards[0].getNotificationWidget();
        ShareProfileWorkflowTest.clearPrivateData(notification);
        for (Device device : parentProfile.dashBoards[0].devices) {
            device.token = null;
            device.hardwareInfo = null;
            device.deviceOtaInfo = null;
            device.lastLoggedIP = null;
            device.disconnectTime = 0;
            device.firstConnectTime = 0;
            device.dataReceivedAt = 0;
            device.connectTime = 0;
            device.status = null;
        }
        parentProfile.dashBoards[0].sharedToken = null;
        Assert.assertEquals(parentProfile.dashBoards[0].toString().replace("\"disconnectTime\":0,", "").replace("\"firstConnectTime\":0,", "").replace("\"dataReceivedAt\":0,", "").replace("\"connectTime\":0,", ""), body2);
    }

    @Test
    public void testGetShareTokenAndRefresh() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("loadProfileGzipped");
        Profile serverProfile = appClient2.parseProfile(2);
        DashBoard dashboard = serverProfile.dashBoards[0];
        Assert.assertNotNull(dashboard);
        clientPair.appClient.reset();
        clientPair.appClient.send("refreshShareToken 1");
        String refreshedToken = clientPair.appClient.getBody();
        Assert.assertNotNull(refreshedToken);
        Assert.assertNotEquals(refreshedToken, token);
        TestAppClient appClient3 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient3.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient3.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(1)));
        TestAppClient appClient4 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient4.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + refreshedToken) + " Android 24"));
        Mockito.verify(appClient4.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient4.send("loadProfileGzipped");
        serverProfile = appClient4.parseProfile(2);
        DashBoard serverDash = serverProfile.dashBoards[0];
        Assert.assertNotNull(dashboard);
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Twitter twitter = profile.dashBoards[0].getTwitterWidget();
        ShareProfileWorkflowTest.clearPrivateData(twitter);
        Notification notification = profile.dashBoards[0].getNotificationWidget();
        ShareProfileWorkflowTest.clearPrivateData(notification);
        // one field update, cause it is hard to compare.
        profile.dashBoards[0].updatedAt = serverDash.updatedAt;
        Assert.assertNull(serverDash.sharedToken);
        serverDash.devices = null;
        profile.dashBoards[0].devices = null;
        Assert.assertEquals(profile.dashBoards[0].toString(), serverDash.toString());
        // System.out.println(dashboard);
    }

    @Test
    public void testMasterMasterSyncWorksWithoutToken() throws Exception {
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.login(CounterBase.getUserName(), "1", "Android", "24");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("hardware 1 vw 1 1");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, APP_SYNC, TestUtil.b("1 vw 1 1"))));
        appClient2.send("hardware 1 vw 2 2");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, APP_SYNC, TestUtil.b("1 vw 2 2"))));
    }

    @Test
    public void checkLogoutCommandForSharedApp() throws Exception {
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.send("hardware 1 vw 1 1");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, APP_SYNC, TestUtil.b("1 vw 1 1"))));
        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();
        appClient2.reset();
        appClient2.send("addPushToken 1\u0000uid2\u0000token2");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        appClient2.send("logout uid2");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
    }

    @Test
    public void testSharedProjectDoesntReceiveCommandFromOtherProjects() throws Exception {
        DashBoard dash = new DashBoard();
        dash.id = 333;
        dash.name = "AAAa";
        dash.isShared = true;
        Device device = new Device();
        device.id = 0;
        device.name = "123";
        dash.devices = new Device[]{ device };
        clientPair.appClient.createDash(dash);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getShareToken 333");
        String token = clientPair.appClient.getBody(2);
        Assert.assertNotNull(token);
        Assert.assertEquals(32, token.length());
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient2.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.hardwareClient.send("hardware vw 1 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 1"))));
        Mockito.verify(appClient2.responseMock, Mockito.never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 1 1"))));
    }
}

