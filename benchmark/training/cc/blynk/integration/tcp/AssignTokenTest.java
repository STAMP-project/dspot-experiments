package cc.blynk.integration.tcp;


import Status.ONLINE;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTestWithDB;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.integration.model.tcp.TestSslHardClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.db.model.FlashedToken;
import cc.blynk.utils.AppNameUtil;
import java.util.UUID;
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
public class AssignTokenTest extends SingleServerInstancePerTestWithDB {
    @Test
    public void testNoTokenExists() throws Exception {
        clientPair.appClient.send(("assignToken 1\u0000" + "123"));
        clientPair.appClient.verifyResult(TestUtil.notAllowed(1));
    }

    @Test
    public void testTokenActivate() throws Exception {
        FlashedToken[] list = new FlashedToken[1];
        String token = UUID.randomUUID().toString().replace("-", "");
        FlashedToken flashedToken = new FlashedToken("test@blynk.cc", token, AppNameUtil.BLYNK, 1, 0);
        list[0] = flashedToken;
        AssignTokenTest.holder.dbManager.insertFlashedTokens(list);
        clientPair.appClient.send(("assignToken 1\u0000" + (flashedToken.token)));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send(("assignToken 1\u0000" + (flashedToken.token)));
        clientPair.appClient.verifyResult(TestUtil.notAllowed(2));
    }

    @Test
    public void testCorrectToken() throws Exception {
        FlashedToken[] list = new FlashedToken[1];
        String token = UUID.randomUUID().toString().replace("-", "");
        FlashedToken flashedToken = new FlashedToken("test@blynk.cc", token, AppNameUtil.BLYNK, 1, 0);
        list[0] = flashedToken;
        AssignTokenTest.holder.dbManager.insertFlashedTokens(list);
        clientPair.appClient.send(("assignToken 1\u0000" + (flashedToken.token)));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        TestHardClient hardClient2 = new TestHardClient("localhost", SingleServerInstancePerTestWithDB.properties.getHttpPort());
        start();
        hardClient2.login(flashedToken.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(3);
        Assert.assertNotNull(devices);
        Assert.assertEquals(1, devices.length);
        Assert.assertEquals(flashedToken.token, devices[0].token);
        Assert.assertEquals(flashedToken.deviceId, devices[0].id);
        Assert.assertEquals(ONLINE, devices[0].status);
    }

    @Test
    public void testConnectTo443PortForHardware() throws Exception {
        clientPair.appClient.createDevice(1, new Device(1, "My Device", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        TestSslHardClient hardClient2 = new TestSslHardClient("localhost", SingleServerInstancePerTestWithDB.properties.getHttpsPort());
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
    }
}

