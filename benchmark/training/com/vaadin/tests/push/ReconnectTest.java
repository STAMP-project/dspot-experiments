package com.vaadin.tests.push;


import com.vaadin.tests.tb3.MultiBrowserTestWithProxy;
import java.io.IOException;
import org.junit.Test;


public abstract class ReconnectTest extends MultiBrowserTestWithProxy {
    @Test
    public void messageIsQueuedOnDisconnect() throws IOException {
        disconnectProxy();
        clickButtonAndWaitForTwoReconnectAttempts();
        connectAndVerifyConnectionEstablished();
        waitUntilClientCounterChanges(1);
    }

    @Test
    public void messageIsNotSentBeforeConnectionIsEstablished() throws IOException, InterruptedException {
        disconnectProxy();
        waitForNextReconnectionAttempt();
        clickButtonAndWaitForTwoReconnectAttempts();
        connectAndVerifyConnectionEstablished();
        waitUntilClientCounterChanges(1);
    }
}

