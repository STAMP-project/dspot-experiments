package cc.blynk.integration.tcp;


import AppNameUtil.BLYNK;
import cc.blynk.integration.CounterBase;
import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.TestAppClient;
import org.junit.Test;


public class RegistrationLimitCheckTest extends SingleServerInstancePerTest {
    @Test
    public void registrationLimitCheck() throws Exception {
        for (int i = 0; i < 100; i++) {
            TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
            start();
            appClient.register(CounterBase.incrementAndGetUserName(), "1", BLYNK);
            appClient.verifyResult(TestUtil.ok(1));
            stop();
        }
        TestAppClient appClient = new TestAppClient(SingleServerInstancePerTest.properties);
        start();
        appClient.register(CounterBase.incrementAndGetUserName(), "1", BLYNK);
        appClient.verifyResult(TestUtil.notAllowed(1));
    }
}

