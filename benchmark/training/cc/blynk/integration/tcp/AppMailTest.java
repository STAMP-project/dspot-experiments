package cc.blynk.integration.tcp;


import cc.blynk.integration.CounterBase;
import cc.blynk.integration.Holder;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
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
public class AppMailTest extends SingleServerInstancePerTest {
    @Test
    public void testSendEmail() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("email 1");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(1000)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("Auth Token for My Dashboard project and device My Device"), ArgumentMatchers.startsWith("Auth Token : "));
    }

    @Test
    public void testSendEmailForDevice() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.send("email 1 0");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(1000)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("Auth Token for My Dashboard project and device My Device"), ArgumentMatchers.startsWith("Auth Token : "));
    }

    @Test
    public void testSendEmailForSingleDevice() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1");
        appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices();
        Assert.assertEquals(1, devices.length);
        appClient.send("email 1");
        String expectedBody = String.format(("Auth Token : %s\n" + (((((((((((("\n" + "Happy Blynking!\n") + "-\n") + "Getting Started Guide -> https://www.blynk.cc/getting-started\n") + "Documentation -> http://docs.blynk.cc/\n") + "Sketch generator -> https://examples.blynk.cc/\n") + "\n") + "Latest Blynk library -> https://github.com/blynkkk/blynk-library/releases/download/v0.6.1/Blynk_Release_v0.6.1.zip\n") + "Latest Blynk server -> https://github.com/blynkkk/blynk-server/releases/download/v0.41.3/server-0.41.3.jar\n") + "-\n") + "https://www.blynk.cc\n") + "twitter.com/blynk_app\n") + "www.facebook.com/blynkapp\n")), devices[0].token);
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(1000)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("Auth Token for My Dashboard project and device My Device"), ArgumentMatchers.eq(expectedBody));
    }

    @Test
    public void testSendEmailForMultiDevices() throws Exception {
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.login(CounterBase.getUserName(), "1");
        appClient.verifyResult(TestUtil.ok(1));
        Device device1 = new Device(1, "My Device2", BoardType.ESP8266);
        clientPair.appClient.createDevice(1, device1);
        Device device = clientPair.appClient.parseDevice();
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        clientPair.appClient.verifyResult(TestUtil.createDevice(1, device));
        clientPair.appClient.send("getDevices 1");
        Device[] devices = clientPair.appClient.parseDevices(2);
        appClient.send("email 1");
        String expectedBody = String.format(("Auth Token for device \'My Device\' : %s\n" + ((((((((((((("Auth Token for device \'My Device2\' : %s\n" + "\n") + "Happy Blynking!\n") + "-\n") + "Getting Started Guide -> https://www.blynk.cc/getting-started\n") + "Documentation -> http://docs.blynk.cc/\n") + "Sketch generator -> https://examples.blynk.cc/\n") + "\n") + "Latest Blynk library -> https://github.com/blynkkk/blynk-library/releases/download/v0.6.1/Blynk_Release_v0.6.1.zip\n") + "Latest Blynk server -> https://github.com/blynkkk/blynk-server/releases/download/v0.41.3/server-0.41.3.jar\n") + "-\n") + "https://www.blynk.cc\n") + "twitter.com/blynk_app\n") + "www.facebook.com/blynkapp\n")), devices[0].token, devices[1].token);
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(1000)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("Auth Tokens for My Dashboard project and 2 devices"), ArgumentMatchers.eq(expectedBody));
    }

    @Test
    public void testEmailMininalValidation() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email to subj body");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.after(500).never()).sendHtml(ArgumentMatchers.eq("to"), ArgumentMatchers.eq("subj"), ArgumentMatchers.eq("body"));
        clientPair.hardwareClient.verifyResult(TestUtil.illegalCommand(1));
    }

    @Test
    public void testEmailWorks() throws Exception {
        // no email widget
        clientPair.hardwareClient.send("email to subj body");
        clientPair.hardwareClient.verifyResult(TestUtil.notAllowed(1));
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email to@to.com subj body");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendHtml(ArgumentMatchers.eq("to@to.com"), ArgumentMatchers.eq("subj"), ArgumentMatchers.eq("body"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(2));
        clientPair.hardwareClient.send("email to@to.com subj body");
        clientPair.hardwareClient.verifyResult(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(3, QUOTA_LIMIT));
    }

    @Test
    public void testPlainTextIsAllowed() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"contentType\":\"TEXT_PLAIN\", \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email to@to.com subj body");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendText(ArgumentMatchers.eq("to@to.com"), ArgumentMatchers.eq("subj"), ArgumentMatchers.eq("body"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testPlaceholderForDeviceNameWorks() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"contentType\":\"TEXT_PLAIN\", \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email to@to.com SUBJ_{DEVICE_NAME} BODY_{DEVICE_NAME}");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendText(ArgumentMatchers.eq("to@to.com"), ArgumentMatchers.eq("SUBJ_My Device"), ArgumentMatchers.eq("BODY_My Device"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testPlaceholderForVendorWorks() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"contentType\":\"TEXT_PLAIN\", \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email {VENDOR_EMAIL} SUBJ_{VENDOR_EMAIL} BODY_{VENDOR_EMAIL}");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendText(ArgumentMatchers.eq("vendor@blynk.cc"), ArgumentMatchers.eq("SUBJ_vendor@blynk.cc"), ArgumentMatchers.eq("BODY_vendor@blynk.cc"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testPlaceholderForDeviceOwnerWorks() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"contentType\":\"TEXT_PLAIN\", \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email {DEVICE_OWNER_EMAIL} SUBJ_{DEVICE_OWNER_EMAIL} BODY_{DEVICE_OWNER_EMAIL}");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendText(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq(("SUBJ_" + (CounterBase.getUserName()))), ArgumentMatchers.eq(("BODY_" + (CounterBase.getUserName()))));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testEmailWorkWithEmailFromApp() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"to\":\"test@mail.ua\", \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email subj body");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendHtml(ArgumentMatchers.eq("test@mail.ua"), ArgumentMatchers.eq("subj"), ArgumentMatchers.eq("body"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testEmailFromAppOverridesEmailFromHardware() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"to\":\"test@mail.ua\", \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email to@to.com subj body");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendHtml(ArgumentMatchers.eq("test@mail.ua"), ArgumentMatchers.eq("subj"), ArgumentMatchers.eq("body"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }

    @Test
    public void testEmailWorkWithNoEmailInApp() throws Exception {
        // adding email widget
        clientPair.appClient.createWidget(1, "{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"width\":1, \"height\":1, \"type\":\"EMAIL\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("email subj body");
        Mockito.verify(AppMailTest.holder.mailWrapper, Mockito.timeout(500)).sendHtml(ArgumentMatchers.eq(CounterBase.getUserName()), ArgumentMatchers.eq("subj"), ArgumentMatchers.eq("body"));
        clientPair.hardwareClient.verifyResult(TestUtil.ok(1));
    }
}

