package com.vaadin.tests.components.customlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class OverflowAutoFixTest extends MultiBrowserTest {
    @Test
    public void testRestoreOverflowHidden() throws InterruptedException {
        openTestURL();
        click("run-button-one");
        assertElementCssValueEquals("first-scrollbar", "overflow", "scroll");
        assertElementCssValueEquals("second-scrollbar", "overflow-x", "hidden");
        assertElementCssValueEquals("third-scrollbar", "overflow-y", "hidden");
    }

    @Test
    public void testRestoreOverflowOther() throws InterruptedException {
        openTestURL();
        click("run-button-two");
        assertElementCssValueEquals("first-scrollbar", "overflow", "visible");
        assertElementCssValueEquals("second-scrollbar", "overflow-x", "scroll");
        assertElementCssValueEquals("third-scrollbar", "overflow-y", "auto");
    }
}

