package com.vaadin.tests.push;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class PushConfigurationLongPollingTest extends PushConfigurationTest {
    @Test
    public void testLongPolling() throws InterruptedException {
        openDebugLogTab();
        getTransportSelect().selectByText("Long polling");
        MatcherAssert.assertThat(getStatusText(), CoreMatchers.containsString("fallbackTransport: long-polling"));
        MatcherAssert.assertThat(getStatusText(), CoreMatchers.containsString("transport: long-polling"));
        clearDebugMessages();
        getPushModeSelect().selectByText("Automatic");
        waitForDebugMessage("Push connection established using long-polling", 10);
        waitForServerCounterToUpdate();
    }
}

