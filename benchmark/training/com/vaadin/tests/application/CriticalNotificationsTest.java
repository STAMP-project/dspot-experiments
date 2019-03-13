package com.vaadin.tests.application;


import com.vaadin.tests.tb3.MultiBrowserThemeTest;
import org.junit.Test;


public class CriticalNotificationsTest extends MultiBrowserThemeTest {
    @Test
    public void internalError() throws Exception {
        testCriticalNotification("Internal error");
    }

    @Test
    public void internalErrorDetails() throws Exception {
        testCriticalNotification("Internal error", true);
    }

    @Test
    public void custom() throws Exception {
        testCriticalNotification("Custom");
    }

    @Test
    public void sessionExpired() throws Exception {
        testCriticalNotification("Session expired");
    }

    @Test
    public void sessionExpiredDetails() throws Exception {
        testCriticalNotification("Session expired", true);
    }
}

