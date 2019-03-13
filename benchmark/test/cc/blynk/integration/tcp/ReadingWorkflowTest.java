package cc.blynk.integration.tcp;


import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
public class ReadingWorkflowTest extends SingleServerInstancePerTest {
    private static int tcpHardPort;

    private ScheduledExecutorService ses;

    @Test
    public void testReadingCommandNotAcceptedAnymoreFromApp() throws Exception {
        clientPair.appClient.send("hardware 1 ar 7");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testServerSendReadingCommandWithReadingWorkerEnabled() throws Exception {
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 1000, TimeUnit.MILLISECONDS);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("ar 7"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("ar 30"))));
    }

    @Test
    public void testServerSendReadingCommandCorrectly() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":155, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 500, TimeUnit.MILLISECONDS);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(600).times(2)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
    }

    @Test
    public void testServerDontSendReadingCommandsForNonActiveDash() throws Exception {
        clientPair.appClient.createWidget(1, "{\"id\":155, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.deactivate(1);
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 500, TimeUnit.MILLISECONDS);
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testSendReadCommandsForLCD() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"LCD\",\"id\":1923810267,\"x\":0,\"y\":6,\"color\":600084223,\"width\":8,\"height\":2,\"tabId\":0,\"" + ((("pins\":[" + "{\"pin\":100,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023},") + "{\"pin\":101,\"pinType\":\"VIRTUAL\",\"pwmMode\":false,\"rangeMappingOn\":false,\"min\":0,\"max\":1023}],") + "\"advancedMode\":false,\"textLight\":false,\"textLightOn\":false,\"frequency\":900}")));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 1000, TimeUnit.MILLISECONDS);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
        clientPair.hardwareClient.reset();
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
    }

    @Test
    public void testSendReadForMultipleDevices() throws Exception {
        Device device2 = new Device(2, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device2);
        device2 = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device2);
        Assert.assertNotNull(device2.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(1, device2)));
        TestHardClient hardClient2 = new TestHardClient("localhost", ReadingWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device2.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.createWidget(1, "{\"id\":155, \"deviceId\":0, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"id\":156, \"deviceId\":2, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":101}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 500, TimeUnit.MILLISECONDS);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(750)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(750)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
    }

    @Test
    public void testSendReadForDeviceSelector() throws Exception {
        Device device2 = new Device(2, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device2);
        device2 = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device2);
        Assert.assertNotNull(device2.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(1, device2)));
        TestHardClient hardClient2 = new TestHardClient("localhost", ReadingWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device2.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.createWidget(1, "{\"id\":200000, \"width\":1, \"value\":2, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"DEVICE_SELECTOR\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"id\":155, \"deviceId\":200000, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 500, TimeUnit.MILLISECONDS);
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(100).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        clientPair.hardwareClient.reset();
        hardClient2.reset();
        clientPair.appClient.send("hardware 1 vu 200000 0");
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(hardClient2.responseMock, Mockito.after(100).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
    }

    @Test
    public void testSendReadForMultipleDevices2() throws Exception {
        Device device2 = new Device(2, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device2);
        device2 = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device2);
        Assert.assertNotNull(device2.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(1, device2)));
        TestHardClient hardClient2 = new TestHardClient("localhost", ReadingWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device2.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.createWidget(1, "{\"id\":155, \"deviceId\":0, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.createWidget(1, "{\"id\":156, \"deviceId\":0, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":101}");
        clientPair.appClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.createWidget(1, "{\"id\":157, \"deviceId\":2, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":102}");
        clientPair.appClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.createWidget(1, "{\"id\":158, \"deviceId\":2, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":103}");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 500, TimeUnit.MILLISECONDS);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 102"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 103"))));
        hardClient2.reset();
        clientPair.hardwareClient.reset();
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 102"))));
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 103"))));
        clientPair.appClient.deactivate(1);
        hardClient2.reset();
        clientPair.hardwareClient.reset();
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
        Mockito.verify(hardClient2.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 102"))));
        Mockito.verify(hardClient2.responseMock, Mockito.after(500).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 103"))));
    }

    @Test
    public void testSendReadOnlyForOnlineApp() throws Exception {
        Device device2 = new Device(2, "My Device", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device2);
        device2 = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device2);
        Assert.assertNotNull(device2.token);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.createDevice(1, device2)));
        TestHardClient hardClient2 = new TestHardClient("localhost", ReadingWorkflowTest.tcpHardPort);
        start();
        hardClient2.login(device2.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        clientPair.appClient.reset();
        clientPair.appClient.createWidget(1, "{\"id\":155, \"deviceId\":0, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":100}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        clientPair.appClient.createWidget(1, "{\"id\":156, \"deviceId\":2, \"frequency\":400, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"label\":\"Some Text\", \"type\":\"GAUGE\", \"pinType\":\"VIRTUAL\", \"pin\":101}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(1000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(2)));
        stop().await();
        ses.scheduleAtFixedRate(ReadingWorkflowTest.holder.readingWidgetsWorker, 0, 500, TimeUnit.MILLISECONDS);
        Mockito.verify(clientPair.hardwareClient.responseMock, Mockito.after(1000).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 100"))));
        Mockito.verify(hardClient2.responseMock, Mockito.after(1000).never()).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(READING_MSG_ID, HARDWARE, TestUtil.b("vr 101"))));
    }
}

