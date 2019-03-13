package com.vaadin.tests.application;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class PreserveWithExpiredHeartbeatTest extends SingleBrowserTest {
    @Test
    public void testNavigateBackAfterMissingHeartbeats() throws InterruptedException {
        final int heartbeatInterval = 5000;
        openTestURL();
        String originalId = getUiIdentification();
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < (heartbeatInterval * 3.1)) {
            // "Close" the tab
            driver.get("about:blank");
            sleep((heartbeatInterval / 2));
            // "Reopen" tab
            openTestURL();
            // Verify that that we still get the same UI
            Assert.assertEquals("Original UI has been closed", originalId, getUiIdentification());
        } 
    }
}

