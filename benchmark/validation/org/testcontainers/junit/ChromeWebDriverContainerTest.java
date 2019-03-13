package org.testcontainers.junit;


import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;


/**
 *
 */
public class ChromeWebDriverContainerTest extends BaseWebDriverContainerTest {
    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer().withCapabilities(new ChromeOptions());

    @Test
    public void simpleTest() {
        doSimpleWebdriverTest(chrome);
    }

    @Test
    public void simpleExploreTest() {
        BaseWebDriverContainerTest.doSimpleExplore(chrome);
    }
}

