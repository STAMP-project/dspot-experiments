package de.zalando.ep.zalenium.proxy;


import BrowserType.IE;
import CapabilityType.BROWSER_NAME;
import CapabilityType.PLATFORM_NAME;
import DockerSeleniumRemoteProxy.DEFAULT_MAX_TEST_IDLE_TIME_SECS;
import DockerSeleniumRemoteProxy.DEFAULT_VIDEO_RECORDING_ENABLED;
import DockerSeleniumRemoteProxy.DockerSeleniumContainerAction.START_RECORDING;
import DockerSeleniumRemoteProxy.DockerSeleniumContainerAction.STOP_RECORDING;
import DockerSeleniumRemoteProxy.ZALENIUM_MAX_TEST_SESSIONS;
import DockerSeleniumRemoteProxy.ZALENIUM_VIDEO_RECORDING_ENABLED;
import Duration.FIVE_HUNDRED_MILLISECONDS;
import Duration.FIVE_SECONDS;
import Duration.TWO_SECONDS;
import Platform.WIN10;
import RequestType.REGULAR;
import RequestType.START_SESSION;
import RequestType.STOP_SESSION;
import TestInformation.TestStatus.FAILED;
import TestInformation.TestStatus.SUCCESS;
import de.zalando.ep.zalenium.container.ContainerClient;
import de.zalando.ep.zalenium.container.ContainerFactory;
import de.zalando.ep.zalenium.container.DockerContainerClient;
import de.zalando.ep.zalenium.container.kubernetes.KubernetesContainerClient;
import de.zalando.ep.zalenium.dashboard.Dashboard;
import de.zalando.ep.zalenium.util.DockerSeleniumRemoteProxy;
import de.zalando.ep.zalenium.util.TestUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.web.servlet.handler.WebDriverRequest;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.server.jmx.JMXHelper;


@SuppressWarnings("Duplicates")
@RunWith(Parameterized.class)
public class DockerSeleniumRemoteProxyTest {
    private DockerSeleniumRemoteProxy proxy;

    private GridRegistry registry;

    private ContainerClient containerClient;

    private Supplier<DockerContainerClient> originalDockerContainerClient;

    private KubernetesContainerClient originalKubernetesContainerClient;

    private Supplier<Boolean> originalIsKubernetesValue;

    private Supplier<Boolean> currentIsKubernetesValue;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    public DockerSeleniumRemoteProxyTest(ContainerClient containerClient, Supplier<Boolean> isKubernetes) {
        this.containerClient = containerClient;
        this.currentIsKubernetesValue = isKubernetes;
        this.originalDockerContainerClient = ContainerFactory.getDockerContainerClient();
        this.originalIsKubernetesValue = ContainerFactory.getIsKubernetes();
        this.originalKubernetesContainerClient = ContainerFactory.getKubernetesContainerClient();
    }

    @Test
    public void dockerSeleniumOnlyRunsOneTestPerContainer() {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        // Not tests have been executed.
        Assert.assertEquals(0, proxy.getAmountOfExecutedTests());
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        // One test is/has been executed and the session amount limit was reached.
        Assert.assertEquals(1, proxy.getAmountOfExecutedTests());
        Assert.assertTrue(proxy.isTestSessionLimitReached());
    }

    @Test
    public void secondRequestGetsANullTestRequest() {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("name", "anyRandomTestName");
        requestedCapability.put("build", "anyRandomTestBuild");
        {
            TestSession newSession = proxy.getNewSession(requestedCapability);
            Assert.assertNotNull(newSession);
        }
        // Since only one test should be executed, the second request should come null
        {
            TestSession newSession = proxy.getNewSession(requestedCapability);
            Assert.assertNull(newSession);
        }
        Assert.assertEquals("anyRandomTestBuild", proxy.getTestBuild());
        Assert.assertEquals("anyRandomTestName", proxy.getTestName());
    }

    @Test
    public void sessionGetsCreatedEvenIfCapabilitiesAreNull() {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("name", null);
        requestedCapability.put("build", null);
        requestedCapability.put("version", null);
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        Assert.assertTrue(proxy.getTestBuild().isEmpty());
        Assert.assertEquals(newSession.getInternalKey(), proxy.getTestName());
    }

