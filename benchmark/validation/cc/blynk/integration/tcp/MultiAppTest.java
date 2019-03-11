package cc.blynk.integration.tcp;


import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.BaseTestHardwareClient;
import cc.blynk.integration.model.tcp.TestAppClient;
import cc.blynk.integration.model.tcp.TestHardClient;
import cc.blynk.server.core.protocol.model.messages.common.HardwareMessage;
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
public class MultiAppTest extends SingleServerInstancePerTest {
    @Test
    public void testCreateFewAccountWithDifferentApp() throws Exception {
        TestAppClient appClient1 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        TestAppClient appClient2 = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        String token1 = workflowForUser(appClient1, "test@blynk.cc", "a", "testapp1");
        String token2 = workflowForUser(appClient2, "test@blynk.cc", "a", "testapp2");
        appClient1.reset();
        appClient2.reset();
        TestHardClient hardClient1 = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        TestHardClient hardClient2 = new TestHardClient("localhost", SingleServerInstancePerTest.properties.getHttpPort());
        start();
        hardClient1.login(token1);
        Mockito.verify(hardClient1.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(appClient1.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardwareConnected(1, "1-0")));
        hardClient2.login(token2);
        Mockito.verify(hardClient2.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(1)));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.hardwareConnected(1, "1-0")));
        hardClient1.send("hardware vw 1 100");
        Mockito.verify(appClient1.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(2, TestUtil.b("1-0 vw 1 100"))));
        Mockito.verify(appClient2.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 1 100"))));
        appClient1.reset();
        appClient2.reset();
        hardClient2.send("hardware vw 1 100");
        Mockito.verify(appClient2.responseMock, Mockito.timeout(2000)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(2, TestUtil.b("1-0 vw 1 100"))));
        Mockito.verify(appClient1.responseMock, Mockito.timeout(500).times(0)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new HardwareMessage(1, TestUtil.b("1-0 vw 1 100"))));
    }
}

