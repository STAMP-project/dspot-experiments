package cc.blynk.integration.tcp;


import AppNameUtil.BLYNK;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.model.device.BoardType;
import cc.blynk.server.core.model.device.Device;
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
public class PortUnificationTest extends SingleServerInstancePerTest {
    @Test
    public void testAppConectsOk() throws Exception {
        int appPort = SingleServerInstancePerTest.properties.getHttpsPort();
        TestAppClient appClient = new TestAppClient("localhost", appPort, SingleServerInstancePerTest.properties);
        start();
        appClient.register(CounterBase.incrementAndGetUserName(), "1", BLYNK);
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.10.4");
        appClient.verifyResult(TestUtil.ok(1));
        appClient.verifyResult(TestUtil.ok(2));
    }

    @Test
    public void testHardwareConnectsOk() throws Exception {
        int appPort = SingleServerInstancePerTest.properties.getHttpsPort();
        TestAppClient appClient = new TestAppClient("localhost", appPort, SingleServerInstancePerTest.properties);
        start();
        appClient.register(CounterBase.incrementAndGetUserName(), "1", BLYNK);
        appClient.login(CounterBase.getUserName(), "1", "Android", "1.10.4");
        appClient.createDash("{\"id\":1, \"createdAt\":1, \"name\":\"test board\"}");
        Device device1 = new Device(1, "My Device", BoardType.ESP8266);
        appClient.createDevice(1, device1);
        appClient.verifyResult(TestUtil.ok(1));
        appClient.verifyResult(TestUtil.ok(2));
        appClient.verifyResult(TestUtil.ok(3));
        Device device = appClient.parseDevice(4);
        Assert.assertNotNull(device);
        Assert.assertNotNull(device.token);
        int hardwarePort = SingleServerInstancePerTest.properties.getHttpPort();
        TestHardClient hardClient = new TestHardClient("localhost", hardwarePort);
        start();
        hardClient.login(device.token);
        hardClient.verifyResult(TestUtil.ok(1));
    }
}

