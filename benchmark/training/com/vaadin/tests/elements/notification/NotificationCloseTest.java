package com.vaadin.tests.elements.notification;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class NotificationCloseTest extends MultiBrowserTest {
    @Test
    public void testWarning() {
        testClose(0);
    }

    @Test
    public void testError() {
        testClose(1);
    }

    @Test
    public void testHumanized() {
        testClose(2);
    }

    @Test
    public void testTrayNotification() {
        testClose(3);
    }
}

