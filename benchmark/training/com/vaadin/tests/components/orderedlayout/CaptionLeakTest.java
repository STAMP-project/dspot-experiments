package com.vaadin.tests.components.orderedlayout;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class CaptionLeakTest extends MultiBrowserTest {
    @Test
    public void testCaptionLeak() throws Exception {
        setDebug(true);
        openTestURL();
        // Make sure debug window is visible
        showDebugWindow();
        openDebugLogTab();
        // this should be present
        // 3 general non-connector elements, none accumulated on click
        checkConnectorCount();
        clearLog();
        $(ButtonElement.class).caption("Set leaky content").first().click();
        checkConnectorCount();
        // nothing accumulates over clicks
        clearLog();
        $(ButtonElement.class).caption("Set leaky content").first().click();
        checkConnectorCount();
    }

    @Test
    public void testNoCaptionLeak() throws Exception {
        setDebug(true);
        openTestURL();
        openDebugLogTab();
        clearLog();
        $(ButtonElement.class).caption("Set non leaky content").first().click();
        // this should be present
        // 3 general non-connector elements, none accumulated on click
        checkConnectorCount();
    }
}

