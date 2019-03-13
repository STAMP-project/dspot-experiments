package de.zalando.ep.zalenium.proxy;


import BrowserType.CHROME;
import BrowserType.IE;
import BrowserType.SAFARI;
import CapabilityType.BROWSER_NAME;
import CapabilityType.PLATFORM_NAME;
import Duration.FIVE_HUNDRED_MILLISECONDS;
import Duration.TWO_SECONDS;
import Platform.MAC;
import Platform.WIN10;
import Platform.WIN8;
import RequestType.REGULAR;
import RequestType.STOP_SESSION;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.zalando.ep.zalenium.dashboard.Dashboard;
import de.zalando.ep.zalenium.dashboard.TestInformation;
import de.zalando.ep.zalenium.util.CommonProxyUtilities;
import de.zalando.ep.zalenium.util.Environment;
import de.zalando.ep.zalenium.util.TestUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSession;
import org.openqa.grid.web.servlet.handler.WebDriverRequest;

import static BrowserStackRemoteProxy.addToDashboardCalled;


public class BrowserStackRemoteProxyTest {
    private BrowserStackRemoteProxy browserStackProxy;

    private GridRegistry registry;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void checkProxyOrdering() {
        // Checking that the DockerSeleniumStarterProxy should come before BrowserStackRemoteProxy
        List<RemoteProxy> sorted = registry.getAllProxies().getSorted();
        Assert.assertEquals(2, sorted.size());
        Assert.assertEquals(DockerSeleniumRemoteProxy.class, sorted.get(0).getClass());
        Assert.assertEquals(BrowserStackRemoteProxy.class, sorted.get(1).getClass());
    }

