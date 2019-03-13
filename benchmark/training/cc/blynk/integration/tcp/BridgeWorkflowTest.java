package cc.blynk.integration.tcp;


import AppNameUtil.BLYNK;
import PinType.ANALOG;
import PinType.VIRTUAL;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import cc.blynk.server.core.model.device.Status;
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
public class BridgeWorkflowTest extends SingleServerInstancePerTest {
    private static int tcpHardPort;

    @Test
    public void testBridgeInitOk() throws Exception {
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testBridgeInitIllegalCommand() throws Exception {
        clientPair.hardwareClient.send("bridge 1 i");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(1));
        clientPair.hardwareClient.send("bridge i");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(2));
        clientPair.hardwareClient.send("bridge 1 auth_tone");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(3));
        clientPair.hardwareClient.send("bridge 1");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(4));
        clientPair.hardwareClient.send("bridge 1");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(5));
    }

    @Test
    public void testSeveralBridgeInitOk() throws Exception {
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.send(("bridge 2 i " + (clientPair.token)));
        clientPair.hardwareClient.send(("bridge 3 i " + (clientPair.token)));
        clientPair.hardwareClient.send(("bridge 4 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(3));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(4));
        clientPair.hardwareClient.send(("bridge 5 i " + (clientPair.token)));
        clientPair.hardwareClient.send(("bridge 5 i " + (clientPair.token)));
        clientPair.hardwareClient.send(("bridge 5 i " + (clientPair.token)));
        clientPair.hardwareClient.send(("bridge 5 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(5));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(6));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(7));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(8));
    }

    @Test
    public void testBridgeInitAndOk() throws Exception {
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testBridgeWithoutInit() throws Exception {
        clientPair.hardwareClient.send("bridge 1 aw 10 10");
        clientPair.hardwareClient.verifyResult(TestUtil.notAllowed(1));
    }

    @Test
    public void testBridgeInitAndSendNoOtherDevices() throws Exception {
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 10 10");
        clientPair.hardwareClient.verifyResult(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(2, DEVICE_NOT_IN_NETWORK));
    }

    @Test
    public void testBridgeInitAndSendOtherDevicesButNoBridgeDevices() throws Exception {
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.reset();
        // creating 1 new hard client
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(clientPair.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + (device.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 10 10");
        clientPair.hardwareClient.verifyResult(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(2, DEVICE_NOT_IN_NETWORK));
    }

    @Test
    public void testSecondTokenNotInitialized() throws Exception {
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 2 aw 10 10");
        clientPair.hardwareClient.verifyResult(TestUtil.notAllowed(2));
    }

    @Test
    public void testCorrectWorkflow2HardsSameToken() throws Exception {
        // creating 1 new hard client
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(clientPair.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 10 10");
        hardClient1.verifyResult(TestUtil.bridge(2, "aw 10 10"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "1-0 aw 10 10"));
    }

    @Test
    public void testWrongPinForBridge() throws Exception {
        // creating 1 new hard client
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(clientPair.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + (clientPair.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 vw 256 10");
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(2));
    }

    @Test
    public void testCorrectWorkflow2HardsDifferentToken() throws Exception {
        clientPair.appClient.createDevice(2, new Device(4, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token = device.token;
        clientPair.appClient.activate(2);
        clientPair.appClient.verifyResult(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(2, DEVICE_NOT_IN_NETWORK));
        clientPair.appClient.reset();
        // creating 1 new hard client
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + token));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 11 11");
        hardClient1.verifyResult(TestUtil.bridge(2, "aw 11 11"));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, ("2-" + (device.id))));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, (("2-" + (device.id)) + " aw 11 11")));
    }

    @Test
    public void testCorrectWorkflow3HardsDifferentToken() throws Exception {
        clientPair.appClient.createDevice(2, new Device(4, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token = device.token;
        clientPair.appClient.reset();
        // creating 2 new hard clients
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        TestHardClient hardClient2 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + token));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 11 11");
        hardClient1.verifyResult(TestUtil.bridge(2, "aw 11 11"));
        hardClient2.verifyResult(TestUtil.bridge(2, "aw 11 11"));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, ("2-" + (device.id))), 2);
        clientPair.appClient.never(TestUtil.hardware(2, "2 aw 11 11"));
    }

    @Test
    public void testCorrectWorkflow4HardsDifferentToken() throws Exception {
        clientPair.appClient.createDevice(2, new Device(4, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token2 = device.token;
        clientPair.appClient.createDevice(3, new Device(5, "123", BoardType.ESP8266));
        device = clientPair.appClient.parseDevice(2);
        String token3 = device.token;
        // creating 2 new hard clients
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(token2);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        TestHardClient hardClient2 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(token2);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.reset();
        TestHardClient hardClient3 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient3.login(token3);
        hardClient3.verifyResult(TestUtil.ok(1));
        hardClient3.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + token2));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send(("bridge 2 i " + token3));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 11 11");
        hardClient1.verifyResult(TestUtil.bridge(3, "aw 11 11"));
        hardClient2.verifyResult(TestUtil.bridge(3, "aw 11 11"));
        clientPair.hardwareClient.send("bridge 2 aw 13 13");
        hardClient3.verifyResult(TestUtil.bridge(4, "aw 13 13"));
    }

    @Test
    public void testCorrectWorkflow3HardsDifferentTokenAndSync() throws Exception {
        clientPair.appClient.createDevice(2, new Device(4, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token = device.token;
        clientPair.appClient.reset();
        // creating 2 new hard clients
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        TestHardClient hardClient2 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + token));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 aw 11 11");
        hardClient1.verifyResult(TestUtil.bridge(2, "aw 11 11"));
        hardClient2.verifyResult(TestUtil.bridge(2, "aw 11 11"));
        clientPair.appClient.verifyResult(produce(1, HARDWARE_CONNECTED, ("2-" + (device.id))), 2);
        clientPair.appClient.never(TestUtil.hardware(2, "2 aw 11 11"));
        hardClient1.sync(ANALOG, 11);
        hardClient1.verifyResult(TestUtil.hardware(1, "aw 11 11"));
        hardClient2.sync(ANALOG, 11);
        hardClient2.verifyResult(TestUtil.hardware(1, "aw 11 11"));
    }

    @Test
    public void testCorrectWorkflow4HardsDifferentTokenAndSync() throws Exception {
        clientPair.appClient.createDevice(2, new Device(4, "123", BoardType.ESP8266));
        Device device = clientPair.appClient.parseDevice();
        String token2 = device.token;
        clientPair.appClient.createDevice(3, new Device(5, "123", BoardType.ESP8266));
        device = clientPair.appClient.parseDevice(2);
        String token3 = device.token;
        // creating 2 new hard clients
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(token2);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        TestHardClient hardClient2 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(token2);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.reset();
        TestHardClient hardClient3 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient3.login(token3);
        hardClient3.verifyResult(TestUtil.ok(1));
        hardClient3.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + token2));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send(("bridge 2 i " + token3));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 vw 11 12");
        hardClient1.verifyResult(TestUtil.bridge(3, "vw 11 12"));
        hardClient2.verifyResult(TestUtil.bridge(3, "vw 11 12"));
        clientPair.hardwareClient.send("bridge 2 aw 13 13");
        hardClient3.verifyResult(TestUtil.bridge(4, "aw 13 13"));
        hardClient1.sync(VIRTUAL, 11);
        hardClient1.verifyResult(TestUtil.hardware(1, "vw 11 12"));
        hardClient2.sync(VIRTUAL, 11);
        hardClient2.verifyResult(TestUtil.hardware(1, "vw 11 12"));
        hardClient3.sync(ANALOG, 13);
        hardClient3.verifyResult(TestUtil.hardware(1, "aw 13 13"));
        hardClient3.sync(ANALOG, 13);
        hardClient3.never(TestUtil.hardware(2, "aw 13 13"));
    }

    @Test
    public void bridgeOnlyWorksWithinOneAccount() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register("test@test.com", "1", BLYNK);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.login("test@test.com", "1", "Android", "RC13");
        appClient.verifyResult(TestUtil.ok(2));
        appClient.createDash("{\"id\":1, \"createdAt\":1, \"name\":\"test board\"}");
        appClient.verifyResult(TestUtil.ok(3));
        appClient.reset();
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        device1.status = Status.OFFLINE;
        appClient.createDevice(1, device1);
        Device device = appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        appClient.verifyResult(TestUtil.createDevice(1, device));
        appClient.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + (device.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.notAllowed(1));
    }

    @Test
    public void testCorrectWorkflow2HardsOnDifferentProjects() throws Exception {
        DashBoard dash = new DashBoard();
        dash.id = 5;
        dash.name = "test";
        dash.activate();
        clientPair.appClient.createDash(dash);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Device device = new Device(1, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(dash.id, device);
        device = clientPair.appClient.parseDevice(2);
        // creating 1 new hard client
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(device.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        clientPair.hardwareClient.send(("bridge 1 i " + (device.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 vw 11 11");
        hardClient1.verifyResult(TestUtil.bridge(2, "vw 11 11"));
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "5-1"));
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "5-1 vw 11 11"));
        clientPair.appClient.never(TestUtil.hardware(2, "5-0 vw 11 11"));
        clientPair.appClient.never(TestUtil.hardware(2, "1-0 vw 11 11"));
    }

    @Test
    public void testCorrectWorkflow3HardsOnDifferentProjectsSameId() throws Exception {
        DashBoard dash = new DashBoard();
        dash.id = 5;
        dash.name = "test";
        dash.activate();
        clientPair.appClient.createDash(dash);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        Device device = new Device(0, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(dash.id, device);
        device = clientPair.appClient.parseDevice(2);
        TestHardClient hardClient1 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient1.login(device.token);
        hardClient1.verifyResult(TestUtil.ok(1));
        hardClient1.reset();
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "5-0"));
        clientPair.appClient.reset();
        dash.id = 6;
        clientPair.appClient.createDash(dash);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        device = new Device(0, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(dash.id, device);
        device = clientPair.appClient.parseDevice(2);
        TestHardClient hardClient2 = new TestHardClient("localhost", BridgeWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.reset();
        clientPair.appClient.verifyResult(TestUtil.hardwareConnected(1, "6-0"));
        clientPair.hardwareClient.send(("bridge 1 i " + (device.token)));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("bridge 1 vw 11 11");
        clientPair.appClient.verifyResult(TestUtil.hardware(2, "6-0 vw 11 11"));
        hardClient2.verifyResult(TestUtil.bridge(2, "vw 11 11"));
        hardClient1.never(TestUtil.bridge(2, "vw 11 11"));
        clientPair.appClient.never(TestUtil.hardware(2, "5-0 vw 11 11"));
        clientPair.appClient.never(TestUtil.hardware(2, "1-0 vw 11 11"));
    }
}

