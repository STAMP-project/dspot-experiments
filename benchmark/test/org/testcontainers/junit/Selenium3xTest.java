package org.testcontainers.junit;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;


/**
 * Simple test to check that readiness detection works correctly across major versions of the containers.
 */
@RunWith(Parameterized.class)
public class Selenium3xTest {
    @Parameterized.Parameter
    public String tag;

    @Test
    public void testAdditionalStartupString() {
        try (BrowserWebDriverContainer chrome = new BrowserWebDriverContainer(("selenium/standalone-chrome-debug:" + (tag))).withCapabilities(new ChromeOptions())) {
            chrome.start();
        }
    }
}

