package com.vaadin.tests;


import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to check high resolution time availability in browser (depending on
 * browser).
 *
 * @author Vaadin Ltd
 */
public class CurrentTimeMillisTest extends MultiBrowserTest {
    @Test
    public void testJsonParsing() {
        setDebug(true);
        openTestURL();
        boolean phantomJs1 = (BrowserUtil.isPhantomJS(getDesiredCapabilities())) && ("1".equals(getDesiredCapabilities().getVersion()));
        boolean highResTimeSupported = (!phantomJs1) && (!(BrowserUtil.isSafari(getDesiredCapabilities())));
        String time = getJsonParsingTime();
        Assert.assertNotNull("JSON parsing time is not found", time);
        time = time.trim();
        if (time.endsWith("ms")) {
            time = time.substring(0, ((time.length()) - 2));
        }
        if (highResTimeSupported) {
            if ((BrowserUtil.isChrome(getDesiredCapabilities())) || (BrowserUtil.isFirefox(getDesiredCapabilities()))) {
                // Chrome (version 33 at least) sometimes doesn't use high res
                // time for very short times
                Assert.assertTrue((("High resolution time is not used in " + "JSON parsing mesurement. Time=") + time), (((time.equals("0")) || (time.equals("1"))) || ((time.indexOf('.')) > 0)));
            } else {
                Assert.assertTrue((("High resolution time is not used in " + "JSON parsing mesurement. Time=") + time), ((time.indexOf('.')) > 0));
            }
        } else {
            Assert.assertFalse((("Unexpected dot is detected in browser " + ("that doesn't support high resolution time and " + "should report time as integer number. Time=")) + time), ((time.indexOf('.')) > 0));
        }
    }
}

