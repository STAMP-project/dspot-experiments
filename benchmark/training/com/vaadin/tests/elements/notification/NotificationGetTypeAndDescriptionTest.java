package com.vaadin.tests.elements.notification;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class NotificationGetTypeAndDescriptionTest extends MultiBrowserTest {
    @Test
    public void testWarning() {
        testNotificationByIndex(0);
    }

    @Test
    public void testError() {
        testNotificationByIndex(1);
    }

    @Test
    public void testHumanized() {
        testNotificationByIndex(2);
    }

    @Test
    public void testTrayNotification() {
        testNotificationByIndex(3);
    }
}

