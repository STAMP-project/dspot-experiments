package cc.blynk.integration.tcp;


import cc.blynk.integration.CounterBase;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.Profile;
import cc.blynk.server.core.model.device.HardwareInfo;
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
public class BlynkInternalTest extends SingleServerInstancePerTest {
    @Test
    public void testGetRTC() throws Exception {
        clientPair.appClient.createWidget(1, ("{\"type\":\"RTC\",\"id\":99, \"pin\":99, \"pinType\":\"VIRTUAL\", " + "\"x\":0,\"y\":0,\"width\":0,\"height\":0}"));
        clientPair.hardwareClient.send("internal rtc");
        String rtcResponse = clientPair.hardwareClient.getBody();
        Assert.assertNotNull(rtcResponse);
        String rtcTime = rtcResponse.split("\u0000")[1];
        Assert.assertNotNull(rtcTime);
        Assert.assertEquals(10, rtcTime.length());
        Assert.assertEquals(System.currentTimeMillis(), ((Long.parseLong(rtcTime)) * 1000), 10000L);
    }

    @Test
    public void testHardwareLoginWithInfo() throws Exception {
        TestHardClient hardClient2 = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        hardClient2.login(clientPair.token);
        hardClient2.verifyResult(TestUtil.ok(1));
        hardClient2.send(("internal " + (TestUtil.b("ver 0.3.1 fw 3.3.3 h-beat 10 buff-in 256 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"))));
        hardClient2.verifyResult(TestUtil.ok(2));
        clientPair.appClient.reset();
        HardwareInfo hardwareInfo = new HardwareInfo("3.3.3", "0.3.1", "Arduino", "ATmega328P", "W5100", null, "tmpl00123", 10, 256);
        clientPair.appClient.send("loadProfileGzipped");
        Profile profile = clientPair.appClient.parseProfile(1);
        Assert.assertEquals(JsonParser.toJson(hardwareInfo), JsonParser.toJson(profile.dashBoards[0].devices[0].hardwareInfo));
        stop().awaitUninterruptibly();
    }

    @Test
    public void appConnectedEvent() throws Exception {
        clientPair.appClient.updateDash("{\"id\":1, \"name\":\"test board\", \"isAppConnectedOn\":true}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.13.3");
        appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.verifyResult(TestUtil.internal(7777, "acon"));
    }

    @Test
    public void appDisconnectedEvent() throws Exception {
        clientPair.appClient.updateDash("{\"id\":1, \"name\":\"test board\", \"isAppConnectedOn\":true}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        stop().await();
        clientPair.hardwareClient.verifyResult(TestUtil.internal(7777, "adis"));
    }

    @Test
    public void testBuffInIsHandled() throws Exception {
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 12 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("hardware 1-0 vw 1 12");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(1, "vw 1 12"));
        clientPair.appClient.send("hardware 1-0 vw 1 123");
        clientPair.hardwareClient.never(TestUtil.hardware(2, "vw 1 123"));
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(2));
        clientPair.appClient.send("hardware 1-0 vw 1 12");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(3, "vw 1 12"));
        clientPair.hardwareClient.send(("internal " + (TestUtil.b("ver 0.3.1 h-beat 10 buff-in 0 dev Arduino cpu ATmega328P con W5100 tmpl tmpl00123"))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(3));
        clientPair.appClient.send("hardware 1-0 vw 1 12");
        clientPair.hardwareClient.verifyResult(TestUtil.hardware(4, "vw 1 12"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 245; i++) {
            sb.append("a");
        }
        String s = sb.toString();
        clientPair.appClient.send(("hardware 1-0 vw 1 " + s));
        clientPair.hardwareClient.never(TestUtil.hardware(5, ("vw 1 " + s)));
    }
}

