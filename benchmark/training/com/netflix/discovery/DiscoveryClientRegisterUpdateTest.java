package com.netflix.discovery;


import InstanceInfo.InstanceStatus.DOWN;
import InstanceInfo.InstanceStatus.UNKNOWN;
import InstanceInfo.InstanceStatus.UP;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.ConfigurationManager;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author David Liu
 */
public class DiscoveryClientRegisterUpdateTest {
    private DiscoveryClientRegisterUpdateTest.TestApplicationInfoManager applicationInfoManager;

    private MockRemoteEurekaServer mockLocalEurekaServer;

    private DiscoveryClientRegisterUpdateTest.TestClient client;

    @Test
    public void registerUpdateLifecycleTest() throws Exception {
        applicationInfoManager.setInstanceStatus(UP);
        // give some execution time
        expectStatus(UP, 5, TimeUnit.SECONDS);
        applicationInfoManager.setInstanceStatus(UNKNOWN);
        // give some execution time
        expectStatus(UNKNOWN, 5, TimeUnit.SECONDS);
        applicationInfoManager.setInstanceStatus(DOWN);
        // give some execution time
        expectStatus(DOWN, 5, TimeUnit.SECONDS);
        Assert.assertTrue(((mockLocalEurekaServer.registerCount.get()) >= 3));// at least 3

    }

    /**
     * This test is similar to the normal lifecycle test, but don't sleep between calls of setInstanceStatus
     */
    @Test
    public void registerUpdateQuickLifecycleTest() throws Exception {
        applicationInfoManager.setInstanceStatus(UP);
        applicationInfoManager.setInstanceStatus(UNKNOWN);
        applicationInfoManager.setInstanceStatus(DOWN);
        expectStatus(DOWN, 5, TimeUnit.SECONDS);
        // this call will be rate limited, but will be transmitted by the automatic update after 10s
        applicationInfoManager.setInstanceStatus(UP);
        expectStatus(UP, 5, TimeUnit.SECONDS);
        Assert.assertTrue(((mockLocalEurekaServer.registerCount.get()) >= 2));// at least 2

    }

    @Test
    public void registerUpdateShutdownTest() throws Exception {
        Assert.assertEquals(1, applicationInfoManager.getStatusChangeListeners().size());
        shutdown();
        Assert.assertEquals(0, applicationInfoManager.getStatusChangeListeners().size());
        Mockito.verify(client, Mockito.times(1)).unregister();
    }

    @Test
    public void testRegistrationDisabled() throws Exception {
        shutdown();// shutdown the default @Before client first

        ConfigurationManager.getConfigInstance().setProperty("eureka.registration.enabled", "false");
        client = new DiscoveryClientRegisterUpdateTest.TestClient(applicationInfoManager, new DefaultEurekaClientConfig());
        Assert.assertEquals(0, applicationInfoManager.getStatusChangeListeners().size());
        applicationInfoManager.setInstanceStatus(DOWN);
        applicationInfoManager.setInstanceStatus(UP);
        Thread.sleep(400);
        shutdown();
        Assert.assertEquals(0, applicationInfoManager.getStatusChangeListeners().size());
    }

    @Test
    public void testDoNotUnregisterOnShutdown() throws Exception {
        shutdown();// shutdown the default @Before client first

        ConfigurationManager.getConfigInstance().setProperty("eureka.shouldUnregisterOnShutdown", "false");
        client = Mockito.spy(new DiscoveryClientRegisterUpdateTest.TestClient(applicationInfoManager, new DefaultEurekaClientConfig()));
        shutdown();
        Mockito.verify(client, Mockito.never()).unregister();
    }

    public class TestApplicationInfoManager extends ApplicationInfoManager {
        TestApplicationInfoManager(InstanceInfo instanceInfo) {
            super(new MyDataCenterInstanceConfig(), instanceInfo, null);
        }

        Map<String, StatusChangeListener> getStatusChangeListeners() {
            return this.listeners;
        }
    }

    private static class TestClient extends DiscoveryClient {
        public TestClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config) {
            super(applicationInfoManager, config);
        }

        @Override
        public void unregister() {
            super.unregister();
        }
    }
}

