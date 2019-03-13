package com.vaadin.tests.push;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class PushConfigurationWebSocketTest extends PushConfigurationTest {
    @Test
    public void testWebsocket() throws InterruptedException {
        getTransportSelect().selectByText("Websocket");
        getPushModeSelect().selectByText("Automatic");
        MatcherAssert.assertThat(getStatusText(), CoreMatchers.containsString("fallbackTransport: long-polling"));
        MatcherAssert.assertThat(getStatusText(), CoreMatchers.containsString("transport: websocket"));
        waitForServerCounterToUpdate();
        // Use debug console to verify we used the correct transport type
        MatcherAssert.assertThat(driver.getPageSource(), CoreMatchers.containsString("Push connection established using websocket"));
        MatcherAssert.assertThat(driver.getPageSource(), CoreMatchers.not(CoreMatchers.containsString("Push connection established using streaming")));
    }
}

