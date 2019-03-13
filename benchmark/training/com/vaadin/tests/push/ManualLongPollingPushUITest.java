package com.vaadin.tests.push;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ManualLongPollingPushUITest extends SingleBrowserTest {
    @Test
    public void doubleManualPushDoesNotFreezeApplication() {
        openTestURL();
        $(ButtonElement.class).caption("Double manual push after 1s").first().click();
        waitUntilLogText("2. Second message logged after 1s, followed by manual push");
        $(ButtonElement.class).caption("Manual push after 1s").first().click();
        waitUntilLogText("3. Logged after 1s, followed by manual push");
    }
}

