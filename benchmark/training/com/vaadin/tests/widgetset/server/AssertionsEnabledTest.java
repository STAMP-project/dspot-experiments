package com.vaadin.tests.widgetset.server;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class AssertionsEnabledTest extends SingleBrowserTest {
    private static final String FAILING_CLASSNAME = "non-existent-widget";

    @Test
    public void testAssertionsAreEnabled() {
        setDebug(true);
        openTestURL();
        // If assertions are disabled, the AssertionFailureWidget will add a
        // label to the UI.
        Assert.assertFalse((("Label with classname " + (AssertionsEnabledTest.FAILING_CLASSNAME)) + " should not exist"), isElementPresent(By.className(AssertionsEnabledTest.FAILING_CLASSNAME)));
        Assert.assertTrue("Assertion error Notification is not present", isElementPresent(NotificationElement.class));
    }
}

