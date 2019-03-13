package cc.blynk.integration.http;


import cc.blynk.integration.BaseTest;
import cc.blynk.server.Holder;
import cc.blynk.server.SslContextHolder;
import cc.blynk.server.servers.BaseServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 07.01.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcmeTest extends BaseTest {
    private BaseServer httpServer;

    private Holder holder2;

    @Test
    public void testCorrectContext() {
        SslContextHolder sslContextHolder = holder2.sslContextHolder;
        Assert.assertNotNull(sslContextHolder);
        Assert.assertTrue(sslContextHolder.runRenewalWorker());
        Assert.assertTrue(sslContextHolder.isNeedInitializeOnStart);
        Assert.assertNotNull(sslContextHolder.acmeClient);
    }
}

