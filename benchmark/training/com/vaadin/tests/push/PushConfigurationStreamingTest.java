package com.vaadin.tests.push;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class PushConfigurationStreamingTest extends PushConfigurationTest {
    @Test
    public void testStreaming() throws InterruptedException {
        openDebugLogTab();
        getTransportSelect().selectByText("Streaming");
        MatcherAssert.assertThat(getStatusText(), CoreMatchers.containsString("fallbackTransport: long-polling"));
        MatcherAssert.assertThat(getStatusText(), CoreMatchers.containsString("transport: streaming"));
        clearDebugMessages();
        getPushModeSelect().selectByText("Automatic");
        waitForDebugMessage("Push connection established using streaming", 10);
        waitForServerCounterToUpdate();
    }
}

