package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class TabBarWidthTest extends MultiBrowserTest {
    @Test
    public void testWidths() throws Exception {
        openTestURL();
        // Initial rendering.
        compareScreen("tab-bar-width-init");
        // Remove all widths.
        vaadinElementById("toggleWidths").click();
        compareScreen("tab-bar-width-undefined");
        // Restore all widths. This should restore the rendering to the same
        // point as the initial rendering.
        vaadinElementById("toggleWidths").click();
        compareScreen("tab-bar-width-restored");
    }
}

