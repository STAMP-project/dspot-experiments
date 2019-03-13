package com.vaadin.tests.application;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TerminalErrorNotificationTest extends MultiBrowserTest {
    @Test
    public void tb2test() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertTrue(isElementPresent(NotificationElement.class));
        Assert.assertEquals("Got an exception: You asked for it", $(NotificationElement.class).first().getCaption());
    }
}

