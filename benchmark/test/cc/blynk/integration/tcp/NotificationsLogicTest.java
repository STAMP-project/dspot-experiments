package cc.blynk.integration.tcp;


import AppNameUtil.BLYNK;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.widgets.notifications.Notification;
import cc.blynk.server.notifications.push.android.AndroidGCMMessage;
import io.netty.channel.ChannelFuture;
import java.util.Map;
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
public class NotificationsLogicTest extends SingleServerInstancePerTest {
    private static int tcpHardPort;

    @Test
    public void addPushTokenWrongInput() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register("test@test.com", "1", BLYNK);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login("test@test.com", "1", "Android", "RC13");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.createDash("{\"id\":1, \"createdAt\":1, \"name\":\"test board\"}");
        appClient.verifyResult(TestUtil.ok(3));
        appClient.send("addPushToken 1\u0000uid\u0000token");
        Mockito.verify(appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.notAllowed(4)));
    }

    @Test
    public void addPushTokenWorksForAndroid() throws Exception {
        clientPair.appClient.send("addPushToken 1\u0000uid1\u0000token1");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        Notification notification = profile.getDashById(1).getNotificationWidget();
        Assert.assertNotNull(notification);
        Assert.assertEquals(2, notification.androidTokens.size());
        Assert.assertEquals(0, notification.iOSTokens.size());
        Assert.assertTrue(notification.androidTokens.containsKey("uid1"));
        Assert.assertTrue(notification.androidTokens.containsValue("token1"));
    }

    @Test
    public void addPushTokenNotOverridedOnProfileSave() throws Exception {
        clientPair.appClient.send("addPushToken 1\u0000uid1\u0000token1");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        Notification notification = profile.getDashById(1).getNotificationWidget();
        Assert.assertNotNull(notification);
        Assert.assertEquals(2, notification.androidTokens.size());
        Assert.assertEquals(0, notification.iOSTokens.size());
        Assert.assertTrue(notification.androidTokens.containsKey("uid1"));
        Assert.assertTrue(notification.androidTokens.containsValue("token1"));
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send("loadProfileGzipped");
        profile = clientPair.appClient.parseProfile(4);
        notification = profile.getDashById(1).getNotificationWidget();
        Assert.assertNotNull(notification);
        Assert.assertEquals(2, notification.androidTokens.size());
        Assert.assertEquals(0, notification.iOSTokens.size());
        Assert.assertTrue(notification.androidTokens.containsKey("uid1"));
        Assert.assertTrue(notification.androidTokens.containsValue("token1"));
    }

    @Test
    public void addPushTokenWorksForIos() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "iOS", "1.10.2");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("addPushToken 1\u0000uid2\u0000token2");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.reset();
        appClient.send("loadProfileGzipped");
        Profile profile = appClient.parseProfile(1);
        Notification notification = profile.getDashById(1).getNotificationWidget();
        Assert.assertNotNull(notification);
        Assert.assertEquals(1, notification.androidTokens.size());
        Assert.assertEquals(1, notification.iOSTokens.size());
        Map.Entry<String, String> entry = notification.iOSTokens.entrySet().iterator().next();
        Assert.assertEquals("uid2", entry.getKey());
        Assert.assertEquals("token2", entry.getValue());
    }

    @Test
    public void testHardwareDeviceWentOffline() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = false;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
    }

    @Test
    public void testHardwareDeviceWentOfflineForSecondDeviceSameToken() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = false;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        TestHardClient newHardClient = new TestHardClient("localhost", NotificationsLogicTest.tcpHardPort);
        start();
        newHardClient.login(clientPair.token);
        Mockito.verify(newHardClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        stop();
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.deviceOffline(0, "1-0")));
    }

    @Test
    public void testHardwareDeviceWentOfflineForSecondDeviceNewToken() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = false;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        Device device1 = new Device(1, "Name", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.getDevice(1, 1);
        device = clientPair.appClient.parseDevice(2);
        TestHardClient newHardClient = new TestHardClient("localhost", NotificationsLogicTest.tcpHardPort);
        start();
        newHardClient.login(device.token);
        newHardClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, ("1-" + (device.id))));
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, ("1-" + (device.id))));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushWorks() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testNotifWidgetOverrideProjectSetting() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        DashBoard dashBoard = profile.getDashById(1);
        dashBoard.isNotificationsOff = true;
        Notification notification = dashBoard.getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
        clientPair.appClient.never(TestUtil.deviceOffline(0, "1-0"));
    }

    @Test
    public void testNoOfflineNotifsExpected() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        DashBoard dashBoard = profile.getDashById(1);
        dashBoard.isNotificationsOff = true;
        Notification notification = dashBoard.getNotificationWidget();
        notification.notifyWhenOffline = false;
        clientPair.appClient.updateDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.never(TestUtil.deviceOffline(0, "1-0"));
    }

    @Test
    public void testOfflineNotifsExpectedButNotPush() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        DashBoard dashBoard = profile.getDashById(1);
        dashBoard.isNotificationsOff = false;
        Notification notification = dashBoard.getNotificationWidget();
        notification.notifyWhenOffline = false;
        clientPair.appClient.updateDash(dashBoard);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushNotWorksForLogoutUser() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.send("logout");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.send("logout");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushNotWorksForLogoutUserWithUID() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.send("logout uid");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        clientPair.appClient.send("logout");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushNotWorksForLogoutUserWithWrongUID() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.send("logout uidxxx");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500)).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq("uid"));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushNotWorksForLogoutUser2() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.getDevice(1, 0);
        Device device = clientPair.appClient.parseDevice(2);
        clientPair.appClient.send("logout");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        stop().await();
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.10.4");
        appClient.verifyResult(TestUtil.ok(1));
        TestHardClient hardClient = new TestHardClient("localhost", NotificationsLogicTest.tcpHardPort);
        start();
        hardClient.login(device.token);
        hardClient.verifyResult(TestUtil.ok(1));
        appClient.send("addPushToken 1\u0000uid\u0000token");
        appClient.verifyResult(TestUtil.ok(2));
        stop().await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq("uid"));
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testLoginWith2AppsAndLogoutFrom1() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.10.4");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("addPushToken 1\u0000uid2\u0000token2");
        appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("logout uid");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        stop().await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq("uid2"));
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testLoginWith2AppsAndLogoutFrom2() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.getDevice(1, 0);
        Device device = clientPair.appClient.parseDevice(2);
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.10.4");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("addPushToken 1\u0000uid2\u0000token2");
        appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("logout");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        stop().await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq("uid2"));
    }

    @Test
    public void testLoginWithSharedAppAndLogoutFrom() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.send("getShareToken 1");
        String token = clientPair.appClient.getBody();
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.send((((("shareLogin " + (CounterBase.getUserName())) + " ") + token) + " Android 24"));
        appClient.send("addPushToken 1\u0000uid2\u0000token2");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.send("logout uid2");
        appClient.verifyResult(TestUtil.ok(3));
        stop().await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(500).never()).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.eq("uid2"));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushDelayedWorks() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        notification.notifyWhenOfflineIgnorePeriod = 1000;
        long now = System.currentTimeMillis();
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        stop();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(2000).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        Assert.assertTrue((((System.currentTimeMillis()) - now) > (notification.notifyWhenOfflineIgnorePeriod)));
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
    }

    @Test
    public void testHardwareDeviceWentOfflineAndPushDelayedNotTriggeredDueToReconnect() throws Exception {
        Profile profile = TestUtil.parseProfile(TestUtil.readTestUserProfile());
        Notification notification = profile.getDashById(1).getNotificationWidget();
        notification.notifyWhenOffline = true;
        notification.notifyWhenOfflineIgnorePeriod = 1000;
        clientPair.appClient.updateDash(profile.getDashById(1));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        clientPair.appClient.getDevice(1, 0);
        Device device = clientPair.appClient.parseDevice(2);
        TestHardClient newHardClient = new TestHardClient("localhost", NotificationsLogicTest.tcpHardPort);
        start();
        newHardClient.login(device.token);
        newHardClient.verifyResult(TestUtil.ok(1));
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.after(1500).never()).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testCreateNewNotificationWidget() throws Exception {
        clientPair.appClient.deleteWidget(1, 9);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"id\":9, \"x\":1, \"y\":1, \"width\":1, \"height\":1, \"type\":\"NOTIFICATION\", \"notifyWhenOfflineIgnorePeriod\":0, \"priority\":\"high\", \"notifyWhenOffline\":true}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("addPushToken 1\u0000uid1\u0000token1");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.updateWidget(1, "{\"id\":9, \"x\":1, \"y\":1, \"width\":1, \"height\":1, \"type\":\"NOTIFICATION\", \"notifyWhenOfflineIgnorePeriod\":0, \"priority\":\"high\", \"notifyWhenOffline\":false}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.send("push 123");
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testPushWhenHardwareOffline() throws Exception {
        ChannelFuture channelFuture = clientPair.hardwareClient.stop();
        channelFuture.await();
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(750).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testPushHandler() throws Exception {
        clientPair.hardwareClient.send("push Yo!");
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testPushHandlerWithPlaceHolder() throws Exception {
        clientPair.hardwareClient.send("push Yo {DEVICE_NAME}!");
        ArgumentCaptor<AndroidGCMMessage> objectArgumentCaptor = ArgumentCaptor.forClass(AndroidGCMMessage.class);
        Mockito.verify(NotificationsLogicTest.holder.gcmWrapper, Mockito.timeout(500).times(1)).send(objectArgumentCaptor.capture(), ArgumentMatchers.any(), ArgumentMatchers.any());
        AndroidGCMMessage message = objectArgumentCaptor.getValue();
        String expectedJson = toJson();
        Assert.assertEquals(expectedJson, message.toJson());
    }

    @Test
    public void testOfflineMessageIsSentToBothApps() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "iOS", "1.10.2");
        appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.deleteWidget(1, 9);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
    }
}

