package com.vaadin.tests.components.notification;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;


/**
 * Test to check notification delay.
 *
 * @author Vaadin Ltd
 */
public class NotificationDelayTest extends MultiBrowserTest {
    @Test
    public void testDelay() throws InterruptedException {
        openTestURL();
        Assert.assertTrue("No notification found", hasNotification());
        waitUntil(( input) -> {
            new Actions(getDriver()).moveByOffset(10, 10).perform();
            return !(hasNotification());
        });
    }
}

