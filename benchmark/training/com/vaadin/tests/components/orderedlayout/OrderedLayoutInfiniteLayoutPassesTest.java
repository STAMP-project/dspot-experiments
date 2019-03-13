package com.vaadin.tests.components.orderedlayout;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class OrderedLayoutInfiniteLayoutPassesTest extends MultiBrowserTest {
    @Test
    public void ensureFiniteLayoutPhase() throws Exception {
        openTestURL("debug");
        zoomBrowserIn();
        try {
            $(ButtonElement.class).first().click();
            assertNoErrorNotifications();
            resetZoom();
            assertNoErrorNotifications();
        } finally {
            // Reopen test to ensure that modal window does not prevent zoom
            // reset from taking place
            openTestURL();
            resetZoom();
        }
    }
}

