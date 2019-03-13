package de.zalando.ep.zalenium.proxy;


import BrowserType.CHROME;
import BrowserType.IE;
import CapabilityType.BROWSER_NAME;
import CapabilityType.PLATFORM_NAME;
import Platform.MAC;
import Platform.WIN8;
import RequestType.REGULAR;
import RequestType.STOP_SESSION;
import com.google.gson.JsonObject;
import de.zalando.ep.zalenium.dashboard.Dashboard;
import de.zalando.ep.zalenium.dashboard.DashboardCollection;
import de.zalando.ep.zalenium.dashboard.TestInformation;
import de.zalando.ep.zalenium.util.CommonProxyUtilities;
import de.zalando.ep.zalenium.util.Environment;
import de.zalando.ep.zalenium.util.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


public class TestingBotRemoteProxyTest {
    private TestingBotRemoteProxy testingBotProxy;

    private GridRegistry registry;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void checkProxyOrdering() {
        // Checking that the DockerSeleniumStarterProxy should come before TestingBotProxy
        List<RemoteProxy> sorted = registry.getAllProxies().getSorted();
        Assert.assertEquals(2, sorted.size());
        Assert.assertEquals(DockerSeleniumRemoteProxy.class, sorted.get(0).getClass());
        Assert.assertEquals(TestingBotRemoteProxy.class, sorted.get(1).getClass());
    }

