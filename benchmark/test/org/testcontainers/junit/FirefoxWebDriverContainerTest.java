package org.testcontainers.junit;


import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;


/**
 *
 */
public class FirefoxWebDriverContainerTest extends BaseWebDriverContainerTest {
    @Rule
    public BrowserWebDriverContainer firefox = new BrowserWebDriverContainer().withCapabilities(new FirefoxOptions());

    @Test
    public void simpleTest() {
        doSimpleWebdriverTest(firefox);
    }

    @Test
    public void simpleExploreTest() {
        BaseWebDriverContainerTest.doSimpleExplore(firefox);
    }
}

