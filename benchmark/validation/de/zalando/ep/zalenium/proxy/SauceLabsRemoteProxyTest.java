package de.zalando.ep.zalenium.proxy;


import BrowserType.CHROME;
import BrowserType.EDGE;
import BrowserType.SAFARI;
import CapabilityType.BROWSER_NAME;
import CapabilityType.PLATFORM_NAME;
import Platform.LINUX;
import Platform.MAC;
import Platform.WIN10;
import RequestType.STOP_SESSION;
import com.google.gson.JsonElement;
import de.zalando.ep.zalenium.dashboard.TestInformation;
import de.zalando.ep.zalenium.util.CommonProxyUtilities;
import de.zalando.ep.zalenium.util.Environment;
import de.zalando.ep.zalenium.util.GoogleAnalyticsApi;
import de.zalando.ep.zalenium.util.TestUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.web.servlet.handler.WebDriverRequest;


public class SauceLabsRemoteProxyTest {
    private SauceLabsRemoteProxy sauceLabsProxy;

    private GridRegistry registry;

    @Test
    public void doesNotCreateSessionWhenDockerSeleniumCanProcessRequest() {
        // This capability is supported by docker-selenium, so it should return a null session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, CHROME);
        requestedCapability.put(PLATFORM_NAME, LINUX);
        TestSession testSession = sauceLabsProxy.getNewSession(requestedCapability);
        Assert.assertNull(testSession);
    }

