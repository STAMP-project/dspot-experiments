package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class PollListeningTest extends MultiBrowserTest {
    @Test
    public void testReceivePollEvent() {
        openTestURL();
        waitUntilPollEventReceived();
    }
}