    @Test
    public void sessionIsCreatedWithCapabilitiesThatDockerSeleniumCannotProcess() {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, IE);
        requestedCapability.put(PLATFORM_NAME, WIN8);
        TestSession testSession = testingBotProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
    }

    @Test
    public void credentialsAreAddedInSessionCreation() throws IOException {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, IE);
        requestedCapability.put(PLATFORM_NAME, WIN8);
        // Getting a test session in the TestingBot node
        TestSession testSession = testingBotProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
        // We need to mock all the needed objects to forward the session and see how in the beforeMethod
        // the TestingBot user and api key get added to the body request.
        WebDriverRequest request = TestUtils.getMockedWebDriverRequestStartSession(IE, WIN8);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream stream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(stream);
        testSession.forward(request, response, true);
        Environment env = new Environment();
        // The body should now have the TestingBot variables
        String expectedBody = String.format(("{\"desiredCapabilities\":{\"browserName\":\"internet explorer\",\"platformName\":" + "\"WIN8\",\"key\":\"%s\",\"secret\":\"%s\",\"version\":\"latest\"}}"), env.getStringEnvVariable("TESTINGBOT_KEY", ""), env.getStringEnvVariable("TESTINGBOT_SECRET", ""));
        Mockito.verify(request).setBody(expectedBody);
    }

    @Test
    public void requestIsNotModifiedInOtherRequestTypes() throws IOException {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, IE);
        requestedCapability.put(PLATFORM_NAME, WIN8);
        // Getting a test session in the TestingBot node
        TestSession testSession = testingBotProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
        // We need to mock all the needed objects to forward the session and see how in the beforeMethod
        // the TestingBot user and api key get added to the body request.
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
        testSession.forward(request, response, true);
        // The body should not be affected and not contain the TestingBot variables
        Assert.assertThat(request.getBody(), CoreMatchers.containsString(jsonObject.toString()));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("key")));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("secret")));
        Mockito.when(request.getMethod()).thenReturn("GET");
        testSession.forward(request, response, true);
        Assert.assertThat(request.getBody(), CoreMatchers.containsString(jsonObject.toString()));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("key")));
        Assert.assertThat(request.getBody(), CoreMatchers.not(CoreMatchers.containsString("secret")));
    }

    @Test
    public void testInformationIsRetrievedWhenStoppingSession() {
        // Capability which should result in a created session
        Map<String, Object> requestedCapability = new HashMap<>();
        requestedCapability.put(BROWSER_NAME, CHROME);
        requestedCapability.put(PLATFORM_NAME, MAC);
        // Getting a test session in the TestingBot node
        TestingBotRemoteProxy spyProxy = Mockito.spy(testingBotProxy);
        TestSession testSession = spyProxy.getNewSession(requestedCapability);
        Assert.assertNotNull(testSession);
        String mockSeleniumSessionId = "2cf5d115-ca6f-4bc4-bc06-a4fca00836ce";
        testSession.setExternalKey(new ExternalSessionKey(mockSeleniumSessionId));
        // We release the session, the node should be free
        WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getMethod()).thenReturn("DELETE");
        Mockito.when(request.getRequestType()).thenReturn(STOP_SESSION);
        testSession.getSlot().doFinishRelease();
        spyProxy.afterCommand(testSession, request, response);
        Mockito.verify(spyProxy, Mockito.timeout((1000 * 5))).getTestInformation(mockSeleniumSessionId);
        TestInformation testInformation = spyProxy.getTestInformation(mockSeleniumSessionId);
        Assert.assertEquals("loadZalandoPageAndCheckTitle", testInformation.getTestName());
        Assert.assertThat(testInformation.getFileName(), CoreMatchers.containsString("testingbot_loadZalandoPageAndCheckTitle_Safari9_CAPITAN"));
        Assert.assertEquals("Safari9 9, CAPITAN", testInformation.getBrowserAndPlatform());
        Assert.assertEquals("https://s3-eu-west-1.amazonaws.com/eurectestingbot/2cf5d115-ca6f-4bc4-bc06-a4fca00836ce.mp4", testInformation.getVideoUrl());
    }

    @Test
    public void dashboardFilesGetCopied() {
        try {
            // Capability which should result in a created session
            Map<String, Object> requestedCapability = new HashMap<>();
            requestedCapability.put(BROWSER_NAME, CHROME);
            requestedCapability.put(PLATFORM_NAME, MAC);
            // Getting a test session in the TestingBot node
            TestingBotRemoteProxy spyProxy = Mockito.spy(testingBotProxy);
            TestSession testSession = spyProxy.getNewSession(requestedCapability);
            Assert.assertNotNull(testSession);
            String mockSeleniumSessionId = "2cf5d115-ca6f-4bc4-bc06-a4fca00836ce";
            testSession.setExternalKey(new ExternalSessionKey(mockSeleniumSessionId));
            // We release the session, the node should be free
            WebDriverRequest request = Mockito.mock(WebDriverRequest.class);
            HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(request.getRequestType()).thenReturn(STOP_SESSION);
            testSession.getSlot().doFinishRelease();
            spyProxy.afterCommand(testSession, request, response);
            CommonProxyUtilities proxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
            Dashboard.setCommonProxyUtilities(proxyUtilities);
            TestInformation testInformation = spyProxy.getTestInformation(mockSeleniumSessionId);
            DashboardCollection.updateDashboard(testInformation);
            File videosFolder = new File(temporaryFolder.getRoot().getAbsolutePath(), "videos");
            Assert.assertTrue(videosFolder.isDirectory());
            File amountOfRunTests = new File(videosFolder, "executedTestsInfo.json");
            Assert.assertTrue(amountOfRunTests.exists());
            File dashboard = new File(videosFolder, "dashboard.html");
            Assert.assertTrue(dashboard.exists());
            Assert.assertTrue(dashboard.isFile());
            File cssFolder = new File(videosFolder, "css");
            Assert.assertTrue(cssFolder.exists());
            Assert.assertTrue(cssFolder.isDirectory());
            File jsFolder = new File(videosFolder, "js");
            Assert.assertTrue(jsFolder.exists());
            Assert.assertTrue(jsFolder.isDirectory());
        } finally {
            Dashboard.restoreCommonProxyUtilities();
        }
    }

    @Test
    public void checkVideoFileExtensionAndProxyName() {
        Assert.assertEquals(".mp4", testingBotProxy.getVideoFileExtension());
        Assert.assertEquals("TestingBot", testingBotProxy.getProxyName());
    }
}

