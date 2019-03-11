package cc.blynk.integration.tcp;


import AppNameUtil.BLYNK;
import LRUCache.LOGIN_TOKENS_CACHE;
import cc.blynk.integration.BaseTest;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.Holder;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
import cc.blynk.server.servers.BaseServer;
import cc.blynk.utils.AppNameUtil;
import cc.blynk.utils.properties.ServerProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 5/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoadBalancingIntegrationTest extends BaseTest {
    private BaseServer appServer1;

    private BaseServer hardwareServer1;

    private Holder holder;

    private BaseServer appServer2;

    private BaseServer hardwareServer2;

    private int tcpAppPort2;

    private int plainHardPort2;

    private Holder holder2;

    private ServerProperties properties2;

    private ClientPair clientPair;

    @Test
    public void testNoGetServerHandlerAfterLogin() throws Exception {
        TestAppClient appClient1 = new TestAppClient(BaseTest.properties);
        start();
        workflowForUser(appClient1, "123@gmail.com", "a", BLYNK);
        appClient1.send((("getServer " + ("123@gmail.com" + "\u0000")) + (AppNameUtil.BLYNK)));
        appClient1.neverAfter(500, TestUtil.getServer(1, "127.0.0.1"));
    }

    @Test
    public void testCreateFewAccountWithDifferentApp() throws Exception {
        TestAppClient appClient1 = new TestAppClient(BaseTest.properties);
        start();
        String email = "test@gmmail.com";
        String pass = "a";
        String appName = "Blynk";
        appClient1.send("getServer");
        appClient1.verifyResult(TestUtil.illegalCommand(1));
        appClient1.send(((("getServer " + email) + "\u0000") + appName));
        appClient1.verifyResult(TestUtil.getServer(2, "127.0.0.1"));
        appClient1.register(email, pass, appName);
        appClient1.verifyResult(TestUtil.ok(3));
        appClient1.login(email, pass, "Android", ("1.10.4 " + appName));
        // we should wait until login finished. Only after that we can send commands
        appClient1.verifyResult(TestUtil.ok(4));
        appClient1.send(((("getServer " + email) + "\u0000") + appName));
        appClient1.never(TestUtil.getServer(5, "127.0.0.1"));
    }

    @Test
    public void testNoRedirectAsTokenIsWrong() throws Exception {
        TestHardClient hardClient = new TestHardClient("localhost", BaseTest.tcpHardPort);
        start();
        hardClient.login("123");
        hardClient.verifyResult(TestUtil.invalidToken(1));
        holder.dbManager.assignServerToToken("123", "127.0.0.1", "user", 0, 0);
        hardClient.login("123");
        hardClient.verifyResult(TestUtil.invalidToken(2));
        hardClient.login("\u0000");
        hardClient.verifyResult(TestUtil.invalidToken(3));
    }

    @Test
    public void hardwareCreatedAndServerStoredInDB() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResultAfter(500, TestUtil.createDevice(1, device));
        Assert.assertEquals("127.0.0.1", holder.dbManager.forwardingTokenDBDao.selectHostByToken(device.token));
    }

    @Test
    public void hardwareCreatedAndServerStoredInDBAndDelete() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResultAfter(1000, TestUtil.createDevice(1, device));
        Assert.assertEquals("127.0.0.1", holder.dbManager.forwardingTokenDBDao.selectHostByToken(device.token));
        clientPair.appClient.send(("deleteDevice 1\u0000" + (device.id)));
        clientPair.appClient.verifyResultAfter(1000, TestUtil.ok(2));
        Assert.assertNull(holder.dbManager.forwardingTokenDBDao.selectHostByToken(device.token));
    }

    @Test
    public void redirectForHardwareWorks() throws Exception {
        String token = "12345678901234567890123456789012";
        Assert.assertTrue(holder.dbManager.forwardingTokenDBDao.insertTokenHost(token, "test_host", CounterBase.getUserName(), 0, 0));
        TestHardClient hardClient = new TestHardClient("localhost", BaseTest.tcpHardPort);
        start();
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(1, ("test_host " + (BaseTest.tcpHardPort))));
    }

    @Test
    public void redirectForHardwareWorksWithForce80Port() throws Exception {
        String token = "12345678901234567890123456789013";
        Assert.assertTrue(holder.dbManager.forwardingTokenDBDao.insertTokenHost(token, "test_host", CounterBase.getUserName(), 0, 0));
        TestHardClient hardClient = new TestHardClient("localhost", plainHardPort2);
        start();
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(1, ("test_host " + 80)));
    }

    @Test
    public void testInvalidToken() throws Exception {
        String token = "1234567890123456789012345678901";
        Assert.assertTrue(holder.dbManager.forwardingTokenDBDao.insertTokenHost(token, "test_host", CounterBase.getUserName(), 0, 0));
        TestHardClient hardClient = new TestHardClient("localhost", BaseTest.tcpHardPort);
        start();
        hardClient.login(token);
        Mockito.verify(hardClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.invalidToken(1)));
    }

    @Test
    public void redirectForHardwareWorksFromCache() throws Exception {
        String token = "12345678901234567890123456789013";
        Assert.assertTrue(holder.dbManager.forwardingTokenDBDao.insertTokenHost(token, "test_host", CounterBase.getUserName(), 0, 0));
        TestHardClient hardClient = new TestHardClient("localhost", BaseTest.tcpHardPort);
        start();
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(1, ("test_host " + (BaseTest.tcpHardPort))));
        holder.dbManager.executeSQL("DELETE FROM forwarding_tokens");
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(2, ("test_host " + (BaseTest.tcpHardPort))));
    }

    @Test
    public void redirectForHardwareDoesntWorkFromInvalidatedCache() throws Exception {
        String token = "12345678901234567890123456789012";
        Assert.assertTrue(holder.dbManager.forwardingTokenDBDao.insertTokenHost(token, "test_host", CounterBase.getUserName(), 0, 0));
        TestHardClient hardClient = new TestHardClient("localhost", BaseTest.tcpHardPort);
        start();
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(1, ("test_host " + (BaseTest.tcpHardPort))));
        holder.dbManager.executeSQL("DELETE FROM forwarding_tokens");
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(2, ("test_host " + (BaseTest.tcpHardPort))));
        LOGIN_TOKENS_CACHE.clear();
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.invalidToken(3));
        Assert.assertTrue(holder.dbManager.forwardingTokenDBDao.insertTokenHost(token, "test_host_2", CounterBase.getUserName(), 0, 0));
        LOGIN_TOKENS_CACHE.clear();
        hardClient.login(token);
        hardClient.verifyResult(TestUtil.connectRedirect(4, ("test_host_2 " + (BaseTest.tcpHardPort))));
    }
}