    @Test
    public void sessionIsCreatedWithCapabilitiesThatDockerSeleniumCannotProcess() {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, SAFARI);
        requestedCapability.put(PLATFORM_NAME, MAC);
        Assert.assertEquals(0, browserStackProxy.getNumberOfSessions());
        TestSession testSession = browserStackProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
        Assert.assertEquals(1, browserStackProxy.getNumberOfSessions());
    }

    @Test
    public void credentialsAreAddedInSessionCreation() throws IOException {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, IE);
        requestedCapability.put(PLATFORM_NAME, WIN8);
        // Getting a test session in the sauce labs node
        TestSession testSession = browserStackProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
        // We need to mock all the needed objects to forward the session and see how in the beforeMethod
        // the BrowserStack user and api key get added to the body request.
        WebDriverRequest request = TestUtils.getMockedWebDriverRequestStartSession(IE, WIN8);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream stream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(stream);
        testSession.setExternalKey(new ExternalSessionKey("BrowserStack Test"));
        testSession.forward(request, response, true);
        Environment env = new Environment();
        // The body should now have the BrowserStack variables
        String expectedBody = String.format(("{\"desiredCapabilities\":{\"browserName\":\"internet explorer\",\"platformName\":" + "\"WIN8\",\"browserstack.user\":\"%s\",\"browserstack.key\":\"%s\"}}"), env.getStringEnvVariable("BROWSER_STACK_USER", ""), env.getStringEnvVariable("BROWSER_STACK_KEY", ""));
        Mockito.verify(request).setBody(expectedBody);
    }

    @Test
    public void testInformationIsRetrievedWhenStoppingSession() throws IOException {
        // Capability which should result in a created session
        try {
            Map<String, Object> requestedCapability = new HashMap<>();
            requestedCapability.put(BROWSER_NAME, CHROME);
            requestedCapability.put(PLATFORM_NAME, WIN10);
            JsonElement informationSample = TestUtils.getTestInformationSample("browserstack_testinformation.json");
            TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
            CommonProxyUtilities commonProxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            Environment env = new Environment();
            String mockTestInformationUrl = "https://www.browserstack.com/automate/sessions/77e51cead8e6e37b0a0feb0dfa69325b2c4acf97.json";
            Mockito.when(commonProxyUtilities.readJSONFromUrl(mockTestInformationUrl, env.getStringEnvVariable("BROWSER_STACK_USER", ""), env.getStringEnvVariable("BROWSER_STACK_KEY", ""))).thenReturn(informationSample);
            BrowserStackRemoteProxy.setCommonProxyUtilities(commonProxyUtilities);
            Dashboard.setCommonProxyUtilities(commonProxyUtilities);
            // Getting a test session in the sauce labs node
            BrowserStackRemoteProxy bsSpyProxy = Mockito.spy(browserStackProxy);
            TestSession testSession = bsSpyProxy.getNewSession(requestedCapability);
            Assert.assertNotNull(testSession);
            String mockSeleniumSessionId = "77e51cead8e6e37b0a0feb0dfa69325b2c4acf97";
            testSession.setExternalKey(new ExternalSessionKey(mockSeleniumSessionId));
            // We release the session, the node should be free
            WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(request.getRequestType()).thenReturn(STOP_SESSION);
            testSession.getSlot().doFinishRelease();
            bsSpyProxy.afterCommand(testSession, request, response);
            Mockito.verify(bsSpyProxy, Mockito.timeout((1000 * 5))).getTestInformation(mockSeleniumSessionId);
            Callable<Boolean> callable = () -> addToDashboardCalled;
            await().pollInterval(FIVE_HUNDRED_MILLISECONDS).atMost(TWO_SECONDS).until(callable);
            TestInformation testInformation = bsSpyProxy.getTestInformation(mockSeleniumSessionId);
            Assert.assertEquals("loadZalandoPageAndCheckTitle", testInformation.getTestName());
            Assert.assertThat(testInformation.getFileName(), CoreMatchers.containsString("browserstack_loadZalandoPageAndCheckTitle_safari_OS_X"));
            Assert.assertEquals("safari 6.2, OS X Mountain Lion", testInformation.getBrowserAndPlatform());
            Assert.assertEquals(("https://www.browserstack.com/s3-upload/bs-video-logs-use/s3/77e51cead8e6e37b0" + (("a0feb0dfa69325b2c4acf97/video-77e51cead8e6e37b0a0feb0dfa69325b2c4acf97.mp4?AWSAccessKeyId=" + "AKIAIOW7IEY5D4X2OFIA&Expires=1497088589&Signature=tQ9SCH1lgg6FjlBIhlTDwummLWc%3D&response-") + "content-type=video%2Fmp4")), testInformation.getVideoUrl());
        } finally {
            BrowserStackRemoteProxy.restoreCommonProxyUtilities();
            BrowserStackRemoteProxy.restoreGa();
            BrowserStackRemoteProxy.restoreEnvironment();
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void requestIsNotModifiedInOtherRequestTypes() throws IOException {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, IE);
        requestedCapability.put(PLATFORM_NAME, WIN8);
        // Getting a test session in the sauce labs node
        TestSession testSession = browserStackProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
        // We need to mock all the needed objects to forward the session and see how in the beforeMethod
        // the SauceLabs user and api key get added to the body request.
        WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("session");
        Mockito.when(request.getServletPath()).thenReturn("session");
        Mockito.when(request.getContextPath()).thenReturn("");
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getRequestType()).thenReturn(REGULAR);
        JsonObject jsonObject = new JsonObject();
        JsonObject desiredCapabilities = new JsonObject();
        desiredCapabilities.addProperty(BROWSER_NAME, IE);
        desiredCapabilities.addProperty(PLATFORM_NAME, WIN8.name());
        jsonObject.add("desiredCapabilities", desiredCapabilities);
        Mockito.when(request.getBody()).thenReturn(jsonObject.toString());
        Enumeration<String> strings = Collections.emptyEnumeration();
        Mockito.when(request.getHeaderNames()).thenReturn(strings);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream stream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(stream);
        testSession.setExternalKey(new ExternalSessionKey("BrowserStack Test"));
        testSession.forward(request, response, true);
        // The body should not be affected and not contain the BrowserStack variables
        Assert.assertThat(request.getBody(), CoreMatchers.containsString(jsonObject.toString()));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("browserstack.user")));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("browserstack.key")));
        Mockito.when(request.getMethod()).thenReturn("GET");
        testSession.forward(request, response, true);
        Assert.assertThat(request.getBody(), CoreMatchers.containsString(jsonObject.toString()));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("browserstack.user")));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("browserstack.key")));
    }

    @Test
    public void checkVideoFileExtensionAndProxyName() {
        Assert.assertEquals(".mp4", browserStackProxy.getVideoFileExtension());
        Assert.assertEquals("BrowserStack", browserStackProxy.getProxyName());
    }
}

