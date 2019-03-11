package generic;


import com.sun.net.httpserver.HttpServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;


public class HostPortExposedTest {
    private static HttpServer server;

    private static int localServerPort;

    @Rule
    public BrowserWebDriverContainer browser = new BrowserWebDriverContainer().withCapabilities(new ChromeOptions());

    @Test
    public void testContainerRunningAgainstExposedHostPort() {
        // useHostExposedPort {
        final String rootUrl = String.format("http://host.testcontainers.internal:%d/", HostPortExposedTest.localServerPort);
        final RemoteWebDriver webDriver = browser.getWebDriver();
        webDriver.get(rootUrl);
        // }
        final String pageSource = webDriver.getPageSource();
        Assert.assertTrue(pageSource.contains("Hello World!"));
    }
}

