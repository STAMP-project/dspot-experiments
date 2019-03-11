package org.testcontainers.junit;


import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;


/**
 *
 */
public class CustomWaitTimeoutWebDriverContainerTest extends BaseWebDriverContainerTest {
    @Rule
    public BrowserWebDriverContainer chromeWithCustomTimeout = new BrowserWebDriverContainer().withCapabilities(new ChromeOptions()).withStartupTimeout(Duration.of(30, ChronoUnit.SECONDS));

    @Test
    public void simpleTest() {
        doSimpleWebdriverTest(chromeWithCustomTimeout);
    }
}

