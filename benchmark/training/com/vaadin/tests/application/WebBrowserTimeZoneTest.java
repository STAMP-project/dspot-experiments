package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;


public class WebBrowserTimeZoneTest extends MultiBrowserTest {
    @Test
    public void testBrowserTimeZoneInfo() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        // Ask TimeZone from browser
        String tzOffset = ((JavascriptExecutor) (getDriver())).executeScript("return new Date().getTimezoneOffset()").toString();
        // Translate the same way as Vaadin should
        int offsetMillis = ((-(Integer.parseInt(tzOffset))) * 60) * 1000;
        // Check that server got the same value.
        assertLabelText("Browser offset", offsetMillis);
    }
}

