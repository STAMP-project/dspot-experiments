package de.zalando.ep.zalenium.registry;


import BrowserType.CHROME;
import BrowserType.SAFARI;
import CapabilityType.BROWSER_NAME;
import CapabilityType.PLATFORM_NAME;
import Duration.FIVE_HUNDRED_MILLISECONDS;
import Duration.TWO_SECONDS;
import Platform.LINUX;
import Platform.MAC;
import Platform.WIN8;
import SessionTerminationReason.CLIENT_STOPPED_SESSION;
import de.zalando.ep.zalenium.proxy.AutoStartProxySet;
import de.zalando.ep.zalenium.proxy.DockerSeleniumRemoteProxy;
import de.zalando.ep.zalenium.proxy.DockeredSeleniumStarter;
import de.zalando.ep.zalenium.util.TestUtils;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.ProxySet;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.web.servlet.handler.RequestHandler;


@SuppressWarnings("Duplicates")
public class ZaleniumRegistryTest {
    @Test
    public void proxyIsAdded() throws Exception {
        GridRegistry registry = ZaleniumRegistry.newInstance(new org.openqa.grid.web.Hub(new GridHubConfiguration()), new ProxySet(false));
        DockerSeleniumRemoteProxy p1 = TestUtils.getNewBasicRemoteProxy("app1", "http://machine1:4444/", registry);
        DockerSeleniumRemoteProxy p2 = TestUtils.getNewBasicRemoteProxy("app1", "http://machine2:4444/", registry);
        DockerSeleniumRemoteProxy p3 = TestUtils.getNewBasicRemoteProxy("app1", "http://machine3:4444/", registry);
        DockerSeleniumRemoteProxy p4 = TestUtils.getNewBasicRemoteProxy("app1", "http://machine4:4444/", registry);
        try {
            registry.add(p1);
            registry.add(p2);
            registry.add(p3);
            registry.add(p4);
            Assert.assertEquals(4, registry.getAllProxies().size());
        } finally {
            registry.stop();
        }
    }

    @Test
    public void proxyIsRemoved() throws Exception {
        GridRegistry registry = ZaleniumRegistry.newInstance(new org.openqa.grid.web.Hub(new GridHubConfiguration()), new ProxySet(false));
        DockerSeleniumRemoteProxy p1 = TestUtils.getNewBasicRemoteProxy("app1", "http://machine1:4444/", registry);
        try {
            registry.add(p1);
            Assert.assertEquals(1, registry.getAllProxies().size());
            registry.removeIfPresent(p1);
            Assert.assertEquals(0, registry.getAllProxies().size());
        } finally {
            registry.stop();
        }
    }

    @Test
    public void sessionIsProcessed() {
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, CHROME);
        requestedCapability.put(PLATFORM_NAME, LINUX);
        GridRegistry registry = ZaleniumRegistry.newInstance(new org.openqa.grid.web.Hub(new GridHubConfiguration()), new ProxySet(false));
        RegistrationRequest req = TestUtils.getRegistrationRequestForTesting(40000, DockerSeleniumRemoteProxy.class.getCanonicalName());
        req.getConfiguration().capabilities.clear();
        req.getConfiguration().capabilities.addAll(TestUtils.getDockerSeleniumCapabilitiesForTesting());
        DockerSeleniumRemoteProxy p1 = new DockerSeleniumRemoteProxy(req, registry);
        try {
            registry.add(p1);
            await().pollInterval(FIVE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(() -> (registry.getAllProxies().size()) == 1);
            RequestHandler newSessionRequest = TestUtils.createNewSessionHandler(registry, requestedCapability);
            newSessionRequest.process();
            TestSession session = newSessionRequest.getSession();
            session.setExternalKey(new ExternalSessionKey(UUID.randomUUID().toString()));
            registry.terminate(session, CLIENT_STOPPED_SESSION);
            Callable<Boolean> callable = () -> (registry.getActiveSessions().size()) == 0;
            await().pollInterval(FIVE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(callable);
        } finally {
            registry.stop();
        }
    }

    @Test(expected = GridException.class)
    public void requestIsRejectedWhenCapabilitiesAreNotSupported() {
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, SAFARI);
        requestedCapability.put(PLATFORM_NAME, MAC);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(10000), ZoneId.systemDefault());
        DockeredSeleniumStarter starter = Mockito.mock(DockeredSeleniumStarter.class);
        AutoStartProxySet autoStartProxySet = new AutoStartProxySet(true, 0, 5, 1000, false, starter, clock, 30, 30000);
        GridRegistry registry = ZaleniumRegistry.newInstance(new org.openqa.grid.web.Hub(new GridHubConfiguration()), autoStartProxySet);
        try {
            RequestHandler newSessionRequest = TestUtils.createNewSessionHandler(registry, requestedCapability);
            newSessionRequest.process();
            Assert.fail("These capabilities should be rejected by the Grid.");
        } finally {
            registry.stop();
        }
    }

    @Test(expected = GridException.class)
    public void requestIsRejectedWhenPlatformIsNotSupported() {
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, CHROME);
        requestedCapability.put(PLATFORM_NAME, WIN8);
        Clock clock = Clock.fixed(Instant.ofEpochMilli(10000), ZoneId.systemDefault());
        DockeredSeleniumStarter starter = Mockito.mock(DockeredSeleniumStarter.class);
        AutoStartProxySet autoStartProxySet = new AutoStartProxySet(true, 0, 5, 1000, false, starter, clock, 30, 30000);
        GridRegistry registry = ZaleniumRegistry.newInstance(new org.openqa.grid.web.Hub(new GridHubConfiguration()), autoStartProxySet);
        try {
            RequestHandler newSessionRequest = TestUtils.createNewSessionHandler(registry, requestedCapability);
            newSessionRequest.process();
            Assert.fail("These capabilities should be rejected by the Grid because the platform is not supported.");
        } finally {
            registry.stop();
        }
    }
}

