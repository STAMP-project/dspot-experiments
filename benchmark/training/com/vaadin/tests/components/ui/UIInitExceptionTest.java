package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class UIInitExceptionTest extends SingleBrowserTest {
    @Test
    public void testExceptionOnUIInit() throws Exception {
        openTestURL();
        Assert.assertTrue("Page does not contain the given text", driver.getPageSource().contains("Catch me if you can"));
    }
}