    @Test
    public void noSessionIsCreatedWhenCapabilitiesAreNotSupported() {
        // Non supported capabilities
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, IE);
        requestedCapability.put(PLATFORM_NAME, WIN10);
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNull(newSession);
    }

    @Test
    public void noSessionIsCreatedWithSpecialScreenSize() {
        // Non supported capabilities
        Dimension customScreenSize = new Dimension(1280, 760);
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        String screenResolution = String.format("%sx%s", customScreenSize.getWidth(), customScreenSize.getHeight());
        requestedCapability.put("screenResolution", screenResolution);
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNull(newSession);
    }

    @Test
    public void noSessionIsCreatedWithSpecialTimeZone() {
        // Non supported capabilities
        TimeZone timeZone = TimeZone.getTimeZone("America/Montreal");
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("tz", timeZone.getID());
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNull(newSession);
    }

    @Test
    public void testIdleTimeoutUsesDefaultValueWhenCapabilityIsNotPresent() {
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        Assert.assertEquals(proxy.getMaxTestIdleTimeSecs(), DEFAULT_MAX_TEST_IDLE_TIME_SECS);
    }

    @Test
    public void testIdleTimeoutUsesDefaultValueWhenCapabilityHasNegativeValue() {
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("idleTimeout", (-20L));
        proxy.getNewSession(requestedCapability);
        Assert.assertEquals(proxy.getMaxTestIdleTimeSecs(), DEFAULT_MAX_TEST_IDLE_TIME_SECS);
    }

    @Test
    public void testIdleTimeoutUsesDefaultValueWhenCapabilityHasFaultyValue() {
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("zal:idleTimeout", "thisValueIsNAN Should not work.");
        proxy.getNewSession(requestedCapability);
        Assert.assertEquals(proxy.getMaxTestIdleTimeSecs(), DEFAULT_MAX_TEST_IDLE_TIME_SECS);
    }

    @Test
    public void testIdleTimeoutUsesValueInStringPassedAsCapability() {
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("zal:idleTimeout", "200");
        proxy.getNewSession(requestedCapability);
        Assert.assertEquals(200L, proxy.getMaxTestIdleTimeSecs());
    }

    @Test
    public void testIdleTimeoutUsesValuePassedAsCapability() {
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("zal:idleTimeout", 180L);
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        Assert.assertEquals(180L, proxy.getMaxTestIdleTimeSecs());
    }

    @Test
    public void pollingThreadTearsDownNodeAfterTestIsCompleted() throws IOException {
        try {
            CommonProxyUtilities commonProxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
            Dashboard.setCommonProxyUtilities(commonProxyUtilities);
            // Supported desired capability for the test session
            Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
            // Start polling thread
            proxy.startPolling();
            // Get a test session
            TestSession newSession = proxy.getNewSession(requestedCapability);
            Assert.assertNotNull(newSession);
            // The node should be busy since there is a session in it
            Assert.assertTrue(proxy.isBusy());
            // We release the session, the node should be free
            WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(request.getRequestType()).thenReturn(STOP_SESSION);
            newSession.getSlot().doFinishRelease();
            proxy.afterCommand(newSession, request, response);
            proxy.afterSession(newSession);
            // After running one test, the node shouldn't be busy and also down
            Assert.assertFalse(proxy.isBusy());
            Callable<Boolean> callable = () -> (registry.getProxyById(proxy.getId())) == null;
            await().pollInterval(FIVE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(callable);
        } finally {
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void normalSessionCommandsDoNotStopNode() {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        // Start polling thread
        proxy.startPolling();
        // Get a test session
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        // The node should be busy since there is a session in it
        Assert.assertTrue(proxy.isBusy());
        // We release the session, the node should be free
        WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getRequestType()).thenReturn(REGULAR);
        proxy.afterCommand(newSession, request, response);
        // The node should not tear down
        Assert.assertTrue(proxy.isBusy());
        Callable<Boolean> callable = () -> !(proxy.isDown());
        await().pollInterval(FIVE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(callable);
    }

    @Test
    public void testIsMarkedAsPassedAndFailedWithCookie() {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        // Start polling thread
        proxy.startPolling();
        // Get a test session
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        // The node should be busy since there is a session in it
        Assert.assertTrue(proxy.isBusy());
        // We release the session, the node should be free
        WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getRequestType()).thenReturn(REGULAR);
        Mockito.when(request.getPathInfo()).thenReturn("/cookie");
        Mockito.when(request.getBody()).thenReturn("{\"cookie\": {\"name\": \"zaleniumTestPassed\", \"value\": true}}");
        proxy.beforeCommand(newSession, request, response);
        Assert.assertEquals(SUCCESS, proxy.getTestInformation().getTestStatus());
        Mockito.when(request.getBody()).thenReturn("{\"cookie\": {\"name\": \"zaleniumTestPassed\", \"value\": false}}");
        proxy.beforeCommand(newSession, request, response);
        Assert.assertEquals(FAILED, proxy.getTestInformation().getTestStatus());
    }

    @Test
    public void nodeShutsDownWhenTestIsIdle() {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("idleTimeout", 1L);
        DockerSeleniumRemoteProxy spyProxy = Mockito.spy(proxy);
        // Start pulling thread
        spyProxy.startPolling();
        // Get a test session
        TestSession newSession = spyProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        // Start the session
        WebDriverRequest webDriverRequest = Mockito.mock(WebDriverRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(webDriverRequest.getMethod()).thenReturn("POST");
        Mockito.when(webDriverRequest.getRequestType()).thenReturn(START_SESSION);
        Mockito.when(webDriverRequest.getPathInfo()).thenReturn("/something");
        spyProxy.beforeCommand(newSession, webDriverRequest, response);
        // The node should be busy since there is a session in it
        Assert.assertTrue(spyProxy.isBusy());
        // The node should tear down after the maximum idle time is elapsed
        Assert.assertTrue(spyProxy.isBusy());
        Callable<Boolean> callable = () -> (registry.getProxyById(spyProxy.getId())) == null;
        await().pollInterval(FIVE_HUNDRED_MILLISECONDS).atMost(FIVE_SECONDS).until(callable);
    }

    @Test
    public void fallbackToDefaultValueWhenEnvVariableIsNotABoolean() {
        try {
            Environment environment = Mockito.mock(Environment.class, Mockito.withSettings().useConstructor());
            Mockito.when(environment.getEnvVariable(ZALENIUM_VIDEO_RECORDING_ENABLED)).thenReturn("any_nonsense_value");
            Mockito.when(environment.getBooleanEnvVariable(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Boolean.class))).thenCallRealMethod();
            DockerSeleniumRemoteProxy.setEnv(environment);
            DockerSeleniumRemoteProxy.readEnvVars();
            Assert.assertEquals(DEFAULT_VIDEO_RECORDING_ENABLED, proxy.isVideoRecordingEnabled());
        } finally {
            DockerSeleniumRemoteProxy.restoreEnvironment();
        }
    }

    @Test
    public void videoRecordingIsStartedAndStopped() throws IOException, MalformedObjectNameException {
        try {
            CommonProxyUtilities commonProxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
            Dashboard.setCommonProxyUtilities(commonProxyUtilities);
            // Creating a spy proxy to verify the invoked methods
            DockerSeleniumRemoteProxy spyProxy = Mockito.spy(proxy);
            // Start pulling thread
            spyProxy.startPolling();
            // Get a test session
            TestSession newSession = spyProxy.getNewSession(getCapabilitySupportedByDockerSelenium());
            newSession.setExternalKey(new ExternalSessionKey("DockerSeleniumRemoteProxy Test"));
            Assert.assertNotNull(newSession);
            // We start the session, in order to start recording
            WebDriverRequest webDriverRequest = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(webDriverRequest.getMethod()).thenReturn("POST");
            Mockito.when(webDriverRequest.getRequestType()).thenReturn(START_SESSION);
            spyProxy.afterCommand(newSession, webDriverRequest, response);
            // Assert video recording started
            String containerId = spyProxy.getContainerId();
            Mockito.verify(spyProxy, Mockito.times(1)).videoRecording(START_RECORDING);
            Mockito.verify(spyProxy, Mockito.times(1)).processContainerAction(START_RECORDING, containerId);
            // We release the sessions, the node should be free
            webDriverRequest = Mockito.mock(WebDriverRequest.class);
            response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(webDriverRequest.getMethod()).thenReturn("DELETE");
            Mockito.when(webDriverRequest.getRequestType()).thenReturn(STOP_SESSION);
            newSession.getSlot().doFinishRelease();
            spyProxy.afterCommand(newSession, webDriverRequest, response);
            spyProxy.afterSession(newSession);
            Assert.assertFalse(spyProxy.isBusy());
            Mockito.verify(spyProxy, Mockito.timeout(40000)).videoRecording(STOP_RECORDING);
            Mockito.verify(spyProxy, Mockito.timeout(40000)).processContainerAction(STOP_RECORDING, containerId);
            Mockito.verify(spyProxy, Mockito.timeout(40000)).copyVideos(containerId);
        } finally {
            ObjectName objectName = new ObjectName("org.seleniumhq.grid:type=RemoteProxy,node=\"http://localhost:30000\"");
            new JMXHelper().unregister(objectName);
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void videoRecordingIsDisabled() throws IOException, MalformedObjectNameException {
        try {
            // Mocking the environment variable to return false for video recording enabled
            Environment environment = Mockito.mock(Environment.class);
            Mockito.when(environment.getEnvVariable(ZALENIUM_VIDEO_RECORDING_ENABLED)).thenReturn("false");
            Mockito.when(environment.getIntEnvVariable(ZALENIUM_MAX_TEST_SESSIONS, 1)).thenReturn(1);
            // Creating a spy proxy to verify the invoked methods
            CommonProxyUtilities commonProxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
            Dashboard.setCommonProxyUtilities(commonProxyUtilities);
            DockerSeleniumRemoteProxy spyProxy = Mockito.spy(proxy);
            DockerSeleniumRemoteProxy.setEnv(environment);
            DockerSeleniumRemoteProxy.readEnvVars();
            // Start pulling thread
            spyProxy.startPolling();
            // Get a test session
            TestSession newSession = spyProxy.getNewSession(getCapabilitySupportedByDockerSelenium());
            newSession.setExternalKey(new ExternalSessionKey("DockerSeleniumRemoteProxy Test"));
            Assert.assertNotNull(newSession);
            // We start the session, in order to start recording
            WebDriverRequest webDriverRequest = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(webDriverRequest.getMethod()).thenReturn("POST");
            Mockito.when(webDriverRequest.getRequestType()).thenReturn(START_SESSION);
            spyProxy.afterCommand(newSession, webDriverRequest, response);
            // Assert no video recording was started, videoRecording is invoked but processContainerAction should not
            Mockito.verify(spyProxy, Mockito.times(1)).videoRecording(START_RECORDING);
            Mockito.verify(spyProxy, Mockito.never()).processContainerAction(START_RECORDING, "");
            // We release the sessions, the node should be free
            webDriverRequest = Mockito.mock(WebDriverRequest.class);
            response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(webDriverRequest.getMethod()).thenReturn("DELETE");
            Mockito.when(webDriverRequest.getRequestType()).thenReturn(STOP_SESSION);
            newSession.getSlot().doFinishRelease();
            spyProxy.afterCommand(newSession, webDriverRequest, response);
            spyProxy.afterSession(newSession);
            Assert.assertFalse(spyProxy.isBusy());
            // Now we assert that videoRecording was invoked but processContainerAction not, neither copyVideos
            Mockito.verify(spyProxy, Mockito.timeout(40000)).videoRecording(STOP_RECORDING);
            Mockito.verify(spyProxy, Mockito.never()).processContainerAction(STOP_RECORDING, "");
            Mockito.verify(spyProxy, Mockito.never()).copyVideos("");
        } finally {
            DockerSeleniumRemoteProxy.restoreEnvironment();
            Dashboard.restoreCommonProxyUtilities();
            ObjectName objectName = new ObjectName("org.seleniumhq.grid:type=RemoteProxy,node=\"http://localhost:30000\"");
            new JMXHelper().unregister(objectName);
        }
    }

    @Test
    public void videoRecordingIsDisabledViaCapability() {
        Map<String, Object> requestedCapability = getCapabilitySupportedByDockerSelenium();
        requestedCapability.put("recordVideo", false);
        TestSession newSession = proxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        Assert.assertFalse(proxy.isVideoRecordingEnabled());
    }
}

