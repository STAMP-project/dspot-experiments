package cc.blynk.integration.tcp;


import cc.blynk.integration.BaseTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.servers.BaseServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AppOfflineTest extends BaseTest {
    private BaseServer appServer;

    private BaseServer hardwareServer;

    private ClientPair clientPair;

    @Test
    public void testWarn() throws Exception {
        clientPair.appClient.updateDash("{\"id\":1, \"name\":\"test board\", \"isAppConnectedOn\":true}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        TestUtil.sleep(1500);
        clientPair.hardwareClient.verifyResult(TestUtil.internal(7777, "adis"));
    }
}

