package com.vaadin.tests.components;


import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class MenuBarDownloadBrowserOpenerUITest extends MultiBrowserTest {
    @Test
    public void testTriggerExtension() {
        openTestURL();
        MenuBarElement first = $(MenuBarElement.class).first();
        first.clickItem("TestExtension", "RunMe");
        checkAndCloseAlert();
        first.clickItem("TestExtension", "AddTrigger");
        first.clickItem("TestExtension", "RunMe");
        checkAndCloseAlert();
        checkAndCloseAlert();
        first.clickItem("TestExtension", "RemoveTrigger");
        first.clickItem("TestExtension", "RunMe");
        checkAndCloseAlert();
    }
}

