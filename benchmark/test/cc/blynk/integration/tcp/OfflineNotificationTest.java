package cc.blynk.integration.tcp;


import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.DashboardSettings;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.core.model.enums.Theme;
import cc.blynk.server.core.model.serialization.JsonParser;
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
public class OfflineNotificationTest extends SingleServerInstancePerTest {
    private static int tcpHardPort;

    @Test
    public void testOfflineTimingIsCorrectForMultipleDevices() throws Exception {
        Device device2 = new Device(1, "My Device", BoardType.ESP8266);
        device2.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device2);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(2);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        Assert.assertEquals(1, devices[1].id);
        TestHardClient hardClient2 = new TestHardClient("localhost", OfflineNotificationTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100"))));
        hardClient2.verifyResult(TestUtil.ok(2));
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-0"));
        clientPair.appClient.never(TestUtil.deviceOffline(0, "1-1"));
        clientPair.appClient.reset();
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, TestUtil.b("1-1")));
        clientPair.appClient.never(TestUtil.deviceOffline(0, TestUtil.b("1-0")));
    }

    @Test
    public void testOfflineTimingIsCorrectForMultipleDevices2() throws Exception {
        Device device2 = new Device(1, "My Device", BoardType.ESP8266);
        device2.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device2);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(2);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        Assert.assertEquals(1, devices[1].id);
        TestHardClient hardClient2 = new TestHardClient("localhost", OfflineNotificationTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 1 buff-in 256 dev Arduino cpu ATmega328P con W5100"))));
        hardClient2.verifyResult(TestUtil.ok(2));
        stop();
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, TestUtil.b("1-0")));
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, TestUtil.b("1-1")));
    }

    @Test
    public void testTurnOffNotifications() throws Exception {
        DashboardSettings settings = new DashboardSettings("New Name", true, Theme.BlynkLight, true, true, true, false, 0, false);
        clientPair.appClient.send(("updateSettings 1\u0000" + (JsonParser.toJson(settings))));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(2);
        DashBoard dashBoard = profile.dashBoards[0];
        Assert.assertNotNull(dashBoard);
        Assert.assertEquals(settings.name, dashBoard.name);
        Assert.assertEquals(settings.isAppConnectedOn, dashBoard.isAppConnectedOn);
        Assert.assertEquals(settings.isNotificationsOff, dashBoard.isNotificationsOff);
        Assert.assertTrue(dashBoard.isNotificationsOff);
        Assert.assertEquals(settings.isShared, dashBoard.isShared);
        Assert.assertEquals(settings.keepScreenOn, dashBoard.keepScreenOn);
        Assert.assertEquals(settings.theme, dashBoard.theme);
        Assert.assertEquals(settings.widgetBackgroundOn, dashBoard.widgetBackgroundOn);
        stop();
        clientPair.appClient.neverAfter(500, TestUtil.deviceOffline(0, "1-0"));
        Device device2 = new Device(1, "My Device", BoardType.ESP8266);
        device2.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device2);
        Device device = clientPair.appClient.parseDevice(3);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(3, device));
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(4);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        Assert.assertEquals(1, devices[1].id);
        settings = new DashboardSettings("New Name", true, Theme.BlynkLight, true, true, false, false, 0, false);
        clientPair.appClient.send(("updateSettings 1\u0000" + (JsonParser.toJson(settings))));
        clientPair.appClient.verifyResult(TestUtil.ok(5));
        TestHardClient hardClient2 = new TestHardClient("localhost", OfflineNotificationTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        stop();
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, "1-1"));
    }

    @Test
    public void testTurnOffNotificationsAndNoDevices() throws Exception {
        DashboardSettings settings = new DashboardSettings("New Name", true, Theme.BlynkLight, true, true, true, false, 0, false);
        clientPair.appClient.send(("updateSettings 1\u0000" + (JsonParser.toJson(settings))));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        stop();
        clientPair.appClient.neverAfter(500, TestUtil.deviceOffline(0, "1-0"));
        clientPair.appClient.activate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
    }

    @Test
    public void deviceGoesOfflineAfterBeingIdle() throws Exception {
        Device device2 = new Device(1, "My Device", BoardType.ESP8266);
        device2.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device2);
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(2);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        Assert.assertEquals(1, devices[1].id);
        TestHardClient hardClient2 = new TestHardClient("localhost", OfflineNotificationTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 1 buff-in 256 dev Arduino cpu ATmega328P con W5100"))));
        hardClient2.verifyResult(TestUtil.ok(2));
        // just waiting 2.5 secs so server trigger idle event
        TestUtil.sleep(2500);
        clientPair.appClient.verifyResult(TestUtil.deviceOffline(0, TestUtil.b("1-1")));
    }

    @Test
    public void sessionDisconnectChangeState() throws Exception {
        Device device2 = new Device(1, "My Device", BoardType.ESP8266);
        device2.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device2);
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(2);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        Assert.assertEquals(1, devices[1].id);
        Assert.assertEquals(0, devices[1].disconnectTime);
        TestHardClient hardClient2 = new TestHardClient("localhost", OfflineNotificationTest.tcpHardPort);
        start();
        hardClient2.login(devices[1].token);
        hardClient2.verifyResult(TestUtil.ok(1));
        OfflineNotificationTest.holder.sessionDao.close();
        TestAppClient testAppClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        testAppClient.login(CounterBase.getUserName(), "1");
        testAppClient.verifyResult(TestUtil.ok(1));
        testAppClient.send("getDevices 1");
        devices = testAppClient.parseDevices(2);
        Assert.assertNotNull(devices);
        Assert.assertEquals(2, devices.length);
        Assert.assertEquals(1, devices[1].id);
        Assert.assertEquals(System.currentTimeMillis(), devices[1].disconnectTime, 5000);
    }
}

