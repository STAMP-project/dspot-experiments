package com.vaadin.tests;


import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;


public class VerifyBrowserVersionTest extends MultiBrowserTest {
    @Test
    public void verifyUserAgent() {
        openTestURL();
        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();
        String userAgent = vaadinElementById("userAgent").getText();
        String browserIdentifier;
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            // Chrome version does not necessarily match the desired version
            // because of auto updates...
            browserIdentifier = (getExpectedUserAgentString(getDesiredCapabilities())) + "69";
        } else
            if (BrowserUtil.isFirefox(getDesiredCapabilities())) {
                browserIdentifier = (getExpectedUserAgentString(getDesiredCapabilities())) + "58";
            } else {
                browserIdentifier = (getExpectedUserAgentString(desiredCapabilities)) + (desiredCapabilities.getVersion());
            }

        MatcherAssert.assertThat(userAgent, CoreMatchers.containsString(browserIdentifier));
        MatcherAssert.assertThat(vaadinElementById("touchDevice").getText(), CoreMatchers.is("Touch device? No"));
    }
}

