package com.vaadin.tests.push;


import com.vaadin.tests.tb3.WebsocketTest;
import org.junit.Test;


public class PushLargeDataWebsocketTest extends WebsocketTest {
    @Test
    public void testWebsocketLargeData() throws Exception {
        openTestURL();
        // Without this timing will be completly off as pushing "start" can
        // remain waiting for all pushes to complete
        testBench(driver).disableWaitForVaadin();
        push();
        // Push complete. Browser will reconnect now as > 10MB has been sent
        // Push again to ensure push still works
        push();
    }
}

