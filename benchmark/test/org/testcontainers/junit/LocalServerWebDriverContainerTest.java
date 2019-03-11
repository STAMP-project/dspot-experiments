package org.testcontainers.junit;


import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;


/**
 * Test that a browser running in a container can access a web server hosted on the host machine (i.e. the one running
 * the tests)
 */
public class LocalServerWebDriverContainerTest {
    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer().withCapabilities(new ChromeOptions());

    private int localPort;

    @Test
    public void testConnection() {
        RemoteWebDriver driver = chrome.getWebDriver();
        // Construct a URL that the browser container can access
        String hostIpAddress = chrome.getTestHostIpAddress();
        driver.get(((("http://" + hostIpAddress) + ":") + (localPort)));
        String headingText = driver.findElement(By.cssSelector("h1")).getText().trim();
        assertEquals("The hardcoded success message was found on a page fetched from a local server", "It worked", headingText);
    }
}

