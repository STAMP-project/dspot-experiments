package com.vaadin.tests.resources;


import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class FrontendInitialResourceUITest extends MultiBrowserTest {
    @Test
    public void correctEs5Es6FileImportedThroughFrontend() {
        openTestURL();
        String es;
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            es = "es5";
        } else {
            es = "es6";
        }
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            // For some reason needed by IE11
            testBench().disableWaitForVaadin();
        }
        Assert.assertEquals((("/VAADIN/frontend/" + es) + "/logFilename.js"), findElement(By.tagName("body")).getText());
    }
}

