package org.testcontainers.junit;


import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.NginxContainer;


/**
 *
 *
 * @author richardnorth
 */
public class LinkedContainerTest {
    private static File contentFolder = new File(((System.getProperty("user.home")) + "/.tmp-test-container"));

    @Rule
    public Network network = Network.newNetwork();

    @Rule
    public NginxContainer nginx = new NginxContainer().withNetwork(network).withNetworkAliases("nginx").withCustomContent(LinkedContainerTest.contentFolder.toString());

    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer().withNetwork(network).withCapabilities(new ChromeOptions());

    @Test
    public void testWebDriverToNginxContainerAccessViaContainerLink() {
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get("http://nginx/");
        assertEquals("Using selenium, an HTTP GET from the nginx server returns the index.html from the custom content directory", "This worked", driver.findElement(By.tagName("body")).getText());
    }
}