    @Test
    public void sessionIsCreatedWithCapabilitiesThatDockerSeleniumCannotProcess() {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, EDGE);
        requestedCapability.put(PLATFORM_NAME, WIN10);
        TestSession testSession = sauceLabsProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
    }

    @Test
    public void checkProxyOrdering() {
        // Checking that the DockerSeleniumStarterProxy should come before SauceLabsProxy
        List<RemoteProxy> sorted = registry.getAllProxies().getSorted();
        Assert.assertEquals(2, sorted.size());
        Assert.assertEquals(DockerSeleniumRemoteProxy.class, sorted.get(0).getClass());
        Assert.assertEquals(SauceLabsRemoteProxy.class, sorted.get(1).getClass());
    }

    @Test
    public void checkBeforeSessionInvocation() throws IOException {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, SAFARI);
        requestedCapability.put(PLATFORM_NAME, MAC);
        // Getting a test session in the sauce labs node
        TestSession testSession = sauceLabsProxy.getNewSession(requestedCapability);
        System.out.println(requestedCapability.toString());
        Assert.assertNotNull(testSession);
        // We need to mock all the needed objects to forward the session and see how in the beforeMethod
        // the SauceLabs user and api key get added to the body request.
        WebDriverRequest request = TestUtils.getMockedWebDriverRequestStartSession(SAFARI, MAC);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream stream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(stream);
        testSession.forward(request, response, true);
        Environment env = new Environment();
        // The body should now have the SauceLabs variables
        String expectedBody = String.format(("{\"desiredCapabilities\":{\"browserName\":\"safari\",\"platformName\":" + "\"MAC\",\"username\":\"%s\",\"accessKey\":\"%s\",\"version\":\"latest\"}}"), env.getStringEnvVariable("SAUCE_USERNAME", ""), env.getStringEnvVariable("SAUCE_ACCESS_KEY", ""));
        Mockito.verify(request).setBody(expectedBody);
    }

    @Test
    public void testInformationIsRetrievedWhenStoppingSession() throws IOException {
        try {
            // Capability which should result in a created session
            Map<String, Object> requestedCapability = new HashMap<>();
            requestedCapability.put(BROWSER_NAME, CHROME);
            requestedCapability.put(PLATFORM_NAME, MAC);
            // Getting a test session in the sauce labs node
            SauceLabsRemoteProxy sauceLabsSpyProxy = Mockito.spy(sauceLabsProxy);
            JsonElement informationSample = TestUtils.getTestInformationSample("saucelabs_testinformation.json");
            CommonProxyUtilities commonProxyUtilities = Mockito.mock(CommonProxyUtilities.class);
            Mockito.when(commonProxyUtilities.readJSONFromUrl(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(informationSample);
            SauceLabsRemoteProxy.setCommonProxyUtilities(commonProxyUtilities);
            TestSession testSession = sauceLabsSpyProxy.getNewSession(requestedCapability);
            Assert.assertNotNull(testSession);
            String mockSeleniumSessionId = "72e4f8ecf04440fe965faf657864ed52";
            testSession.setExternalKey(new ExternalSessionKey(mockSeleniumSessionId));
            // We release the session, the node should be free
            WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(request.getRequestType()).thenReturn(STOP_SESSION);
            testSession.getSlot().doFinishRelease();
            sauceLabsSpyProxy.afterCommand(testSession, request, response);
            Mockito.verify(sauceLabsSpyProxy, Mockito.timeout((1000 * 5))).getTestInformation(mockSeleniumSessionId);
            TestInformation testInformation = sauceLabsSpyProxy.getTestInformation(mockSeleniumSessionId);
            Assert.assertEquals(mockSeleniumSessionId, testInformation.getTestName());
            Assert.assertThat(testInformation.getFileName(), CoreMatchers.containsString("saucelabs_72e4f8ecf04440fe965faf657864ed52_googlechrome_Windows_2008"));
            Assert.assertEquals("googlechrome 56, Windows 2008", testInformation.getBrowserAndPlatform());
            Assert.assertThat(testInformation.getVideoUrl(), CoreMatchers.containsString("jobs/72e4f8ecf04440fe965faf657864ed52/assets/video.mp4"));
        } finally {
            SauceLabsRemoteProxy.restoreCommonProxyUtilities();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nodeHasCapabilitiesEvenWhenUrlCallFails() {
        try {
            CommonProxyUtilities commonProxyUtilities = Mockito.mock(CommonProxyUtilities.class);
            Mockito.when(commonProxyUtilities.readJSONFromUrl(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(null);
            SauceLabsRemoteProxy.setCommonProxyUtilities(commonProxyUtilities);
            RegistrationRequest request = TestUtils.getRegistrationRequestForTesting(30001, SauceLabsRemoteProxy.class.getCanonicalName());
            request = SauceLabsRemoteProxy.updateSLCapabilities(request, "");
            // Now the capabilities should be filled even if the url was not fetched
            Assert.assertFalse(request.getConfiguration().capabilities.isEmpty());
        } finally {
            SauceLabsRemoteProxy.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void testEventIsInvoked() throws IOException {
        try {
            // Capability which should result in a created session
            Map<String, Object> requestedCapability = new HashMap<>();
            requestedCapability.put(BROWSER_NAME, SAFARI);
            requestedCapability.put(PLATFORM_NAME, MAC);
            // Getting a test session in the sauce labs node
            TestSession testSession = sauceLabsProxy.getNewSession(requestedCapability);
            Assert.assertNotNull(testSession);
            // We release the sessions and invoke the afterCommand with a mocked object
            Environment env = Mockito.mock(Environment.class);
            Mockito.when(env.getBooleanEnvVariable("ZALENIUM_SEND_ANONYMOUS_USAGE_INFO", false)).thenReturn(true);
            Mockito.when(env.getStringEnvVariable("ZALENIUM_GA_API_VERSION", "")).thenReturn("1");
            Mockito.when(env.getStringEnvVariable("ZALENIUM_GA_TRACKING_ID", "")).thenReturn("UA-88441352");
            Mockito.when(env.getStringEnvVariable("ZALENIUM_GA_ENDPOINT", "")).thenReturn("https://www.google-analytics.com/collect");
            Mockito.when(env.getStringEnvVariable("ZALENIUM_GA_ANONYMOUS_CLIENT_ID", "")).thenReturn("RANDOM_STRING");
            HttpClient client = Mockito.mock(HttpClient.class);
            HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
            Mockito.when(client.execute(ArgumentMatchers.any(HttpPost.class))).thenReturn(httpResponse);
            GoogleAnalyticsApi ga = new GoogleAnalyticsApi();
            GoogleAnalyticsApi gaSpy = Mockito.spy(ga);
            gaSpy.setEnv(env);
            gaSpy.setHttpClient(client);
            SauceLabsRemoteProxy.setGa(gaSpy);
            WebDriverRequest webDriverRequest = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(webDriverRequest.getMethod()).thenReturn("DELETE");
            Mockito.when(webDriverRequest.getRequestType()).thenReturn(STOP_SESSION);
            testSession.getSlot().doFinishRelease();
            testSession.setExternalKey(new ExternalSessionKey("testKey"));
            sauceLabsProxy.afterCommand(testSession, webDriverRequest, response);
            Mockito.verify(gaSpy, Mockito.times(1)).testEvent(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
        } finally {
            SauceLabsRemoteProxy.restoreGa();
        }
    }

    @Test
    public void slotIsReleasedWhenTestIsIdle() throws IOException {
        // Supported desired capability for the test session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, SAFARI);
        requestedCapability.put(PLATFORM_NAME, MAC);
        SauceLabsRemoteProxy sauceLabsSpyProxy = Mockito.spy(sauceLabsProxy);
        // Set a short idle time
        sauceLabsSpyProxy.setMaxTestIdleTime(1L);
        // Start poller thread
        sauceLabsSpyProxy.startPolling();
        // Get a test session
        TestSession newSession = sauceLabsSpyProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(newSession);
        newSession.setExternalKey(new ExternalSessionKey("RANDOM_EXTERNAL_KEY"));
        // Start the session
        WebDriverRequest request = TestUtils.getMockedWebDriverRequestStartSession(SAFARI, MAC);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream stream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(stream);
        sauceLabsSpyProxy.beforeCommand(newSession, request, response);
        // The terminateIdleSessions() method should be called after a moment
        Mockito.verify(sauceLabsSpyProxy, Mockito.timeout(2000)).terminateIdleSessions();
        Mockito.verify(sauceLabsSpyProxy, Mockito.timeout(2000)).addTestToDashboard("RANDOM_EXTERNAL_KEY", false);
    }
}

